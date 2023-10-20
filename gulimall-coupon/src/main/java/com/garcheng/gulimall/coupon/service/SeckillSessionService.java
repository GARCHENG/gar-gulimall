package com.garcheng.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.coupon.entity.SeckillSessionEntity;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:04:34
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SeckillSessionEntity> getLastest3DaySession();
}

