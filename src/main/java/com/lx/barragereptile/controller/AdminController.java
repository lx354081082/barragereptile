package com.lx.barragereptile.controller;

import com.lx.barragereptile.pojo.Job;
import com.lx.barragereptile.service.JobService;
import com.lx.barragereptile.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    JobService jobService;

    @GetMapping("/joblist")
    Object jobslist() {
        List<Job> all = jobService.getAll();
        return all;
    }

    @GetMapping("/stop/{roomid}")
    Object stop(@PathVariable("roomid") String rommid) {
        boolean b = ThreadUtils.interruptThread(rommid);
        if (b) {
            jobService.delByRoomid(rommid);
            return "删除成功";
        }
        return "结束异常";
    }
}
