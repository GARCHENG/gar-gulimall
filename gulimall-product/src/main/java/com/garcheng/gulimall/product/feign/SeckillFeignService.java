package com.garcheng.gulimall.product.feign;

import com.garcheng.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-seckill")
public interface SeckillFeignService {

    @GetMapping("sku/seckillinfo/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
