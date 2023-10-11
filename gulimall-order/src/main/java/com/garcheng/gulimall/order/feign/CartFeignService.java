package com.garcheng.gulimall.order.feign;

import com.garcheng.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.concurrent.ExecutionException;

@FeignClient("gulimall-cart")
public interface CartFeignService {

    @GetMapping("/current/cartItems")
    List<OrderItemVo> getCurrentCartItems() throws ExecutionException, InterruptedException;
}
