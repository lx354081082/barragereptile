package com.lx.barragereptile.controller;

import com.lx.barragereptile.thread.DouyuTvCrawlThread;
import com.lx.barragereptile.thread.PandaTvCrawlThread;
import com.lx.barragereptile.pojo.Job;
import com.lx.barragereptile.service.JobService;
import com.lx.barragereptile.service.RedisService;
import com.lx.barragereptile.service.UserService;
import com.lx.barragereptile.util.BarrageConstant;
import com.lx.barragereptile.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class WebSocketController {
    @Autowired
    SimpMessagingTemplate template;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    JobService jobService;
    @Autowired
    PandaTvCrawlThread pandaTvCrawlThread;
    @Autowired
    DouyuTvCrawlThread douyuTvCrawlThread;
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;


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
                    PandaTvCrawlThread clonePandaTvCrawlThread = pandaTvCrawlThread.clone();
                    clonePandaTvCrawlThread.setRoomId(j.getRoomid().substring("panda".length()));
                    Thread thread = new Thread(clonePandaTvCrawlThread, j.getRoomid());
                    thread.start();
                    j.setThreadid(thread.getId());
                    jobService.update(j);
                } else if (j.getRoomid().indexOf("douyu") >= 0) {
                    //todo
                    DouyuTvCrawlThread cloneDouyuTvCrawlThread = douyuTvCrawlThread.clone();
                    cloneDouyuTvCrawlThread.setRoomId(j.getRoomid().substring("douyu".length()));
                    Thread thread = new Thread(cloneDouyuTvCrawlThread, j.getRoomid());
                    thread.start();
                    j.setThreadid(thread.getId());
                    jobService.update(j);
                }
                log.info("创建任务线程-->" + j.getRoomid());
            }
        }
        log.debug("线程守护-->"+allJob.size()+"个任务正在运行");
    }

    /**
     * 计划任务(用户表刷新)
     * To缓存实现
     */
//    @Scheduled(fixedRate = 6000000)
//    public void barrageToUser() {
//        long start = System.currentTimeMillis();
//        int i = userService.barrageToUser();
//        long end = System.currentTimeMillis();
//
//        log.info("用户详情更新,处理弹幕数据"+i+"条,用时:"+(end-start)+"ms");
//    }

    /**
     * 统计弹幕
     */
    @Scheduled(fixedRate = 10000)
    public void count() {
        //所有弹幕
        Integer getval = redisService.getval(BarrageConstant.ALLBARRAGE);
        redisService.clean(BarrageConstant.ALLBARRAGE);
//        log.info("10s弹幕总量统计---->" + getval);
        template.convertAndSend("/topic/allBarrage",getval);
        //房间弹幕统计
        List<Job> all = jobService.getAll();
        for (Job j : all) {
            Integer roomVal = redisService.getval(j.getRoomid());
            redisService.clean(j.getRoomid());
//            log.info(j.getRoomid() + "---->10s弹幕统计---->" + roomVal);
        }
    }
}
