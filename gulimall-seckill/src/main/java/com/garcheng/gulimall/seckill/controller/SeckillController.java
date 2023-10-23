package com.garcheng.gulimall.seckill.controller;

import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.seckill.service.SeckillService;
import com.garcheng.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SeckillController {

    @Autowired
    SeckillService seckillService;

    @GetMapping("/currentSeckillProduct")
    @ResponseBody
    public R getCurrentSeckillProduct(){
        List<SeckillSkuRedisTo> data = seckillService.getCurrentSeckillProduct();
        return R.ok().setData(data);
    }
}
