package com.lx.barragereptile.controller;

import com.lx.barragereptile.dto.BarrageDTO;
import com.lx.barragereptile.dto.UserBarrageDTO;
import com.lx.barragereptile.service.BarrageService;
import com.lx.barragereptile.util.PageBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequestMapping("/barrage")
@ResponseBody
public class BarrageController {
    @Autowired
    BarrageService barrageService;

    @GetMapping("/find")
    Object find(String where,String roomid,Integer offset, Integer limit) {
        PageBean<BarrageDTO> byRoomid = barrageService.findByRoomid(where, roomid, offset, limit);
        return byRoomid;
    }

}
