package com.garcheng.gulimall.product.service.impl;

import com.garcheng.gulimall.product.entity.*;
import com.garcheng.gulimall.product.service.*;
import com.garcheng.gulimall.product.vo.Attr;
import com.garcheng.gulimall.product.vo.Images;
import com.garcheng.gulimall.product.vo.Skus;
import com.garcheng.gulimall.product.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

        //保存sku基本信息 pms_sku_info
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(sku -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku,skuInfoEntity);

                String defaultImage = null;
                List<Images> skuImages = sku.getImages();
                if (skuImages != null) {
                    for (Images skuImage : skuImages) {
                        if (skuImage.getDefaultImg() != 0){
                            defaultImage = skuImage.getImgUrl();
                        }
                    }
                }
                skuInfoEntity.setSkuDefaultImg(defaultImage);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setPrice(sku.getFullPrice());
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
                //保存sku图片信息 pms_sku_images 并找出默认图片
                if (skuImages != null) {
                    List<SkuImagesEntity> skuImagesEntities = skuImages.stream().map(skuImage -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        BeanUtils.copyProperties(skuImage, skuImagesEntity);
                        skuImagesEntity.setSkuId(skuId);
                        return skuImagesEntity;
                    }).collect(Collectors.toList());
                    skuImagesService.saveBatch(skuImagesEntities);
                }
            });
        }




    }

}