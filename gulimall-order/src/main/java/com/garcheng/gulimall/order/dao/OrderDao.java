package com.garcheng.gulimall.order.dao;

import com.garcheng.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.garcheng.gulimall.order.enume.OrderStatusEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:07:04
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("payStatus") Integer payStatus);
}
