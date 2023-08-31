package com.garcheng.gulimall.ware.service.impl;

import com.garcheng.gulimall.common.constant.PurchaseConstant;
import com.garcheng.gulimall.ware.entity.PurchaseDetailEntity;
import com.garcheng.gulimall.ware.service.PurchaseDetailService;
import com.garcheng.gulimall.ware.vo.MergeVo;
import org.apache.commons.lang.StringUtils;
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

import com.garcheng.gulimall.ware.dao.PurchaseDao;
import com.garcheng.gulimall.ware.entity.PurchaseEntity;
import com.garcheng.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithUnreceive(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("status", PurchaseConstant.PurchaseStatusEnum.NEW_CREATE.getCode())
                .or()
                .eq("status", PurchaseConstant.PurchaseStatusEnum.BE_ASSIGNED.getCode());

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void merge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setStatus(PurchaseConstant.PurchaseStatusEnum.NEW_CREATE.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> detailEntityList = mergeVo.getItems().stream().map(itemId -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(itemId);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(detailEntityList);
    }

    @Transactional
    @Override
    public void recevied(List<Long> purchaseIds) {
        //确定采购单的状态是新建或者已分配
        List<PurchaseEntity> purchaseEntityList = purchaseIds.stream().map(id -> {
            PurchaseEntity purchaseEntity = getById(id);
            return purchaseEntity;
        }).filter(obj -> {
            return obj.getStatus() == PurchaseConstant.PurchaseStatusEnum.NEW_CREATE.getCode() ||
                    obj.getStatus() == PurchaseConstant.PurchaseStatusEnum.BE_ASSIGNED.getCode();
        }).map(obj -> {
            obj.setUpdateTime(new Date());
            obj.setStatus(PurchaseConstant.PurchaseStatusEnum.BE_HANDLE.getCode());
            return obj;
        }).collect(Collectors.toList());
        //修改采购单的状态
        updateBatchById(purchaseEntityList);
        //修改采购单号为该id的各个采购需求单的状态
        purchaseEntityList.forEach(purchaseEntity -> {
            List<PurchaseDetailEntity> detailEntityList = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>()
                    .eq("purchase_id", purchaseEntity.getId()));
            List<PurchaseDetailEntity> purchaseDetailEntityList = detailEntityList.stream().map(obj -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(obj.getId());
                purchaseDetailEntity.setStatus(PurchaseConstant.PurchaseDetailStatusEnum.BE_HANDLE.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntityList);
        });
    }


}