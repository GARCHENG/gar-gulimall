package com.garcheng.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.garcheng.gulimall.product.service.CategoryBrandRelationService;
import com.garcheng.gulimall.product.vo.CategoryLevel2Vo;
import com.garcheng.gulimall.product.vo.CategoryLevel3Vo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate redisTemplate;

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

    @Override
    public Map<String, List<CategoryLevel2Vo>> getCategoryJson() {
        String categoryjson = (String) redisTemplate.opsForValue().get("categoryjson");
        if (StringUtils.isEmpty(categoryjson)){
            Map<String, List<CategoryLevel2Vo>> categoryMap = getCategoryMap();
            String jsonString = JSON.toJSONString(categoryMap);
            redisTemplate.opsForValue().set("categoryjson",jsonString);
            return categoryMap;
        }
        Map<String, List<CategoryLevel2Vo>> resultMap = JSON.parseObject(categoryjson, new TypeReference<Map<String, List<CategoryLevel2Vo>>>() {
        });
        return resultMap;
    }

    private Map<String, List<CategoryLevel2Vo>> getCategoryMap() {
        List<CategoryEntity> allCategory = list();
        List<CategoryEntity> level1 = findCatByParentCId(allCategory, 0l);
        Map<String, List<CategoryLevel2Vo>> categoryJson = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> level2 = findCatByParentCId(allCategory, v.getCatId());
            List<CategoryLevel2Vo> level2Vos = level2.stream().map(l2 -> {
                CategoryLevel2Vo categoryLevel2Vo = new CategoryLevel2Vo();
                BeanUtils.copyProperties(l2, categoryLevel2Vo);
                List<CategoryEntity> catgoryLevel3 = findCatByParentCId(allCategory, l2.getCatId());
                List<CategoryLevel3Vo> level3VoList = catgoryLevel3.stream().map(l3 -> {
                    CategoryLevel3Vo categoryLevel3Vo = new CategoryLevel3Vo();
                    BeanUtils.copyProperties(l3, categoryLevel3Vo);
                    return categoryLevel3Vo;
                }).collect(Collectors.toList());

                categoryLevel2Vo.setCategoryLevel3Vos(level3VoList);
                return categoryLevel2Vo;
            }).collect(Collectors.toList());
            return level2Vos;
        }));

        return categoryJson;
    }

    private List<CategoryEntity> findCatByParentCId(List<CategoryEntity> allCategory , Long parentId){
        List<CategoryEntity> categoryEntities = allCategory.stream().filter(cat -> {
            return cat.getParentCid() == parentId;
        }).collect(Collectors.toList());
        return categoryEntities;
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