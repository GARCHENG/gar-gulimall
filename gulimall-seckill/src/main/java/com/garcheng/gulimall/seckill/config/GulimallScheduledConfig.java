package com.garcheng.gulimall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Configuration
//TaskSchedulingAutoConfiguration
@EnableScheduling
//TaskExecutionAutoConfiguration
@EnableAsync
public class GulimallScheduledConfig {
}
