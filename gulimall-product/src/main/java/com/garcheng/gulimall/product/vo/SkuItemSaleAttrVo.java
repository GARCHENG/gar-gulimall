package com.garcheng.gulimall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
//    private String attrValue;
    private List<SaleAttrValueSkuIdsVo> saleAttrValueSkuIdsVoList;
}