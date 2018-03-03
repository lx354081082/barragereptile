package com.lx.barragereptile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userdetail")
public class UserDetailController {

    @GetMapping("{where}/{id}")
    String index(@PathVariable("where") String where, @PathVariable("id") Integer id) {
        System.out.println(where + id);
        return "userdetail";
    }
    @GetMapping("")
    String index() {
        return "userdetail";
    }

}
