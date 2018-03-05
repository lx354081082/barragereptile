package com.lx.barragereptile.controller;

import com.lx.barragereptile.barrage.carwl.douyu.DouyuTvCrawl;
import com.lx.barragereptile.barrage.carwl.panda.PandaTvCrawl;
import com.lx.barragereptile.dto.UserBarrageDTO;
import com.lx.barragereptile.pojo.Job;
import com.lx.barragereptile.service.BarrageService;
import com.lx.barragereptile.service.JobService;
import com.lx.barragereptile.util.PageBean;
import com.lx.barragereptile.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BarrageController {
    @Autowired
    PandaTvCrawl pandaTvCrawl;
    @Autowired
    DouyuTvCrawl douyuTvCrawl;
    @Autowired
    JobService jobService;
    @Autowired
    BarrageService barrageService;

    @GetMapping("index")
    String index() {
        return "index";
    }


    /**
     * 创建任务
     */
    @GetMapping("/panda/{roomid}")
    @ResponseBody
    Object roomid(@PathVariable("roomid") String roomid) {
        pandaTvCrawl.setRoomId(roomid);
        //判断是否有次线程
        if (jobService.isHave("panda" + roomid)) {
            return true;
        }
        //创建线程 线程名字为房间id
        Thread thread = new Thread(pandaTvCrawl, "panda" + roomid);
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
    @ResponseBody
    Object rid(@PathVariable("roomid") String roomid) {
        douyuTvCrawl.setRoomId(roomid);
        //判断是否有次线程
        if (jobService.isHave("douyu" + roomid)) {
            return true;
        }

        Thread thread = new Thread(douyuTvCrawl, "douyu" + roomid);
        thread.start();

        Job job = new Job();
        job.setRoomid("douyu" + roomid);
        job.setThreadid(thread.getId());
        jobService.save(job);

        return true;
    }

    @GetMapping("/stop/{roomid}")
    @ResponseBody
    Object stop(@PathVariable("roomid") String roomid) {
        Long threadId = jobService.getThreadIdByRoomId(roomid);
        if (threadId != null) {
            return ThreadUtils.interruptThreadByThreadId(threadId);
        }
        return false;
    }

}
