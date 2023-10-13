package com.garcheng.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareLockVo {

    private String orderSn;

    private List<OrderItemLockVo> locks;

}
