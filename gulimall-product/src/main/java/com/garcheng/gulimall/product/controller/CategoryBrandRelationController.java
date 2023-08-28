package com.garcheng.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.garcheng.gulimall.product.entity.BrandEntity;
import com.garcheng.gulimall.product.vo.BrandRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.garcheng.gulimall.product.entity.CategoryBrandRelationEntity;
import com.garcheng.gulimall.product.service.CategoryBrandRelationService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 00:25:41
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/brands/list")
    public R relationBrandsList(@RequestParam(value = "catId", required = true) Long catId) {
        List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandsListByCatId(catId);

        List<BrandRespVo> voList = brandEntities.stream().map(entity -> {
            BrandRespVo brandRespVo = new BrandRespVo();
            brandRespVo.setBrandId(entity.getBrandId());
            brandRespVo.setBrandName(entity.getName());
            return brandRespVo;
        }).collect(Collectors.toList());

        return R.ok().put("data",voList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/catelog/list")
    public R getCatelogList(Long brandId) {
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>()
                .eq("brand_id", brandId));
        return R.ok().put("data", list);
    }

}
