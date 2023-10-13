package com.garcheng.gulimall.order.web;

import com.garcheng.gulimall.order.service.OrderService;
import com.garcheng.gulimall.order.vo.ConfirmOrderVo;
import com.garcheng.gulimall.order.vo.OrderItemVo;
import com.garcheng.gulimall.order.vo.SubmitOrderResponseVo;
import com.garcheng.gulimall.order.vo.SubmitOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        ConfirmOrderVo confirmOrderData = orderService.confirmOrder();
        model.addAttribute("confirmOrderData",confirmOrderData);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(SubmitOrderVo submitOrderVo){

        SubmitOrderResponseVo response = orderService.submitOrder(submitOrderVo);
        if (response.getCode() == 0){
            return "redirect:http://order.gulimall.com/pay";
        }else {
            return "redirect:/toTrade";
        }
    }
}
