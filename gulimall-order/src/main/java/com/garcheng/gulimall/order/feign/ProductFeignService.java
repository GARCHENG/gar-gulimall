package com.garcheng.gulimall.order.feign;

import com.garcheng.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    @GetMapping("product/spuinfo/spuInfo/{skuId}")
    R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);
}
