package com.garcheng.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.product.entity.AttrGroupEntity;
import com.garcheng.gulimall.product.vo.AttrGroupWithAttrVo;
import com.garcheng.gulimall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 00:25:41
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrVo> findAttrGroupWithAttrByCatelogId(Long catelogId);

    List<SpuItemAttrGroupVo> findAttrGroupWithAttrBySpuIdAndCatelogId(Long spuId, Long catalogId);
}

