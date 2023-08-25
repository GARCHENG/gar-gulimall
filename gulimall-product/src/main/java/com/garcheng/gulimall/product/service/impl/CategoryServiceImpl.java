package com.garcheng.gulimall.product.service.impl;

import com.garcheng.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.product.dao.CategoryDao;
import com.garcheng.gulimall.product.entity.CategoryEntity;
import com.garcheng.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = list();
        List<CategoryEntity> menu1 = entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildren(menu, entities));
                    return menu;
                })
                .sorted(Comparator.comparingInt(o -> (o.getSort() == null ? 0 : o.getSort())))
                .collect(Collectors.toList());
        return menu1;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO: 2023/8/22 校验菜单是否允许被删除
        removeByIds(asList);
    }

    @Override
    public Long[] findCategoryPath(Long catelogId) {
        List<Long> categoryPath = new ArrayList<>();
        getParentCatIdToList(catelogId,categoryPath);
        Collections.reverse(categoryPath);
        return categoryPath.toArray(new Long[categoryPath.size()]);
    }

    @Override
    public void updateDetailById(CategoryEntity category) {
        updateById(category);
        if (!StringUtils.isEmpty(category.getName())){
            categoryBrandRelationService.updateCategoryDetail(category.getCatId(),category.getName());
        }
    }

    private List getParentCatIdToList(Long catelogId, List<Long> categoryPath) {
        CategoryEntity categoryEntity = this.getById(catelogId);
        categoryPath.add(categoryEntity.getCatId());
        if (categoryEntity.getParentCid()!=0){
            getParentCatIdToList(categoryEntity.getParentCid(),categoryPath);
        }
        return categoryPath;
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream().filter(children -> children.getParentCid() == root.getCatId())
                .map(children -> {
                    children.setChildren(getChildren(children, all));
                    return children;
                })
                .sorted(Comparator.comparingInt(o -> (o.getSort() == null ? 0 : o.getSort())))
                .collect(Collectors.toList());
    }

}