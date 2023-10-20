package com.garcheng.gulimall.seckill.scheduled;


import com.garcheng.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SeckillSkuSchedule {

    @Autowired
    SeckillService seckillService;

    @Async
    @Scheduled(cron = "* * 3 * * ?")
    public void uploadSeckillSkuLastest3Day(){
        seckillService.uploadSeckillSkuLastest3Day();
    }

}
