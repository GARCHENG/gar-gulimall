package com.garcheng.gulimall.auth.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterController {

    @GetMapping("/reg.html")
    public String registerHome(){
        return "reg";
    }
}
