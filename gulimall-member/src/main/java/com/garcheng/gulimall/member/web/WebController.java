package com.garcheng.gulimall.member.web;

import com.alibaba.fastjson.JSON;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebController {

    @Autowired
    OrderFeignService orderFeignService;

    @GetMapping("memberOrder.html")
    public String orderList(String pageNum, Model model){

        Map<String, Object> param = new HashMap<>();
        param.put("page",pageNum);
        R r = orderFeignService.listWithItems(param);
        System.out.println(JSON.toJSONString(r));
        model.addAttribute("orders",r);
        return "orderList";
    }
}
