package com.garcheng.gulimall.common.to;

import lombok.Data;

@Data
public class SkuStockTo {

    private Long skuId;

    private Long stock;

    private Boolean hasStock;

}
