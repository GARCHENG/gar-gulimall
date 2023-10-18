package com.garcheng.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.order.entity.OrderEntity;
import com.garcheng.gulimall.order.vo.ConfirmOrderVo;
import com.garcheng.gulimall.order.vo.OrderItemVo;
import com.garcheng.gulimall.order.vo.SubmitOrderResponseVo;
import com.garcheng.gulimall.order.vo.SubmitOrderVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:07:04
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    ConfirmOrderVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(SubmitOrderVo submitOrderVo) throws ExecutionException, InterruptedException;

    OrderEntity getOrderByOrderSn(String orderSn);
}

