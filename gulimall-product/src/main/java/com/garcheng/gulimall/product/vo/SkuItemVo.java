package com.garcheng.gulimall.product.vo;

import com.garcheng.gulimall.product.entity.SkuImagesEntity;
import com.garcheng.gulimall.product.entity.SkuInfoEntity;
import com.garcheng.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    private SkuInfoEntity info;

    private List<SkuImagesEntity> skuImages;

    private SpuInfoDescEntity desp;

    private List<SkuItemSaleAttrVo> saleAttrVos;

    private List<SpuItemAttrGroupVo> attrGroupVos;


}
