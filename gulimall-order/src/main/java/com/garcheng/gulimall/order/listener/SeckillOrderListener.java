package com.garcheng.gulimall.order.listener;

import com.garcheng.gulimall.common.to.mq.SeckillOrderTo;
import com.garcheng.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RabbitListener(queues = "order.seckill.order.queue")
public class SeckillOrderListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void handSeckillOrder(SeckillOrderTo seckillOrder ,Message message , Channel channel) throws IOException {
        try {
            log.warn("接受到【order.seckill.order.queue】消息");
            orderService.handleSeckillOrder(seckillOrder);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
