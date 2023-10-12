package com.garcheng.gulimall.cart.controller;

import com.garcheng.gulimall.cart.interceptor.CartInterceptor;
import com.garcheng.gulimall.cart.service.CartService;
import com.garcheng.gulimall.cart.vo.Cart;
import com.garcheng.gulimall.cart.vo.CartItem;
import com.garcheng.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cart.html")
    public String cartPage(Model model) throws ExecutionException, InterruptedException {
        Cart cart = cartService.getCart();
        model.addAttribute("cartList",cart);
        return "cartList";
    }

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.gulimall.com/cart.html";
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

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num){
        cartService.countItem(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/current/cartItems")
    @ResponseBody
    public List<CartItem> getCurrentCartItems() throws ExecutionException, InterruptedException {
        return cartService.getCurrentCartItems();
    }
}
