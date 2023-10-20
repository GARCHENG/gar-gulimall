package com.garcheng.gulimall.seckill.scheduled;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
//TaskSchedulingAutoConfiguration
@EnableScheduling
//TaskExecutionAutoConfiguration
@EnableAsync
public class HelloSchedule {


//    @Async
//    @Scheduled(cron = "* * * * * ?")
//    public void hello()  {
//        System.out.println("scheduling....");
//    }

}
