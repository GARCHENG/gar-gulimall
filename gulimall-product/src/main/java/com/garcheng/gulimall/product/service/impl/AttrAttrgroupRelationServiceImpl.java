package com.garcheng.gulimall.product.service.impl;

import com.garcheng.gulimall.product.dao.AttrDao;
import com.garcheng.gulimall.product.entity.AttrEntity;
import com.garcheng.gulimall.product.vo.AttrGroupAttrRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.garcheng.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.garcheng.gulimall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Autowired
    private AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<AttrEntity> getRelations(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = this.list(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_group_id", attrgroupId));
        List<Long> attrIds = relationEntities.stream().map((obj) -> {
            return obj.getAttrId();
        }).collect(Collectors.toList());
        List<AttrEntity> attrEntities = null;
        if (attrIds != null && attrIds.size() > 0) {
            attrEntities = attrDao.selectBatchIds(attrIds);
        }
        return attrEntities;
    }

    @Override
    public void deleteByVoList(List<AttrGroupAttrRelationVo> voList) {
        List<AttrAttrgroupRelationEntity> entities = voList.stream().map(vo -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(vo, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        this.baseMapper.deleteBatchRelation(entities);
    }

    @Override
    public void addAttrRelation(List<AttrGroupAttrRelationVo> voList) {
        List<AttrAttrgroupRelationEntity> entityList = voList.stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        saveBatch(entityList);
    }

}