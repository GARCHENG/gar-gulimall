package com.garcheng.gulimall.ware.service.impl;

import com.garcheng.gulimall.common.to.SkuStockTo;
import com.garcheng.gulimall.ware.vo.LockStockResult;
import com.garcheng.gulimall.ware.vo.WareLockVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.ware.dao.WareSkuDao;
import com.garcheng.gulimall.ware.entity.WareSkuEntity;
import com.garcheng.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuStockTo> getSkusStock(List<Long> skuIds) {
        List<SkuStockTo> stockToList = skuIds.stream().map(skuid -> {
            SkuStockTo skuStockTo = new SkuStockTo();
            skuStockTo.setSkuId(skuid);
            skuStockTo.setStock(baseMapper.getStockBySkuId(skuid));
            skuStockTo.setHasStock(skuStockTo.getStock() != null && skuStockTo.getStock() > 0);
            return skuStockTo;
        }).collect(Collectors.toList());
        return stockToList;
    }

    @Override
    public List<LockStockResult> orderLockStock(WareLockVo wareLockVo) {
        return null;
    }

}