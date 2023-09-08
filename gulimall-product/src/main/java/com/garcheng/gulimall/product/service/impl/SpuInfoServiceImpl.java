package com.garcheng.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.garcheng.gulimall.common.constant.ProductConstant;
import com.garcheng.gulimall.common.to.SkuReductTo;
import com.garcheng.gulimall.common.to.SkuStockTo;
import com.garcheng.gulimall.common.to.SpuBoundsTo;
import com.garcheng.gulimall.common.to.es.SkuUpTo;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.product.entity.*;
import com.garcheng.gulimall.product.feign.CouponFeignService;
import com.garcheng.gulimall.product.feign.SearchFeignService;
import com.garcheng.gulimall.product.feign.WareFeignService;
import com.garcheng.gulimall.product.service.*;
import com.garcheng.gulimall.product.vo.*;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //保存基本的spu信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);

        //保存spu描述图片的信息 pms_spu_info_desc
        List<String> decript = spuSaveVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(spuInfoDescEntity);

        //保存spu商品图片集信息 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //保存spu基本属性信息 pms_product_attr_value
        productAttrValueService.saveSpuAttrValue(spuInfoEntity.getId(), spuSaveVo.getBaseAttrs());

        //保存优惠信息（远程）
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        Bounds bounds = spuSaveVo.getBounds();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode() != 0) {
            log.error("coupon服务远程调用失败~~~");
        }

        //保存sku基本信息 pms_sku_info
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(sku -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);

                String defaultImage = null;
                List<Images> skuImages = sku.getImages();
                if (skuImages != null) {
                    for (Images skuImage : skuImages) {
                        if (skuImage.getDefaultImg() != 0) {
                            defaultImage = skuImage.getImgUrl();
                        }
                    }
                }
                skuInfoEntity.setSkuDefaultImg(defaultImage);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setPrice(sku.getPrice());
                skuInfoEntity.setBrandId(spuSaveVo.getBrandId());
                skuInfoEntity.setCatalogId(spuSaveVo.getCatalogId());
                skuInfoEntity.setSaleCount(0l);

                skuInfoService.save(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();
                //保存商品sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attrs = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //保存sku图片信息 pms_sku_images
                if (skuImages != null) {
                    List<SkuImagesEntity> skuImagesEntities = skuImages.stream().map(skuImage -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        BeanUtils.copyProperties(skuImage, skuImagesEntity);
                        skuImagesEntity.setSkuId(skuId);
                        return skuImagesEntity;
                    }).filter(obj -> {
                        return !StringUtils.isEmpty(obj.getImgUrl());
                    }).collect(Collectors.toList());
                    skuImagesService.saveBatch(skuImagesEntities);
                }
                //保存sku优惠信息
                SkuReductTo skuReductTo = new SkuReductTo();
                BeanUtils.copyProperties(sku, skuReductTo);
                skuReductTo.setSkuId(skuId);
                R r1 = couponFeignService.saveSkuReduction(skuReductTo);
                if (r1.getCode() != 0) {
                    log.error("coupon服务远程调用失败~~~");
                }

            });
        }


    }

    @Override
    public PageUtils queryPageByConditions(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(obj -> {
                obj.eq("id", key).or().like("spu_name", key);
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
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void upSpu(Long spuId) {

        // 查询当前spu的可以被检索的规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        List<Long> attrIds = productAttrValueEntities.stream().map(obj -> {
            return obj.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchableAttrIds = attrService.getSearchableAttrIds(attrIds);
        Set<Long> idsSet = new HashSet<>(searchableAttrIds);

        List<ProductAttrValueEntity> attrValueEntityList = productAttrValueEntities.stream().filter(obj -> {
            return idsSet.contains(obj.getAttrId());
        }).collect(Collectors.toList());

        List<SkuUpTo.attr> attrs = attrValueEntityList.stream().map(obj -> {
            SkuUpTo.attr attr = new SkuUpTo.attr();
            BeanUtils.copyProperties(obj, attr);
            return attr;
        }).collect(Collectors.toList());

        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);

        //查看是否又库存
        List<Long> skuIds = skus.stream().map(sku -> {
            return sku.getSkuId();
        }).collect(Collectors.toList());

        Map<Long, Boolean> stockMap = null;

        try {
            R result = wareFeignService.getSkusStock(skuIds);
            TypeReference<List<SkuStockTo>> typeReference = new TypeReference<List<SkuStockTo>>(){};
            List<SkuStockTo> skuStockToList = result.getData(typeReference);
            stockMap = skuStockToList.stream()
                    .collect(Collectors.toMap(SkuStockTo::getSkuId,SkuStockTo::getHasStock));
        } catch (Exception e) {
            log.error("远程调用ware服务出错，原因:{}", e.getCause());
        }

        if (skus != null) {
            Map<Long, Boolean> finalStockMap = stockMap;
            List<SkuUpTo> esModels = skus.stream().map(sku -> {
                SkuUpTo skuUpTo = new SkuUpTo();
                BeanUtils.copyProperties(sku, skuUpTo);
                //skuPrice,skuImg hasStock hotScore brandName brandImg;catalogName attrs
                skuUpTo.setSkuPrice(sku.getPrice());
                skuUpTo.setSkuImg(sku.getSkuDefaultImg());

                if (finalStockMap != null) {
                    skuUpTo.setHasStock(finalStockMap.get(sku.getSkuId()));
                } else {
                    skuUpTo.setHasStock(false);
                }
                //商品新上架默认热度为0

                // TODO: 2023/9/7 热度相关
                skuUpTo.setHotScore(0L);
                //查询品牌和分类
                BrandEntity brandEntity = brandService.getById(sku.getBrandId());
                skuUpTo.setBrandName(brandEntity.getName());
                skuUpTo.setBrandImg(brandEntity.getLogo());

                CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
                skuUpTo.setCatalogName(categoryEntity.getName());

                skuUpTo.setAttrs(attrs);
                return skuUpTo;
            }).collect(Collectors.toList());

            //2023/9/7 将包装好的信息保存到es中
            R result = searchFeignService.upSpuInfo(esModels);
            if (result.getCode() == 0){
                baseMapper.updateSpuPublishStatus(spuId, ProductConstant.StatusEnum.PRODUCT_UP.getCode());
            }else {
                // TODO: 2023/9/7 重复调用？幂等？
            }
        }
    }

}