package com.garcheng.gulimall.order.service.impl;

import com.garcheng.gulimall.order.entity.OrderEntity;
import com.garcheng.gulimall.order.entity.OrderSettingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
//@RabbitListener(queues = {"hello-java-queue"})
public class RabbitService {

//    @RabbitHandler
    public void getRabbitMsg1(Message message, OrderEntity content) {
        log.info("接受到了消息【{}】,内容为【{}】", message, content);
    }

//    @RabbitHandler
    public void getRabbitMsg2(Message message, OrderSettingEntity content) {
        log.info("接受到了消息【{}】,内容为【{}】", message, content);
    }
}
