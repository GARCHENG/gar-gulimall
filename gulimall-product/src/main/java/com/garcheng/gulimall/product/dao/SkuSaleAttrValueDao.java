package com.garcheng.gulimall.product.dao;

import com.garcheng.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.garcheng.gulimall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-25 14:36:03
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> findSaleAttrVosBySpuId(@Param("spuId") Long spuId);

    List<String> getSaleAttrStringBySkuId(@Param("skuId") Long skuId);

}
