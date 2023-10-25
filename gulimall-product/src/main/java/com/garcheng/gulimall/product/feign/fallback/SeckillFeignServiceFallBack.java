package com.garcheng.gulimall.product.feign.fallback;

import com.garcheng.gulimall.common.exception.BaseCodeEnum;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.warn("SeckillFeignService fallback");
        return R.error(BaseCodeEnum.TOO_MANY_REQUEST.getCode(),BaseCodeEnum.TOO_MANY_REQUEST.getMessage());
    }
}
