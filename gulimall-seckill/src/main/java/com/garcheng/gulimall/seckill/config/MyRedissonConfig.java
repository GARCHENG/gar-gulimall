package com.garcheng.gulimall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://192.168.137.158:6379");
//                .setAddress("redis://gulimall.com:6379");

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}