package com.garcheng.gulimall.cart.service;

import com.garcheng.gulimall.cart.vo.Cart;
import com.garcheng.gulimall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem getCartItemBySkuId(Long skuId);

    Cart getCart() throws ExecutionException, InterruptedException;

    void checkItem(Long skuId, Integer check);

    void countItem(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
