package com.lx.barragereptile.controller;

import com.lx.barragereptile.dto.UserDTO;
import com.lx.barragereptile.pojo.PandaBarrage;
import com.lx.barragereptile.service.PandaBarrageService;
import com.lx.barragereptile.service.UserService;
import com.lx.barragereptile.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@RequestMapping("/user")
@Controller
public class UserController {
    @Autowired
    PandaBarrageService pandaBarrageService;
    @Autowired
    UserService userService;

    @GetMapping("")
    String index() {
        return "user";
    }


    @GetMapping("/listByName")
    @ResponseBody
    Object listByName(Integer limit, Integer offset, String username) {
        PageBean<UserDTO> pageBean = userService.selectByName(new PageBean<UserDTO>(), username, offset, limit);

        return pageBean;
    }
}
