package com.garcheng.gulimall.order;

import com.alibaba.fastjson.JSON;
import com.garcheng.gulimall.order.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void createExchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        DirectExchange exchange = new DirectExchange("hello-java-exchange",true,false,null);
        amqpAdmin.declareExchange(exchange);
        log.info("交换机【{}】,创建成功....","hello-java-exchange");
    }


    @Test
    public void createQueue(){
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Queue queue = new Queue("hello-java-queue",true,false,false,null);
        amqpAdmin.declareQueue(queue);
        log.info("队列【{}】,创建成功....","hello-java-queue");
    }

    @Test
    public void bingExchangeAndQueue(){
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			Map<String, Object> arguments
        Binding bing = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange","hello.java",null);
        amqpAdmin.declareBinding(bing);
        log.info("绑定【{}】,创建成功....","hello-java-binging");
    }

    @Test
    public void sendMessage(){
        //发送的消息是对象，会使用序列化机制，需实现ser...
        //String exchange, String routingKey, Object message
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setBillContent("hello");
        rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderEntity);
        log.info("消息发送完成：{}", JSON.toJSONString(orderEntity));
    }
}
