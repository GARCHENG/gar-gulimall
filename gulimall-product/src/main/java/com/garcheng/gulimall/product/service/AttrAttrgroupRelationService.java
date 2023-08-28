package com.garcheng.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.garcheng.gulimall.product.entity.AttrEntity;
import com.garcheng.gulimall.product.vo.AttrGroupAttrRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 00:25:41
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<AttrEntity> getRelations(Long attrgroupId);

    void deleteByVoList(List<AttrGroupAttrRelationVo> voList);

    void addAttrRelation(List<AttrGroupAttrRelationVo> voList);
}

