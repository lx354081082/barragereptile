package com.lx.barragereptile.barrage.carwl.douyu;

import com.lx.barragereptile.barrage.carwl.douyu.msg.DyMessage;
import com.lx.barragereptile.barrage.carwl.douyu.msg.MsgView;
import com.lx.barragereptile.pojo.DouyuBarrage;
import com.lx.barragereptile.service.DouyuBarrageService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

/**
 * 进行抓取弹幕任务
 */
@Log4j
@Component
@Scope("prototype")
public class DouyuTvCrawl implements Runnable,Cloneable {
    //WebSocket
    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    DouyuBarrageService douyuBarrageService;

    //设置需要访问的房间ID信息
    private String roomId = "24422";
    //弹幕池分组号，海量模式使用-9999
    private Integer groupId = -9999;
    //第三方弹幕协议服务器地址
    private static final String hostName = "openbarrage.douyutv.com";
    //第三方弹幕协议服务器端口
    private static final int port = 8601;
    //设置字节获取buffer的最大值
    private static final int MAX_BUFFER_LENGTH = 4096;

    //与弹幕服务器联系的socket
    private Socket socket;
    private BufferedOutputStream ots;
    private BufferedInputStream ins;

    /**
     * 设置房间号方法
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * 初始化方法
     */
    private void init() {
        //建立服务器链接socket
        connectServer();
        //登录房间
        loginRoom(roomId);
        //加入指定的弹幕池
        joinGroup(roomId, groupId);
    }

    /**
     * 加入指定的弹幕池
     */
    private void joinGroup(String roomId, int groupId) {
        //获取弹幕服务器加弹幕池请求数据包
        byte[] joinGroupRequest = DyMessage.getJoinGroupRequest(roomId, groupId);

        try {
            ots.write(joinGroupRequest, 0, joinGroupRequest.length);
            ots.flush();
            log.info("加入弹幕池"+groupId+"成功");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("加入弹幕池"+groupId+"失败");
        }
    }

    /**
     * 登录服务器
     */
    private void loginRoom(String roomId) {
        byte[] loginRequestData = DyMessage.getLoginRequestData(roomId);
        try {
            //发送登陆请求数据包给弹幕服务器
            ots.write(loginRequestData, 0, loginRequestData.length);
            ots.flush();

            //初始化弹幕服务器返回值读取包大小
            byte[] recvByte = new byte[MAX_BUFFER_LENGTH];
            //获取弹幕服务器返回值
            ins.read(recvByte, 0, recvByte.length);

            if (DyMessage.parseLoginRespond(recvByte)) {
                log.info("登录房间号" + roomId + "弹幕服务器,成功");
            } else {
                log.error("登录服务器失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 建立服务器链接
     */
    private void connectServer() {
        try {
            //获取弹幕服务器访问host
            String host = InetAddress.getByName(hostName).getHostAddress();
            //建立socke连接
            socket = new Socket(host, port);
            //设置socket输入及输出
            ots = new BufferedOutputStream(socket.getOutputStream());
            ins= new BufferedInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器心跳包
     */
    public void keepAlive() throws IOException {
        //获取与弹幕服务器保持心跳的请求数据包
        byte[] keepAliveRequest = DyMessage.getKeepAliveData((int)(System.currentTimeMillis() / 1000));
            //向弹幕服务器发送心跳请求数据包
            ots.write(keepAliveRequest, 0, keepAliveRequest.length);
            ots.flush();
            log.info("心跳包发送成功");

    }

    public void getServerMsg() {
        //初始化获取弹幕服务器返回信息包大小
        byte[] recvByte = new byte[MAX_BUFFER_LENGTH];
        //定义服务器返回信息的字符串
        String dataStr;
        try {
            //读取服务器返回信息，并获取返回信息的整体字节长度
            int recvLen = ins.read(recvByte, 0, recvByte.length);
            if (recvLen <= 24) {
                return;
            }

            //根据实际获取的字节数初始化返回信息内容长度
            byte[] realBuf = new byte[recvLen];
            //按照实际获取的字节长度读取返回信息
            System.arraycopy(recvByte, 0, realBuf, 0, recvLen);
            //根据TCP协议获取返回信息中的字符串信息
            dataStr = new String(realBuf, 12, realBuf.length - 12);

            //循环处理socekt黏包情况
            while(dataStr.lastIndexOf("type@=") > 5){
                //对黏包中最后一个数据包进行解析
                MsgView msgView = new MsgView(StringUtils.substring(dataStr, dataStr.lastIndexOf("type@=")));
                //分析该包的数据类型，以及根据需要进行业务操作
                parseServerMsg(msgView.getMessageList());
                //处理黏包中的剩余部分
                dataStr = StringUtils.substring(dataStr, 0, dataStr.lastIndexOf("type@=") - 12);
            }
            //对单一数据包进行解析
            MsgView msgView = new MsgView(StringUtils.substring(dataStr, dataStr.lastIndexOf("type@=")));
            //分析该包的数据类型，以及根据需要进行业务操作
            parseServerMsg(msgView.getMessageList());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 解析从服务器接受的信息，并根据需要订制业务需求
     */
    private void parseServerMsg(Map<String, Object> msg){
        if(msg.get("type") != null){

            //服务器反馈错误信息
            if(msg.get("type").equals("error")){
                log.debug(msg.toString());
            }

            /***@TODO 根据业务需求来处理获取到的所有弹幕及礼物信息***********/

            //判断消息类型
            if(msg.get("type").equals("chatmsg")){//弹幕消息
                handlerChatmsg(msg);
                log.debug("弹幕消息===>" + msg.toString());

            } else if(msg.get("type").equals("dgb")){//赠送礼物信息
                log.debug("礼物消息===>" + msg.toString());
            } else {
                log.debug("其他消息===>" + msg.toString());
            }

        }
    }

    /**
     * 弹幕信息处理
     */
    private void handlerChatmsg(Map<String, Object> msg) {
        try {
            //用户名
            String name = (String) msg.get("nn");
            //用户id
            String uid = (String) msg.get("uid");
            //用户等级
            String level = (String) msg.get("level");
            //发言时间戳
//        String cst = (String) msg.get("cst");
            //房间id
            String rid = (String) msg.get("rid");
            //弹幕信息
            String txt = null;
            try {
                txt = (String) msg.get("txt");
            } catch (Exception e) {
                txt = (String) msg.get("txt").toString();
                txt = txt.substring(1, txt.length() - 2);
            }
            String brid = (String) msg.get("brid");

            DouyuBarrage douyuBarrage = new DouyuBarrage();
            douyuBarrage.setUid(uid);
            douyuBarrage.setUname(name);
            douyuBarrage.setRoomid(rid);
            douyuBarrage.setDate(new Date());
            douyuBarrage.setLevel(Integer.parseInt(level));
            douyuBarrage.setTxt(txt);

            douyuBarrageService.save(douyuBarrage);
            //websockfas弹幕消息
            template.convertAndSend("/topic/douyu/" + brid, "<a href='"+uid+"'>"+name+":</a>"+txt);
        } catch (Exception e) {
            log.error("弹幕处理异常==>" +e.getMessage()+"==>"+ msg.toString());
        }
    }

    /**
     * 运行主流程
     */
    @Override
    public void run() {
        //初始化连接弹幕服务器
        init();
        long start = System.currentTimeMillis();
        //心跳包线程内部类
        Thread keep = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        keepAlive();
                    } catch (IOException e) {
                        log.error("心跳包发生异常结束当前进程");
                        return;
                    }
                    try {
                        Thread.sleep(45000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        },roomId+"心跳包");
        keep.start();
        //判断当前进程结束标记 and 心跳包进程是否在运行
        while (!Thread.interrupted() && keep.isAlive()) {

            //处理请求
            getServerMsg();
        }
        //结束心跳包线程
        keep.interrupt();
        log.warn("结束进程");
    }

    /**
     * 克隆对象 循环创建该类线程会出现多个线程引用同一对象的问题
     */
    @Override
    public DouyuTvCrawl clone() throws CloneNotSupportedException {
        return (DouyuTvCrawl) super.clone();
    }
}
