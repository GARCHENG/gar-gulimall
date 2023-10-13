package com.garcheng.gulimall.order.to;

import com.garcheng.gulimall.order.entity.OrderEntity;
import com.garcheng.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class orderCreateTo {

    private OrderEntity orderEntity;

    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice;

    private BigDecimal fare;


}
