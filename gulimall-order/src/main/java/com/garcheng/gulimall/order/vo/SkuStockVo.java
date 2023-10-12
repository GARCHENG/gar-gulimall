package com.garcheng.gulimall.order.vo;

import lombok.Data;

@Data
public class SkuStockVo {

    private Long skuId;

    private Long stock;

    private Boolean hasStock;
}
