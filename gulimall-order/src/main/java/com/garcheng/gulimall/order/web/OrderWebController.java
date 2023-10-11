package com.garcheng.gulimall.order.web;

import com.garcheng.gulimall.order.service.OrderService;
import com.garcheng.gulimall.order.vo.ConfirmOrderVo;
import com.garcheng.gulimall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model){
        ConfirmOrderVo confirmOrderData = orderService.confirmOrder();
        model.addAttribute("confirmOrderData",confirmOrderData);
        return "confirm";
    }
}
