package com.lx.barragereptile.controller;

import com.lx.barragereptile.pojo.Job;
import com.lx.barragereptile.service.JobService;
import com.lx.barragereptile.util.ThreadUtils;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    JobService jobService;
    @Autowired
    Sigar sigar;

    @GetMapping("/joblist")
    Object jobslist() {
        List<Job> all = jobService.getAll();
        return all;
    }

    @GetMapping("/sys/data")
    Object sysData() {
        Mem mem = null;
        try {
            mem = sigar.getMem();
            Cpu cpu = sigar.getCpu();
            // 内存总量
            long total = mem.getTotal() / 1024L / 1024L;
            CpuPerc[] cpuList = sigar.getCpuPercList();
            // cpu线程数
            int size = cpuList.length;
            Map<String, Object> map = new HashMap<>();
            map.put("ram", total);
            map.put("size", size);
            return map;
        } catch (Exception e) {
        }
        return null;
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
