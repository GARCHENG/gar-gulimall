package com.garcheng.gulimall.product.feign;

import com.garcheng.gulimall.common.to.SkuReductTo;
import com.garcheng.gulimall.common.to.SpuBoundsTo;
import com.garcheng.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @RequestMapping("/coupon/spubounds/save")
    public R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @RequestMapping("/coupon/skufullreduction/reduction/save")
    public R saveSkuReduction(@RequestBody SkuReductTo skuReductTo);
}
