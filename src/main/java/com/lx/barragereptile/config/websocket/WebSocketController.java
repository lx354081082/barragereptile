package com.lx.barragereptile.config.websocket;

import com.lx.barragereptile.barrage.handler.douyu.DouyuTvCrawl;
import com.lx.barragereptile.barrage.handler.panda.PandaTvCrawl;
import com.lx.barragereptile.pojo.Job;
import com.lx.barragereptile.service.JobService;
import com.lx.barragereptile.util.ThreadUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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


    @MessageMapping("/send")
    @SendTo("/topic/send")
    public SocketMessage send(SocketMessage message) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.date = df.format(new Date());
        return message;
    }

    @Scheduled(fixedRate = 60000)
    public void callback() throws Exception {
        List<Job> allJob = jobService.getAll();
        for (Job j : allJob) {
            //通过线程id 获取线程name 没有返回null
            String threadName = ThreadUtils.isRunnableByThreadId(j.getThreadid());

            //无此线程就创建相应线程
            if (threadName != null && threadName.equals(j.getRoomid())) {
                log.info("线程守护-->" + j.getRoomid() + "正在运行");
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
                }log.info("线程守护-->" + j.getRoomid() + "创建成功");
            }
        }
    }
}
