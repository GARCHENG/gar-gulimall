package com.garcheng.gulimall.seckill.feign;

import com.garcheng.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @GetMapping("coupon/seckillsession/Lastest3DaySession")
    R getLastest3DaySession();
}
