package com.garcheng.gulimall.order.exception;

public class NoStockException extends RuntimeException{


    public NoStockException(Long skuId) {
        super(skuId+"号商品没有足够的库存");
    }

    public NoStockException() {
        super("没有足够的库存");
    }
}
