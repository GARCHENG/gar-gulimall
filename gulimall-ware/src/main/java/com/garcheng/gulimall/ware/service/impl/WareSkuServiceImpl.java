package com.garcheng.gulimall.ware.service.impl;

import com.garcheng.gulimall.common.to.SkuStockTo;
import com.garcheng.gulimall.ware.exception.NoStockException;
import com.garcheng.gulimall.ware.vo.LockStockResult;
import com.garcheng.gulimall.ware.vo.OrderItemLockVo;
import com.garcheng.gulimall.ware.vo.WareLockVo;
import lombok.Data;
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
import org.springframework.transaction.annotation.Transactional;


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

    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public boolean orderLockStock(WareLockVo wareLockVo) {
        boolean lockSuccess = true;
        List<OrderItemLockVo> locks = wareLockVo.getLocks();
        List<SkuWareHasStock> skuWareHasStocks = locks.stream().map(o -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            skuWareHasStock.setSkuId(o.getSkuId());
            skuWareHasStock.setNum(o.getCount());
            //获取拥有该skuid商品的仓库ids
            List<Long> wareIds = baseMapper.selectWareIdHasSkuStock(o.getSkuId());
            skuWareHasStock.setWareIds(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());

        for (SkuWareHasStock skuWareHasStock : skuWareHasStocks) {
            boolean skuStocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareIds();
            if (wareIds == null || wareIds.size() == 0) {
                lockSuccess = false;
                throw new NoStockException(skuId);
            }else {
                //逐个仓库去尝试锁库存
                for (Long wareId : wareIds) {
                    int i = baseMapper.lockSkuStock(skuId,wareId,skuWareHasStock.getNum());
                    if (i ==1){
                        //锁定库存成功
                        skuStocked = true;
                        break;
                    }else {
                        //重试下一个仓库
                    }
                }
                if (skuStocked == false){
                    //每个仓库都没有
                    lockSuccess = false;
                    throw new NoStockException(skuId);
                }
            }
        }
        return lockSuccess;
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;

    }

}