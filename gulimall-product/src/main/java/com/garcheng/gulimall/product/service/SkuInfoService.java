package com.garcheng.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-25 14:36:03
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

