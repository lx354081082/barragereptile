package com.lx.barragereptile.controller;

import com.alibaba.fastjson.JSON;
import com.lx.barragereptile.thread.DouyuTvCrawlThread;
import com.lx.barragereptile.thread.PandaTvCrawlThread;
import com.lx.barragereptile.pojo.Job;
import com.lx.barragereptile.service.JobService;
import com.lx.barragereptile.service.RedisService;
import com.lx.barragereptile.service.UserService;
import com.lx.barragereptile.util.BarrageConstant;
import com.lx.barragereptile.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import org.hyperic.sigar.Who;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    Sigar sigar;


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
                if (j.getRoomid().indexOf("panda") >= 0) {
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
        log.debug("线程守护-->" + allJob.size() + "个任务正在运行");
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
    @Scheduled(fixedRate = 5000)
    public void count() {
        Map<String, Integer> map = new HashMap<>();
        //所有弹幕
        Integer getval = redisService.getval(BarrageConstant.ALLBARRAGE);
        redisService.clean(BarrageConstant.ALLBARRAGE);
//        log.info("10s弹幕总量统计---->" + getval);
        map.put("allRoom", getval);
        //房间弹幕统计
        List<Job> all = jobService.getAll();
        for (Job j : all) {
            Integer roomVal = redisService.getval(j.getRoomid());
            redisService.clean(j.getRoomid());

//            template.convertAndSend("/topic/count/"+j.getRoomid(),roomVal);
//            log.info(j.getRoomid() + "---->10s弹幕统计---->" + roomVal);
            map.put(j.getRoomid(), roomVal);
        }
        template.convertAndSend("/topic/barrage/count", JSON.toJSONString(map));
    }

    /**
     * 系统状态
     * @throws SigarException
     */
    @Scheduled(fixedRate = 2000)
    public void sys() throws SigarException {
        Map<String, Object> map = new HashMap<>();
        Mem mem = sigar.getMem();
        // 当前内存使用量
        long l = mem.getUsed() / 1024L / 1024L;
        map.put("ram", l);

        Cpu cpu = sigar.getCpu();
        CpuPerc cpuList[] = sigar.getCpuPercList();
        for (int i = 0; i < cpuList.length; i++) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            // 保留两位小数
            nf.setMaximumFractionDigits(2);
            // 如果不需要四舍五入，可以使用RoundingMode.DOWN
            nf.setRoundingMode(RoundingMode.UP);
            String format = nf.format(cpuList[i].getCombined() * 100.0D);
            Double d = Double.parseDouble(format);
            //cpu使用量
            map.put("cpu" + i, d);
        }

        template.convertAndSend("/topic/sys/data", JSON.toJSONString(map));

    }

}