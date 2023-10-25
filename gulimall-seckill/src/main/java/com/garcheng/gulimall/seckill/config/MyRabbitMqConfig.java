package com.garcheng.gulimall.seckill.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
@Slf4j
@EnableRabbit
public class MyRabbitMqConfig {

//    @Autowired
    RabbitTemplate rabbitTemplate;

    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("arrived mq server：CorrelationData【{}】,Ack【{}】,Cause【{}】", correlationData, ack, cause);
            }
        });

        template.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.error("Message【{}】==>ReplyCode【{}】==>ReplyText【{}】==>Exchange【{}】==>RoutingKey【{}】", message, replyCode, replyText, exchange, routingKey);
            }
        });
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        this.rabbitTemplate = template;
        return template;
    }

//    @Bean
//    public MessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }

    //在MyRabbitMqConfig初始化完成之后再调用此方法
//    @PostConstruct
//    public void initRabbitTemplate() {
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            @Override
//            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//                log.info("arrived mq server：CorrelationData【{}】,Ack【{}】,Cause【{}】", correlationData, ack, cause);
//            }
//        });
//
//        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
//            @Override
//            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//                log.error("Message【{}】==>ReplyCode【{}】==>ReplyText【{}】==>Exchange【{}】==>RoutingKey【{}】", message, replyCode, replyText, exchange, routingKey);
//            }
//        });
//    }
}
