package com.garcheng.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {

    private BigDecimal fare;

    private MemberAddressVo addressVo;
}
