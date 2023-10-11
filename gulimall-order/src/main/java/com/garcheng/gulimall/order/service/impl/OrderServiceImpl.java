package com.garcheng.gulimall.order.service.impl;

import com.garcheng.gulimall.common.vo.MemberInfo;
import com.garcheng.gulimall.order.feign.CartFeignService;
import com.garcheng.gulimall.order.feign.MemberFeignService;
import com.garcheng.gulimall.order.interceptor.LoginInterceptor;
import com.garcheng.gulimall.order.vo.ConfirmOrderVo;
import com.garcheng.gulimall.order.vo.MemberAddressVo;
import com.garcheng.gulimall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.order.dao.OrderDao;
import com.garcheng.gulimall.order.entity.OrderEntity;
import com.garcheng.gulimall.order.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;


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
        //设置地址信息
        List<MemberAddressVo> addressList = memberFeignService.getAddress(memberInfo.getId());
        confirmOrderVo.setAddressVoList(addressList);
        //设置订单商品信息
        List<OrderItemVo> currentCartItems = cartFeignService.getCurrentCartItems();
        confirmOrderVo.setOrderItemVos(currentCartItems);

        // TODO: 2023/10/11 防止重复提交

        return confirmOrderVo;
    }

}