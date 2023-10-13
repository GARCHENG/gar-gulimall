package com.garcheng.gulimall.order.vo;

import com.garcheng.gulimall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {

    private OrderEntity orderEntity;

    //0表示成功
    private Integer code;
}
