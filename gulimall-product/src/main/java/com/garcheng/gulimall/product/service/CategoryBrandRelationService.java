package com.garcheng.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.product.entity.BrandEntity;
import com.garcheng.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 00:25:41
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrandDetail(Long brandId, String name);

    void updateCategoryDetail(Long catId, String name);

    List<BrandEntity> getBrandsListByCatId(Long catId);
}

