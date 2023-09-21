package com.garcheng.gulimall.product.dao;

import com.garcheng.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.garcheng.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 00:25:41
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVo> findAttrGroupWithAttrBySpuIdAndCatelogId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
