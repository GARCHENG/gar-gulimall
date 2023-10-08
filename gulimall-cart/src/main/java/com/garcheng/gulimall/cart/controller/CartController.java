package com.garcheng.gulimall.cart.controller;

import com.garcheng.gulimall.cart.interceptor.CartInterceptor;
import com.garcheng.gulimall.cart.vo.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CartController {

    @GetMapping("/cart.html")
    public String cartPage(){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println(userInfoTo);
        return "cartList";
    }

    @RequestMapping("/addToCart")
    public String addToCart(){
        return "success";
    }
}
