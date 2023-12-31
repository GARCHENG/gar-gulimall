package com.garcheng.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.garcheng.gulimall.cart.feign.ProductFeignService;
import com.garcheng.gulimall.cart.interceptor.CartInterceptor;
import com.garcheng.gulimall.cart.service.CartService;
import com.garcheng.gulimall.cart.vo.Cart;
import com.garcheng.gulimall.cart.vo.CartItem;
import com.garcheng.gulimall.cart.vo.SkuInfoEntity;
import com.garcheng.gulimall.cart.vo.UserInfoTo;
import com.garcheng.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    ProductFeignService productFeignService;

    public static final String CART_REDIS_PREFIX_KEY = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            //添加新商品到购物车
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R result = productFeignService.info(skuId);
                if (result.getCode() == 0) {
                    SkuInfoEntity skuInfo = result.getData("skuInfo", new TypeReference<SkuInfoEntity>() {
                    });
                    cartItem.setSkuId(skuId);
                    cartItem.setTitle(skuInfo.getSkuTitle());
                    cartItem.setImage(skuInfo.getSkuDefaultImg());
                    cartItem.setPrice(skuInfo.getPrice());
                    cartItem.setCount(num);
                } else {
                    log.error("远程调用product模块失败。。。。");
                }
            }, executor);

            CompletableFuture<Void> getSaleAttrTask = CompletableFuture.runAsync(() -> {
                List<String> saleAttrString = productFeignService.getSaleAttrString(skuId);
                cartItem.setSkuAttr(saleAttrString);
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask, getSaleAttrTask).get();

            String jsonString = JSON.toJSONString(cartItem);
            cartOps.put(skuId + "", jsonString);

            return cartItem;
        } else {
            //只修改数量
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            String json = JSON.toJSONString(cartItem);
            cartOps.put(skuId + "", json);

            return cartItem;
        }


    }

    @Override
    public CartItem getCartItemBySkuId(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String json = (String) cartOps.get(skuId.toString());

        return JSON.parseObject(json, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartRedisKey = "";
        if (userInfoTo.getUserId() != null) {
            //已经登录了，若临时用户还有购物车信息则合并购物车
            cartRedisKey = CART_REDIS_PREFIX_KEY + userInfoTo.getUserId();
            Cart tempCart = getCartByCartKey(CART_REDIS_PREFIX_KEY + userInfoTo.getUserKey());
            if (tempCart != null) {
                List<CartItem> items = tempCart.getItems();
                for (CartItem item : items) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                clearCartByCartKey(CART_REDIS_PREFIX_KEY + userInfoTo.getUserKey());
            }
            Cart cart = getCartByCartKey(cartRedisKey);
            return cart;
        } else {
            //还没有登录
            cartRedisKey = CART_REDIS_PREFIX_KEY + userInfoTo.getUserKey();
            Cart cart = getCartByCartKey(cartRedisKey);
            return cart;
        }
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItemBySkuId(skuId);
        cartItem.setCheck(check == 1 ? true : false);
        String json = JSON.toJSONString(cartItem);
        cartOps.put(skuId + "", json);
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItemBySkuId(skuId);
        cartItem.setCount(num);
        String json = JSON.toJSONString(cartItem);
        cartOps.put(skuId + "", json);
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId + "");
    }

    @Override
    public List<CartItem> getCurrentCartItems() throws ExecutionException, InterruptedException {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            Cart cart = getCart();
            List<CartItem> items = cart.getItems();
            List<CartItem> cartItems = items.stream().filter(item -> item.getCheck())
                    .map(item -> {
                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(price);
                        return item;
                    }).collect(Collectors.toList());
            return cartItems;
        } else {
            return null;
        }
    }

    private void clearCartByCartKey(String key) {
        stringRedisTemplate.delete(key);
    }

    private Cart getCartByCartKey(String cartRedisKey) {
        BoundHashOperations<String, Object, Object> cartOperation = stringRedisTemplate.boundHashOps(cartRedisKey);
        Cart cart = new Cart();
        List<Object> values = cartOperation.values();
        if (values != null) {
            List<CartItem> cartItems = values.stream().map(o -> {
                String s = o.toString();
                return JSON.parseObject(s, CartItem.class);
            }).collect(Collectors.toList());
            cart.setItems(cartItems);
            return cart;
        }
        return null;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartRedisKey = "";
        if (userInfoTo.getUserId() != null) {
            cartRedisKey = CART_REDIS_PREFIX_KEY + userInfoTo.getUserId();
        } else {
            cartRedisKey = CART_REDIS_PREFIX_KEY + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> cartOperation = stringRedisTemplate.boundHashOps(cartRedisKey);
        return cartOperation;
    }
}
