package com.garcheng.gulimall.order.controller;

import com.alibaba.fastjson.JSON;
import com.garcheng.gulimall.order.entity.OrderEntity;
import com.garcheng.gulimall.order.entity.OrderSettingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMessage")
    public String sendMessage(){
        for (int i =0;i<10;i++){
            if (i%2==0){
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setBillContent("hello");
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderEntity);
                log.info("消息发送完成：{}", JSON.toJSONString(orderEntity));
            }else {
                OrderSettingEntity orderSettingEntity = new OrderSettingEntity();
                orderSettingEntity.setMemberLevel(1);
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderSettingEntity);
                log.info("消息发送完成：{}", JSON.toJSONString(orderSettingEntity));
            }
        }

        return "ok";
    }
}
