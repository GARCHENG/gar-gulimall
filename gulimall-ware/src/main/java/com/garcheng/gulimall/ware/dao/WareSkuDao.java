package com.garcheng.gulimall.ware.dao;

import com.garcheng.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:08:09
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    Long getStockBySkuId(Long skuid);

    List<Long> selectWareIdHasSkuStock(@Param("skuId") Long skuId);

    int lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

    void unLockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum, @Param("wareId") Long wareId);
}
