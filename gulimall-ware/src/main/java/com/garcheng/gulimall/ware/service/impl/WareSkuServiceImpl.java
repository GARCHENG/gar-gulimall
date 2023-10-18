package com.garcheng.gulimall.ware.service.impl;

import com.garcheng.gulimall.common.to.SkuStockTo;
import com.garcheng.gulimall.common.to.mq.StockLockDetail;
import com.garcheng.gulimall.common.to.mq.StockLockedTo;
import com.garcheng.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.garcheng.gulimall.ware.entity.WareOrderTaskEntity;
import com.garcheng.gulimall.ware.exception.NoStockException;
import com.garcheng.gulimall.ware.service.WareOrderTaskDetailService;
import com.garcheng.gulimall.ware.service.WareOrderTaskService;
import com.garcheng.gulimall.ware.vo.LockStockResult;
import com.garcheng.gulimall.ware.vo.OrderItemLockVo;
import com.garcheng.gulimall.ware.vo.WareLockVo;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

import com.garcheng.gulimall.ware.dao.WareSkuDao;
import com.garcheng.gulimall.ware.entity.WareSkuEntity;
import com.garcheng.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
@RabbitListener(queues = "stock.release.stock.queue")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareOrderTaskService wareOrderTaskService;
    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    RabbitTemplate rabbitTemplate;

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

//    @Transactional(rollbackFor = NoStockException.class)
    @Transactional
    @Override
    public boolean orderLockStock(WareLockVo wareLockVo) {

        //保存锁库存的任务信息，方便后期回滚等操作
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(wareLockVo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

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
                        //保存库存锁定任务详情表
                        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuWareHasStock.getSkuId(), null, skuWareHasStock.getNum(), wareOrderTaskEntity.getId(), wareId, 1);
                        wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                        // TODO: 2023/10/18 告诉mq锁库存成功
                        StockLockedTo stockLockedTo = new StockLockedTo();
                        stockLockedTo.setTaskId(wareOrderTaskEntity.getId());
                        StockLockDetail detail = new StockLockDetail();
                        BeanUtils.copyProperties(wareOrderTaskDetailEntity,detail);
                        stockLockedTo.setDetail(detail);
                        rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",stockLockedTo);
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
    //处理解锁库存
    @RabbitHandler
    public void handleStockRelease(StockLockedTo stockLockedTo , Message message , Channel channel){
        Long taskId = stockLockedTo.getTaskId();
        StockLockDetail detail = stockLockedTo.getDetail();
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detail.getId());

        if (byId != null) {
            //如果不为空，1）用户直接取消 2）用户过期未支付 3）下单操作锁库存成功后出现异常，需手动补偿进行回滚
        }else {
            //为空 ，则在锁库存中出现异常，已经回滚了之前已经锁了的库存
        }
    }


}