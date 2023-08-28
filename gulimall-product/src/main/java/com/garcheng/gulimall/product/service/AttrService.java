package com.garcheng.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.product.entity.AttrEntity;
import com.garcheng.gulimall.product.vo.AttrRespVo;
import com.garcheng.gulimall.product.vo.AttrVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-25 14:36:03
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attrVo);

    PageUtils queryPage(Map<String, Object> params, Long catelogId,String attrType);

    AttrRespVo findAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);
}

