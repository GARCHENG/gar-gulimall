package com.garcheng.gulimall.product.service.impl;

import com.garcheng.gulimall.product.entity.AttrEntity;
import com.garcheng.gulimall.product.service.AttrService;
import com.garcheng.gulimall.product.vo.BaseAttrs;
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

import com.garcheng.gulimall.product.dao.ProductAttrValueDao;
import com.garcheng.gulimall.product.entity.ProductAttrValueEntity;
import com.garcheng.gulimall.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuAttrValue(Long spuId, List<BaseAttrs> baseAttrs) {
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(obj -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(obj.getAttrId());
            productAttrValueEntity.setSpuId(spuId);

            AttrEntity attrEntity = attrService.getById(obj.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());

            productAttrValueEntity.setAttrValue(obj.getAttrValues());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        saveBatch(productAttrValueEntities);
    }

}