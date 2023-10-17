package com.garcheng.gulimall.order.config;

import com.garcheng.gulimall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMqConfig {

    @Bean
    public Queue orderDelayQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Map<String, Object> args = new HashMap<>();
        //x-dead-letter-exchange: order-event-exchange  x-dead-letter-routing-key: order.release.order  x-message-ttl: 60000
        args.put("x-dead-letter-exchange", "order-event-exchange");
        args.put("x-dead-letter-routing-key", "order.release.order");
        args.put("x-message-ttl", 60000);
        return new Queue("order.delay.queue", true, false, false, args);
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue("order.release.order.queue", true, false, false, null);
    }

    @Bean
    public Exchange orderEventExchange(){
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        TopicExchange topicExchange = new TopicExchange("order-event-exchange",true,false,null);
        return topicExchange;
    }

    @Bean
    public Binding orderCreateOrder(){
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			Map<String, Object> arguments
        Binding binding = new Binding("order.delay.queue", Binding.DestinationType.QUEUE,"order-event-exchange","order.create.order",null);
        return binding;
    }

    @Bean
    public Binding orderReleaseOrder(){
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			Map<String, Object> arguments
        Binding binding = new Binding("order.release.order.queue", Binding.DestinationType.QUEUE,"order-event-exchange","order.release.order",null);
        return binding;
    }

    @RabbitListener(queues = {"order.release.order.queue"})
    public void listener(OrderEntity orderEntity, Message message , Channel channel) throws IOException {
        System.out.println(orderEntity);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }




}
