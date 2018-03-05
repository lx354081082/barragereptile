package com.lx.barragereptile.controller;

import com.lx.barragereptile.barrage.carwl.douyu.DouyuTvCrawl;
import com.lx.barragereptile.barrage.carwl.panda.PandaTvCrawl;
import com.lx.barragereptile.pojo.Job;
import com.lx.barragereptile.service.JobService;
import com.lx.barragereptile.service.UserService;
import com.lx.barragereptile.util.ThreadUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * webSocket
 * 定时任务
 */
@Controller
@EnableScheduling
@Log4j
public class WebSocketController {

    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    JobService jobService;
    @Autowired
    PandaTvCrawl pandaTvCrawl;
    @Autowired
    DouyuTvCrawl douyuTvCrawl;
    @Autowired
    UserService userService;


//    @MessageMapping("/send")
//    @SendTo("/topic/send")
//    public SocketMessage send(SocketMessage message) throws Exception {
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        message.date = df.format(new Date());
//        return message;
//    }

    /**
     * 计划任务 线程守护
     */
    @Scheduled(fixedRate = 60000)
    public void callback() throws CloneNotSupportedException {
        List<Job> allJob = jobService.getAll();
        for (Job j : allJob) {
            //通过线程id 获取线程name 没有返回null
            String threadName = ThreadUtils.isRunnableByThreadId(j.getThreadid());

            //无此线程就创建相应线程
            if (threadName != null && threadName.equals(j.getRoomid())) {
                continue;
            } else {
                //克隆对象 建立线程
                if (j.getRoomid().indexOf("panda")>=0){
                    PandaTvCrawl clonePandaTvCrawl = pandaTvCrawl.clone();
                    clonePandaTvCrawl.setRoomId(j.getRoomid().substring("panda".length()));
                    Thread thread = new Thread(clonePandaTvCrawl, j.getRoomid());
                    thread.start();
                    j.setThreadid(thread.getId());
                    jobService.update(j);
                } else if (j.getRoomid().indexOf("douyu") >= 0) {
                    //todo
                    DouyuTvCrawl cloneDouyuTvCrawl = douyuTvCrawl.clone();
                    cloneDouyuTvCrawl.setRoomId(j.getRoomid().substring("douyu".length()));
                    Thread thread = new Thread(cloneDouyuTvCrawl, j.getRoomid());
                    thread.start();
                    j.setThreadid(thread.getId());
                    jobService.update(j);
                }
            }
        }
        log.info("线程守护-->"+allJob.size()+"个任务正在运行");
    }

    /**
     * 计划任务(用户表刷新)
     */
    @Scheduled(fixedRate = 60000000)
    public void barrageToUser() {
        long start = System.currentTimeMillis();
        int i = userService.barrageToUser();
        long end = System.currentTimeMillis();

        log.info("用户详情更新,处理弹幕数据"+i+"条,用时:"+(end-start)+"ms");
    }
}
