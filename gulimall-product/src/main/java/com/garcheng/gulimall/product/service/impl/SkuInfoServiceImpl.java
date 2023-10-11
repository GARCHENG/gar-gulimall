package com.garcheng.gulimall.product.service.impl;

import com.garcheng.gulimall.product.entity.SkuImagesEntity;
import com.garcheng.gulimall.product.entity.SpuInfoDescEntity;
import com.garcheng.gulimall.product.service.*;
import com.garcheng.gulimall.product.vo.SkuItemSaleAttrVo;
import com.garcheng.gulimall.product.vo.SkuItemVo;
import com.garcheng.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.product.dao.SkuInfoDao;
import com.garcheng.gulimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByConditions(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(obj -> {
                obj.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.gt("price", min);
        }
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && new BigDecimal("0").compareTo(new BigDecimal(max)) < 0) {
            queryWrapper.le("price", max);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> skus = list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return skus;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> skuInfoEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfoEntity = getById(skuId);
            skuItemVo.setInfo(skuInfoEntity);
            return skuInfoEntity;
        }, executor);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImagesEntities = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setSkuImages(skuImagesEntities);
        }, executor);

        CompletableFuture<Void> despFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((skuInfoEntity) -> {
            SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(skuInfoEntity.getSpuId());
            skuItemVo.setDesp(spuInfoDesc);
        }, executor);

        CompletableFuture<Void> saleAttrVosFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((skuInfoEntity) -> {
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.findSaleAttrVosBySpuId(skuInfoEntity.getSpuId());
            skuItemVo.setSaleAttrVos(saleAttrVos);
        }, executor);

        CompletableFuture<Void> attrGroupVoFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((skuInfoEntity) -> {
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.findAttrGroupWithAttrBySpuIdAndCatelogId(skuInfoEntity.getSpuId(), skuInfoEntity.getCatalogId());
            skuItemVo.setAttrGroupVos(attrGroupVos);
        }, executor);

        CompletableFuture.allOf(imagesFuture,despFuture,saleAttrVosFuture,attrGroupVoFuture).get();

        return skuItemVo;
    }

    @Override
    public BigDecimal getPriceBySkuId(Long skuId) {
        SkuInfoEntity byId = getById(skuId);
        return byId.getPrice();
    }

}