package com.garcheng.gulimall.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductTo {

    private Long skuId;

    private BigDecimal discount;

    private Integer countStatus;

    private Integer fullCount;

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private Integer priceStatus;

    private List<MemberPrice> memberPrice;
}
