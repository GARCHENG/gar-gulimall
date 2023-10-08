package com.garcheng.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.garcheng.gulimall.product.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-25 14:36:03
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrVo> findSaleAttrVosBySpuId(Long spuId);

    List<String> getSaleAttrStringBySkuId(Long skuId);
}

