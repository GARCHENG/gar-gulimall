package com.garcheng.gulimall.cart.controller;

import com.garcheng.gulimall.cart.interceptor.CartInterceptor;
import com.garcheng.gulimall.cart.service.CartService;
import com.garcheng.gulimall.cart.vo.CartItem;
import com.garcheng.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cart.html")
    public String cartPage(){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println(userInfoTo);
        return "cartList";
    }

    @RequestMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        attributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess";
    }

    @GetMapping("/addToCartSuccess")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,
                                       Model model){

        CartItem cartItem = cartService.getCartItemBySkuId(skuId);
        model.addAttribute("item",cartItem);
        return "success";

    }
}
