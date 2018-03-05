package com.lx.barragereptile.controller;

import com.lx.barragereptile.dto.UserDetailDTO;
import com.lx.barragereptile.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userdetail")
public class UserDetailController {
    @Autowired
    UserService userService;

    @GetMapping("{where}/{id}")
    String index(@PathVariable("where") String where, @PathVariable("id") String id, Model model) {
        UserDetailDTO userDetailDTO = userService.selectByWhereAndUid(where, id);
        model.addAttribute("user", userDetailDTO);
        return "userdetail";
    }
    @GetMapping("")
    String index(Model model) {
        model.addAttribute("user", new UserDetailDTO());
        return "userdetail";
    }

}
