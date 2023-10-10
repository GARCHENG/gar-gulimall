package com.garcheng.gulimall.order.service.impl;

import com.garcheng.gulimall.order.entity.OrderEntity;
import com.garcheng.gulimall.order.entity.OrderSettingEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RabbitListener(queues = {"hello-java-queue"})
public class RabbitService {

    @RabbitHandler
    public void getRabbitMsg1(Message message, OrderEntity content, Channel channel) {
        log.info("reviced mq Message【{}】,内容为【{}】", message, content);
        try {
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag, false);
            log.info("channel Tag【{}】成功签收", deliveryTag);
//            channel.basicNack(long deliveryTag, boolean multiple, boolean requeue);
//            channel.basicReject(long deliveryTag, boolean requeue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitHandler
    public void getRabbitMsg2(Message message, OrderSettingEntity content, Channel channel) {
        log.info("reviced mq Message【{}】,内容为【{}】", message, content);
        try {
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag,false);
//            channel.basicNack(deliveryTag, false, true);
            //void basicReject(long deliveryTag, boolean requeue) throws IOException;
            log.info("channel Tag【{}】成功签收", deliveryTag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
