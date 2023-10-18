package com.garcheng.gulimall.ware.feign;

import com.garcheng.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-order")
public interface OrderFeignService {

    @GetMapping("order/order/status/{orderSn}")
    R getOrderByOrderSn(@PathVariable("orderSn") String orderSn);
}
