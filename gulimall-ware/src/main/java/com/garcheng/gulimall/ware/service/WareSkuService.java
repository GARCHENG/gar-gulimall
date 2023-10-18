package com.garcheng.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.to.SkuStockTo;
import com.garcheng.gulimall.common.to.mq.StockLockedTo;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.ware.entity.WareSkuEntity;
import com.garcheng.gulimall.ware.vo.LockStockResult;
import com.garcheng.gulimall.ware.vo.WareLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:08:09
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuStockTo> getSkusStock(List<Long> skuIds);

    boolean orderLockStock(WareLockVo wareLockVo);

    void ReleaseStock(StockLockedTo stockLockedTo);
}

