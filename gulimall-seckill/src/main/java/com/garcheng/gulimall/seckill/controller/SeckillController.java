package com.garcheng.gulimall.seckill.controller;

import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.seckill.service.SeckillService;
import com.garcheng.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/kill")
    public R seckill(@RequestParam("killId") String killId,
                     @RequestParam("key") String key,
                     @RequestParam("num") Integer num){
        String orderSn = seckillService.kill(killId,key,num);
        return R.ok().setData(orderSn);

    }
}
