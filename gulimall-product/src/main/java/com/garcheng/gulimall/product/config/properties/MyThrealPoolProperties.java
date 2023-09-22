package com.garcheng.gulimall.product.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "gulimall.thread")
//@Component
@Data
public class MyThrealPoolProperties {

    private Integer corePoolSize;

    private Integer maximumPoolSize;

    private Integer keepAliveTime;

    private Integer queueSize;
}
