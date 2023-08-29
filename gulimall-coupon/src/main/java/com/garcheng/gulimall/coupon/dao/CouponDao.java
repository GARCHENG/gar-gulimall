package com.garcheng.gulimall.coupon.dao;

import com.garcheng.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-29 16:11:09
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
