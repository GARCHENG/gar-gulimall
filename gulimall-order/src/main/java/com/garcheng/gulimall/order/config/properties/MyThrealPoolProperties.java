package com.garcheng.gulimall.order.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "gulimall.thread")
//@Component
@Data
public class MyThrealPoolProperties {

    private Integer corePoolSize;

    private Integer maximumPoolSize;

    private Integer keepAliveTime;

    private Integer queueSize;
}
