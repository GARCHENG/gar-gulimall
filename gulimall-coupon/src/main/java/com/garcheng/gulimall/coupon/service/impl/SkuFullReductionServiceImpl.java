package com.garcheng.gulimall.coupon.service.impl;

import com.garcheng.gulimall.common.to.MemberPrice;
import com.garcheng.gulimall.common.to.SkuReductTo;
import com.garcheng.gulimall.coupon.entity.MemberPriceEntity;
import com.garcheng.gulimall.coupon.entity.SkuLadderEntity;
import com.garcheng.gulimall.coupon.service.MemberPriceService;
import com.garcheng.gulimall.coupon.service.SkuLadderService;
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

import com.garcheng.gulimall.coupon.dao.SkuFullReductionDao;
import com.garcheng.gulimall.coupon.entity.SkuFullReductionEntity;
import com.garcheng.gulimall.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveReductionInfo(SkuReductTo skuReductTo) {

        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductTo,skuFullReductionEntity);
        save(skuFullReductionEntity);

        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductTo,skuLadderEntity);
        skuLadderEntity.setPrice(skuReductTo.getFullPrice().multiply(skuReductTo.getDiscount()));
        skuLadderEntity.setAddOther(skuReductTo.getCountStatus());
        skuLadderService.save(skuLadderEntity);

        List<MemberPrice> memberPrice = skuReductTo.getMemberPrice();
        if (memberPrice != null) {
            List<MemberPriceEntity> memberPriceEntityList = memberPrice.stream().map(obj -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setMemberLevelId(obj.getId());
                memberPriceEntity.setMemberLevelName(obj.getName());
                memberPriceEntity.setMemberPrice(obj.getPrice());
                memberPriceEntity.setAddOther(1);
                return memberPriceEntity;
            }).collect(Collectors.toList());
            memberPriceService.saveBatch(memberPriceEntityList);
        }


    }

}