package com.garcheng.gulimall.seckill.service;

import com.garcheng.gulimall.seckill.to.SeckillSkuRedisTo;

import java.util.List;

public interface SeckillService {
    void uploadSeckillSkuLastest3Day();

    List<SeckillSkuRedisTo> getCurrentSeckillProduct();
}
