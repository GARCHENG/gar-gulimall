package com.garcheng.gulimall.order.controller;

import com.alipay.api.AlipayApiException;
import com.garcheng.gulimall.order.config.AlipayTemplate;
import com.garcheng.gulimall.order.service.OrderService;
import com.garcheng.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    @ResponseBody
    @GetMapping(value = "payOrder",produces = "text/html")
    public String aliPayOrder(String orderSn) throws AlipayApiException {
        PayVo payVo = orderService.getOrderPay(orderSn);
        return alipayTemplate.pay(payVo);

    }
}
