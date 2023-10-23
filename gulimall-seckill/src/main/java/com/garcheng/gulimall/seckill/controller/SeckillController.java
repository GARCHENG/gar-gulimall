package com.garcheng.gulimall.seckill.controller;

import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.seckill.service.SeckillService;
import com.garcheng.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeckillController {

    @Autowired
    SeckillService seckillService;

    @GetMapping("/currentSeckillProduct")
    public R getCurrentSeckillProduct(){
        List<SeckillSkuRedisTo> data = seckillService.getCurrentSeckillProduct();
        return R.ok().setData(data);
    }

    @GetMapping("sku/seckillinfo/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
        SeckillSkuRedisTo to = seckillService.getSeckillInfoBySkuId(skuId);
        return R.ok().setData(to);
    }
}
