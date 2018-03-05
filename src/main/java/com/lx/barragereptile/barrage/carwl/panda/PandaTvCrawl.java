package com.lx.barragereptile.barrage.carwl.panda;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lx.barragereptile.pojo.PandaBarrage;
import com.lx.barragereptile.service.PandaBarrageService;
import com.lx.barragereptile.util.DateFormatUtils;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * 进行抓取弹幕任务
 */
@Component
@Scope("prototype")
@Log4j
public class PandaTvCrawl implements Runnable,Cloneable {
    @Autowired
    PandaBarrageService pandaBarrageService;
    //WebSocket
    @Autowired
    SimpMessagingTemplate template;


    private String roomId = "10015";

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    //连接弹幕服务器的必要信息
    private String rid;
    private String appid;
    private String ts;
    private String sign;
    private String authType;

    //与弹幕服务器联系的socket
    private Socket socket;
    //弹幕服务器ip
    private String serverIp;
    //弹幕服务器端口
    private int port;

    /**
     * 初始化一些信息，注意是获取登录弹幕服务器的必要信息
     * @return 返回结果表示是否初始化成功
     */
    private boolean init() {
        String url = "https://riven.panda.tv/chatroom/getinfo?roomid=" + roomId + "&app=1&_caller=panda-pc_web&_=" + System.currentTimeMillis();
        Document document;
        try {
            document = Jsoup.connect(url).get();
            log.info("从[" + url + "]获取登录弹幕服务器的必要信息");
            log.info("登录数据Json串：" + document.body().text());
        } catch (IOException e) {
            log.error("获取登录服务器的必要数据出错", e);
            return false;
        }
        JSONObject jsonObject = JSON.parseObject(document.body().text());

        int errno = jsonObject.getInteger("errno");
        if (errno == 0) {
            JSONObject tempJsonObject = jsonObject.getJSONObject("data");
            rid = String.valueOf(tempJsonObject.getLong("rid"));
            appid = tempJsonObject.getString("appid");
            ts = String.valueOf(tempJsonObject.getLong("ts"));
            sign = tempJsonObject.getString("sign");
            authType = tempJsonObject.getString("authType");

            JSONArray chatAddressList = tempJsonObject.getJSONArray("chat_addr_list");
            log.info("弹幕服务器数据：" + chatAddressList);
            //选第一个服务器登录
            serverIp = chatAddressList.getString(0).split(":",2)[0];
            port = Integer.parseInt(chatAddressList.getString(0).split(":", 2)[1]);
        } else {
            log.error("获取登录弹幕服务器的必要信息出错,程序将退出");
            return false;
        }

        return true;
    }

    /**
     * 与弹幕服务器取得联系,相当于登录弹幕服务器
     */
    private void login() throws IOException {
        socket = new Socket(serverIp,port);
        log.info("登录弹幕服务器:" + serverIp + ":" + port + "成功");
        String msg = "u:" + rid + "@" + appid + "\n" +
                "k:1\n" +
                "t:300\n" +
                "ts:" + ts + "\n" +
                "sign:" + sign + "\n" +
                "authtype:" + authType;
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] b = new byte[]{0x00, 0x06, 0x00, 0x02, 0x00, (byte) msg.length()};
        byteArray.write(b);

        byteArray.write(msg.getBytes("ISO-8859-1"));
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(byteArray.toByteArray());

        b = new byte[]{0x00, 0x06, 0x00, 0x00};
        outputStream.write(b);
    }


    @Override
    public void run() {
        PandaTvMessageHandler pandaTvMessageHandler = null;
        OutputStream outputStream;

        try {
            if (!init()) {
                return;
            }
            login();
            pandaTvMessageHandler = new PandaTvMessageHandler(socket);
            outputStream = socket.getOutputStream();
            long start = System.currentTimeMillis();
            //Thread.interrupted线程结束标记
            while (!Thread.interrupted()) {
                List<String> messages = pandaTvMessageHandler.read();
                for (String msg: messages) {
                    if (msg.equals("")) {
                        continue;
                    }
                    try {
                        JSONObject msgJsonObject = JSON.parseObject(msg);
                        String type = msgJsonObject.getString("type");
                        //发言弹幕type为1
                        if (type.equals("1")) {
                            handlerChatmsg(msgJsonObject);
                        } else if (type.equals("306")) {
                            //TODO 306礼物
                        }
                    } catch (Exception e) {
                        log.error("获取消息内容时出错：" + msg, e);
                    }
                }

                //心跳包
                if (System.currentTimeMillis() - start > 60000) {
                    outputStream.write(new byte[]{0x00, 0x06, 0x00, 0x00});
                    start = System.currentTimeMillis();
                }
            }
        } catch (IOException e) {
            log.error("获取弹幕时出错", e);
        } finally {
            try {
                if (pandaTvMessageHandler != null) {
                    pandaTvMessageHandler.close();
                }
            } catch (IOException e) {
                log.error("调用MessageHandler close()方法时出错");
            }
        }
    }

    /**
     * 弹幕信息处理
     */
    private void handlerChatmsg(JSONObject msgJsonObject) {
        String time = msgJsonObject.getString("time");
        String nickname = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("nickName");
        String rid = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("rid");
        String roomid = msgJsonObject.getJSONObject("data").getJSONObject("to").getString("toroom");
        String content = msgJsonObject.getJSONObject("data").getString("content");
        String level = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("level");
        String identity = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("identity");
        String spidentity = msgJsonObject.getJSONObject("data").getJSONObject("from").getString("sp_identity");
        PandaBarrage pandaBarrage = new PandaBarrage();
        pandaBarrage.setContent(content);
        pandaBarrage.setRid(rid);
        pandaBarrage.setNickname(nickname);
        pandaBarrage.setRoomid(roomid);
        pandaBarrage.setDate(DateFormatUtils.parseUnixTimeToData(time));
        pandaBarrage.setLevel(Integer.parseInt(level));
        pandaBarrageService.save(pandaBarrage);
        template.convertAndSend("/topic/panda/" + roomid, "<a href='"+rid+"'>"+nickname+":</a>"+content);
        log.debug(roomid + "[" + nickname + "]:" + content);
    }

    /**
     * 克隆对象 循环创建该类线程会出现多个线程引用同一对象的问题
     */
    @Override
    public PandaTvCrawl clone() throws CloneNotSupportedException {
        return (PandaTvCrawl) super.clone();
    }
}
