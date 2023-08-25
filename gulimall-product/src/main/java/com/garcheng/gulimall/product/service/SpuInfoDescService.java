package com.garcheng.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-25 14:33:52
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

