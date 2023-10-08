package com.garcheng.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cart {

    private List<CartItem> items;

    private Integer countNum;

    private Integer countType;

    private BigDecimal totalAccount;

    private BigDecimal reduce = new BigDecimal("0");


    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count = count + item.getCount()  ;
            }
        }
        return count;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    public Integer getCountType() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count ++ ;
            }
        }
        return count;
    }

    public void setCountType(Integer countType) {
        this.countType = countType;
    }

    public BigDecimal getTotalAccount() {
        BigDecimal totalPrice = new BigDecimal("0");
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                totalPrice = totalPrice.add(item.getTotalPrice());
            }
        }
        BigDecimal subtract = totalPrice.subtract(getReduce());
        return subtract;
    }

    public void setTotalAccount(BigDecimal totalAccount) {
        this.totalAccount = totalAccount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
