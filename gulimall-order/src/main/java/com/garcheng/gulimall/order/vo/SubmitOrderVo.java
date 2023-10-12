package com.garcheng.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubmitOrderVo {

    private Long addrId;

    private Integer payType;

    private BigDecimal payPrice;

    private String note;

    private String orderToken;

    //无需提交购买的商品信息

    //优惠发票


}
