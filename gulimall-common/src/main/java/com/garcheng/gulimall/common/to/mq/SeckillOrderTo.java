package com.garcheng.gulimall.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillOrderTo {

    private String orderSn;

    private Integer count;

    private Long skuId;

    private Long sessionId;

    private BigDecimal seckillPrice;

    private Long memberId;
}
