package com.garcheng.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.garcheng.gulimall.common.to.SkuStockTo;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.common.vo.MemberInfo;
import com.garcheng.gulimall.order.feign.CartFeignService;
import com.garcheng.gulimall.order.feign.MemberFeignService;
import com.garcheng.gulimall.order.feign.WareFeignService;
import com.garcheng.gulimall.order.interceptor.LoginInterceptor;
import com.garcheng.gulimall.order.vo.ConfirmOrderVo;
import com.garcheng.gulimall.order.vo.MemberAddressVo;
import com.garcheng.gulimall.order.vo.OrderItemVo;
import com.garcheng.gulimall.order.vo.SkuStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.order.dao.OrderDao;
import com.garcheng.gulimall.order.entity.OrderEntity;
import com.garcheng.gulimall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    ThreadPoolExecutor executor;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public ConfirmOrderVo confirmOrder() throws ExecutionException, InterruptedException {
        MemberInfo memberInfo = LoginInterceptor.threadLocal.get();
        ConfirmOrderVo confirmOrderVo = new ConfirmOrderVo();
        //设置积分信息
        confirmOrderVo.setIntegration(memberInfo.getIntegration());
        //获取当前主线程的request信息
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //设置地址信息
        CompletableFuture<Void> getAddressTask = CompletableFuture.runAsync(() -> {
            //解决异步feign远程调用request丢失
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> addressList = memberFeignService.getAddress(memberInfo.getId());
            confirmOrderVo.setAddressVoList(addressList);
        }, executor);
        //设置订单商品信息
        CompletableFuture<Void> getCurrentCartItemsTask = CompletableFuture.runAsync(() -> {
            //解决异步feign远程调用request丢失
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> currentCartItems = null;
            try {
                currentCartItems = cartFeignService.getCurrentCartItems();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            confirmOrderVo.setOrderItemVos(currentCartItems);
        }, executor).thenRunAsync(() ->{
            List<OrderItemVo> orderItemVos = confirmOrderVo.getOrderItemVos();
            List<Long> skuIdS = orderItemVos.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R skusStock = wareFeignService.getSkusStock(skuIdS);
                List<SkuStockVo> data = skusStock.getData(new TypeReference<List<SkuStockVo>>() {});
                if (data != null && data.size() > 0) {
                    Map<Long, Boolean> stockMap = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                    confirmOrderVo.setHasStockMap(stockMap);
                }
        },executor);

        CompletableFuture.allOf(getAddressTask,getCurrentCartItemsTask).get();

        // TODO: 2023/10/11 防止重复提交

        return confirmOrderVo;
    }

}