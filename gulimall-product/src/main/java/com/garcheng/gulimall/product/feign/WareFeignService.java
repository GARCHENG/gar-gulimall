package com.garcheng.gulimall.product.feign;

import com.garcheng.gulimall.common.to.SkuStockTo;
import com.garcheng.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @RequestMapping("ware/waresku/hasstock")
    R getSkusStock(@RequestBody List<Long> skuIds);
}
