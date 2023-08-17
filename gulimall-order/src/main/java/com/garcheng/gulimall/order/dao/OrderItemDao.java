package com.garcheng.gulimall.order.dao;

import com.garcheng.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:07:04
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
