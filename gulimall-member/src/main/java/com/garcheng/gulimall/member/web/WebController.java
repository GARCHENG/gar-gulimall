package com.garcheng.gulimall.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("orderList.html")
    public String orderList(){
        return "orderList";
    }
}
