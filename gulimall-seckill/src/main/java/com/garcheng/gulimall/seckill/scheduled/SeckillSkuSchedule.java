package com.garcheng.gulimall.seckill.scheduled;


import com.garcheng.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SeckillSkuSchedule {

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    public final static String SECKILL_UPLOAD_LOCK = "seckill:upload:lock";

    @Async
//    @Scheduled(cron = "* * 3 * * ?")
    @Scheduled(cron = "*/50 * * * * ?")
    public void uploadSeckillSkuLastest3Day(){
        log.warn("定时任务：【上架秒杀商品】");
        //加分布式锁
        RLock lock = redissonClient.getLock(SECKILL_UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSkuLastest3Day();
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
    }

}
