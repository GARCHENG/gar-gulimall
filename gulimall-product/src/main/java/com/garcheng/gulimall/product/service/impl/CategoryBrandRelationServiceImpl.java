package com.garcheng.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.garcheng.gulimall.product.dao.BrandDao;
import com.garcheng.gulimall.product.dao.CategoryDao;
import com.garcheng.gulimall.product.entity.BrandEntity;
import com.garcheng.gulimall.product.entity.CategoryEntity;
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

import com.garcheng.gulimall.product.dao.CategoryBrandRelationDao;
import com.garcheng.gulimall.product.entity.CategoryBrandRelationEntity;
import com.garcheng.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandDao brandDao;
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        BrandEntity brandEntity = brandDao.selectById(categoryBrandRelation.getBrandId());
        CategoryEntity categoryEntity = categoryDao.selectById(categoryBrandRelation.getCatelogId());
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        save(categoryBrandRelation);
    }

    @Override
    public void updateBrandDetail(Long brandId, String name) {
        CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
        relationEntity.setBrandId(brandId);
        relationEntity.setBrandName(name);
        update(relationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Override
    public void updateCategoryDetail(Long catId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setCatelogId(catId);
        categoryBrandRelationEntity.setCatelogName(name);
        update(categoryBrandRelationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
    }

    @Override
    public List<BrandEntity> getBrandsListByCatId(Long catId) {
        List<CategoryBrandRelationEntity> relationEntities = baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>()
                .eq("catelog_id", catId));
        List<BrandEntity> brandEntityList = relationEntities.stream().map(entity -> {
            BrandEntity brandEntity = brandDao.selectById(entity.getBrandId());
            return brandEntity;
        }).collect(Collectors.toList());
        return brandEntityList;
    }


}