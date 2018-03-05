package com.lx.barragereptile.controller;

import com.lx.barragereptile.dto.UserBarrageDTO;
import com.lx.barragereptile.dto.UserDTO;
import com.lx.barragereptile.pojo.PandaBarrage;
import com.lx.barragereptile.service.BarrageService;
import com.lx.barragereptile.service.PandaBarrageService;
import com.lx.barragereptile.service.UserService;
import com.lx.barragereptile.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@RequestMapping("/user")
@Controller
public class UserController {
    @Autowired
    PandaBarrageService pandaBarrageService;
    @Autowired
    UserService userService;
    @Autowired
    BarrageService barrageService;

    /**
     * 模糊查用户
     * @param limit 步长
     * @param offset 起始角标
     * @param username 用户名
     * @param where 平台(douyu,panda)
     */
    @GetMapping("/listByName")
    @ResponseBody
    Object listByName(Integer limit, Integer offset, String username,String where) {

        PageBean<UserDTO> pageBean = userService.selectUserByWhere(where, username, offset, limit);

        return pageBean;
    }

    /**
     * 查用户发的弹幕
     *
     * @param limit  步长
     * @param offset 起始角标
     */
    @GetMapping("/barrage/{where}/{uid}")
    @ResponseBody
    Object fingBarrage(@PathVariable("where") String where, @PathVariable("uid") String uid, Integer offset, Integer limit) {
        PageBean<UserBarrageDTO> pageBean = barrageService.selectByUid(where, uid, offset, limit);
        return pageBean;
    }
}
