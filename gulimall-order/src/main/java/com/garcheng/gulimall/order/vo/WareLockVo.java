package com.garcheng.gulimall.order.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareLockVo {

    private String orderSn;

    private List<OrderItemLockVo> locks;

}