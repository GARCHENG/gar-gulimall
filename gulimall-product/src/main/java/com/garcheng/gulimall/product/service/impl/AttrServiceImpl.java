package com.garcheng.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.garcheng.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.garcheng.gulimall.product.dao.AttrGroupDao;
import com.garcheng.gulimall.product.dao.CategoryDao;
import com.garcheng.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.garcheng.gulimall.product.entity.AttrGroupEntity;
import com.garcheng.gulimall.product.entity.CategoryEntity;
import com.garcheng.gulimall.product.service.AttrAttrgroupRelationService;
import com.garcheng.gulimall.product.service.CategoryService;
import com.garcheng.gulimall.product.vo.AttrRespVo;
import com.garcheng.gulimall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
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

import com.garcheng.gulimall.product.dao.AttrDao;
import com.garcheng.gulimall.product.entity.AttrEntity;
import com.garcheng.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private AttrGroupDao attrGroupDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo,attrEntity);
        save(attrEntity);

        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
        attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        relationDao.insert(attrAttrgroupRelationEntity);


    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.eq("attr_id",key).or().like("attr_name",key);
        }
        if (catelogId != 0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        IPage<AttrEntity> page = page(new Query<AttrEntity>().getPage(params),
                queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrRespVo> respVos = page.getRecords().stream().map((obj) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(obj, attrRespVo);

            CategoryEntity categoryEntity = categoryDao.selectById(obj.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }

            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", obj.getAttrId()));
            if (relationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo findAttrInfo(Long attrId) {
        AttrEntity attrEntity = getById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity,attrRespVo);

        AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attrEntity.getAttrId()));
        if (relationEntity != null) {
            attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
        }

        attrRespVo.setCatelogPath(categoryService.findCategoryPath(attrEntity.getCatelogId()));

        return attrRespVo;
    }

    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attr.getAttrId()));
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        if (count == 0){
            relationEntity.setAttrId(attr.getAttrId());
            relationDao.insert(relationEntity);
        }else {
            relationDao.update(relationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id",attr.getAttrId()));

        }
    }


}