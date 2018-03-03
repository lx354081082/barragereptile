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


    /**
     * 模糊查用户
     * @param limit 步长
     * @param offset 其实角标
     * @param username 用户名
     * @param where 平台(douyu,panda)
     */
    @GetMapping("/listByName")
    @ResponseBody
    Object listByName(Integer limit, Integer offset, String username,String where) {
        PageBean<UserDTO> pageBean = new PageBean<>();
        if (UserDTO.DOUYU.equals(where)) {
            pageBean = userService.selectDouyuByName(pageBean, username, offset, limit);
        } else if (UserDTO.PANDA.equals(where)) {
            pageBean = userService.selectPandaByName(pageBean, username, offset, limit);
        }
        return pageBean;
    }
}
