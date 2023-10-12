package com.garcheng.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ConfirmOrderVo {

    //会员的地址信息
    @Getter @Setter
    private List<MemberAddressVo> addressVoList;

    //订单商品信息
    @Getter @Setter
    private List<OrderItemVo> orderItemVos;



    //发票

    //积分
    @Getter @Setter
    private Integer integration;

    //总价
    private BigDecimal totalPrice;

    //是否有货
    @Getter @Setter
    private Map<Long,Boolean> hasStockMap;

    public BigDecimal getTotalPrice() {
        BigDecimal price = new BigDecimal("0");
        if (orderItemVos != null && orderItemVos.size() > 0){
            for (OrderItemVo orderItemVo : orderItemVos) {
                BigDecimal multiply = orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount()));
                price = price.add(multiply);
            }
        }
        return price;
    }

    public BigDecimal getPayPrice() {
        // TODO: 2023/10/11 处理优惠信息
        return getTotalPrice();
    }

    //应付价格
    private BigDecimal payPrice;

    public Integer getCount(){
        int count = 0;
        if (orderItemVos != null && orderItemVos.size() > 0){
            for (OrderItemVo orderItemVo : orderItemVos) {
                count = count + orderItemVo.getCount();
            }
        }
        return count;
    }

    //防重复提交
    @Getter @Setter
    private String orderToken;


}
