package com.lx.barragereptile.controller;

import com.lx.barragereptile.thread.DouyuTvCrawlThread;
import com.lx.barragereptile.thread.PandaTvCrawlThread;
import com.lx.barragereptile.pojo.Job;
import com.lx.barragereptile.service.BarrageService;
import com.lx.barragereptile.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class JobController {
    @Autowired
    PandaTvCrawlThread pandaTvCrawlThread;
    @Autowired
    DouyuTvCrawlThread douyuTvCrawlThread;
    @Autowired
    JobService jobService;
    @Autowired
    BarrageService barrageService;


    /**
     * 创建任务
     */
    @GetMapping("/panda/{roomid}")
    Object roomid(@PathVariable("roomid") String roomid) {
        pandaTvCrawlThread.setRoomId(roomid);
        //判断是否有次线程
        if (jobService.isHave("panda" + roomid)) {
            return true;
        }
        //创建线程 线程名字为房间id
        Thread thread = new Thread(pandaTvCrawlThread, "panda" + roomid);
        thread.start();

        Job job = new Job();
        job.setRoomid("panda" + roomid);
        job.setThreadid(thread.getId());
        jobService.save(job);

        return true;
    }

    /**
     * 创建任务
     */
    @GetMapping("/douyu/{roomid}")
    Object rid(@PathVariable("roomid") String roomid) {
        douyuTvCrawlThread.setRoomId(roomid);
        //判断是否有次线程
        if (jobService.isHave("douyu" + roomid)) {
            return true;
        }

        Thread thread = new Thread(douyuTvCrawlThread, "douyu" + roomid);
        thread.start();

        Job job = new Job();
        job.setRoomid("douyu" + roomid);
        job.setThreadid(thread.getId());
        jobService.save(job);

        return true;
    }


//    @GetMapping("/barrage/find")
//    Object fing(String where, Integer roomid, String who) {
//        barrageService.selectBarrage(where, roomid, who);
//        return "";
//    }
}
