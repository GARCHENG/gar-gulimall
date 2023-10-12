package com.garcheng.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {

    private BigDecimal fare;

    private MemberAddressVo addressVo;

}
