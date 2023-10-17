package com.garcheng.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.common.vo.MemberInfo;
import com.garcheng.gulimall.order.constant.OrderContant;
import com.garcheng.gulimall.order.entity.OrderItemEntity;
import com.garcheng.gulimall.order.enume.OrderStatusEnum;
import com.garcheng.gulimall.order.exception.NoStockException;
import com.garcheng.gulimall.order.feign.CartFeignService;
import com.garcheng.gulimall.order.feign.MemberFeignService;
import com.garcheng.gulimall.order.feign.ProductFeignService;
import com.garcheng.gulimall.order.feign.WareFeignService;
import com.garcheng.gulimall.order.interceptor.LoginInterceptor;
import com.garcheng.gulimall.order.service.OrderItemService;
import com.garcheng.gulimall.order.to.orderCreateTo;
import com.garcheng.gulimall.order.vo.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    public static ThreadLocal<SubmitOrderVo> submitOrderVoThreadLocal = new ThreadLocal<>();

    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    OrderItemService orderItemService;

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
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> orderItemVos = confirmOrderVo.getOrderItemVos();
            List<Long> skuIdS = orderItemVos.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R skusStock = wareFeignService.getSkusStock(skuIdS);
            List<SkuStockVo> data = skusStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null && data.size() > 0) {
                Map<Long, Boolean> stockMap = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmOrderVo.setHasStockMap(stockMap);
            }
        }, executor);

        CompletableFuture.allOf(getAddressTask, getCurrentCartItemsTask).get();

        // TODO: 2023/10/11 防止重复提交
        String orderToken = UUID.randomUUID().toString().replace("-", "");
        confirmOrderVo.setOrderToken(orderToken);
        stringRedisTemplate.opsForValue().set(OrderContant.USER_ORDER_REDIS_TOKEN_PREFIX + memberInfo.getId(), orderToken);

        return confirmOrderVo;
    }

//    @Transactional(rollbackFor = NoStockException.class)
    //只能管住自己的事务，已经执行完成的分布式远程调用不能进行回滚
    //本地事务失效（同一个对象内事务互调：绕过了代理对象，事务使用代理对象来控制） 解决：使用代理对象来调用
    //0）、导入 spring-boot-starter-aop
    //1）、@EnableTransactionManagement(proxyTargetClass = true)
    //2）、@EnableAspectJAutoProxy(exposeProxy=true)
    //3）、AopContext.currentProxy() 调用方法
//    @GlobalTransactional   seata 的AT模式不适用高并发的场景（适用于大保存情景）
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(SubmitOrderVo submitOrderVo) throws ExecutionException, InterruptedException {
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        MemberInfo memberInfo = LoginInterceptor.threadLocal.get();
        //验令牌
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        //0表示失败 1表示成功
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderContant.USER_ORDER_REDIS_TOKEN_PREFIX + memberInfo.getId()),
                submitOrderVo.getOrderToken());
        response.setCode(0);
        if (result == 0L) {
            response.setCode(1);
            return response;
        } else {
            submitOrderVoThreadLocal.set(submitOrderVo);
            //创建订单
            orderCreateTo orderCreateTo = createOrder();
            //验价
            if (Math.abs(orderCreateTo.getPayPrice().subtract(submitOrderVo.getPayPrice()).doubleValue()) < 0.01){
                //保存订单
                saveOrder(orderCreateTo);
                //锁库存
                WareLockVo wareLockVo = new WareLockVo();
                wareLockVo.setOrderSn(orderCreateTo.getOrderEntity().getOrderSn());
                List<OrderItemLockVo> lockVoList = orderCreateTo.getOrderItems().stream().map(orderItem -> {
                    OrderItemLockVo orderItemLockVo = new OrderItemLockVo();
                    orderItemLockVo.setSkuId(orderItem.getSkuId());
                    orderItemLockVo.setCount(orderItem.getSkuQuantity());
                    return orderItemLockVo;
                }).collect(Collectors.toList());
                wareLockVo.setLocks(lockVoList);
                //远程锁库存
                R r = wareFeignService.lockStock(wareLockVo);
                if (r.getCode() == 0){
                    //锁成功了
                    // TODO: 2023/10/16 远程扣减积分
//                    int i = 1/0;
                    response.setOrderEntity(orderCreateTo.getOrderEntity());
                    return response;
                }else {
                    throw new NoStockException();
                }
            }else{
                response.setCode(2);
                return response;
            }

        }

    }

    private void saveOrder(orderCreateTo orderCreateTo) {
        OrderEntity orderEntity = orderCreateTo.getOrderEntity();
        orderEntity.setModifyTime(new Date());
        save(orderEntity);

        List<OrderItemEntity> orderItems = orderCreateTo.getOrderItems();
//        orderItemService.saveBatch(orderItems);
        for (OrderItemEntity orderItem : orderItems) {
            orderItemService.save(orderItem);
        }

    }

    /**
     * 创建订单to
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private orderCreateTo createOrder() throws ExecutionException, InterruptedException {
        orderCreateTo orderCreateTo = new orderCreateTo();
        String orderSn = IdWorker.getTimeId();
        //创建订单
        OrderEntity orderEntity = buildOrder(orderSn);
        //创建订单项
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        //封装订单金额相关信息
        computOrder(orderEntity, orderItemEntities);

        orderCreateTo.setOrderEntity(orderEntity);
        orderCreateTo.setOrderItems(orderItemEntities);
        orderCreateTo.setFare(orderEntity.getFreightAmount());
        orderCreateTo.setPayPrice(orderEntity.getPayAmount());

        return orderCreateTo;
    }

    private void computOrder(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {

        if (orderItemEntities != null && orderItemEntities.size() > 0) {

            BigDecimal totalAmount = new BigDecimal("0");
            BigDecimal promotion = new BigDecimal("0");
            BigDecimal coupon = new BigDecimal("0");
            BigDecimal integrationAmount = new BigDecimal("0");
            BigDecimal integration = new BigDecimal("0");
            BigDecimal growth = new BigDecimal("0");


            for (OrderItemEntity orderItemEntity : orderItemEntities) {
                totalAmount = totalAmount.add(orderItemEntity.getRealAmount());
                promotion = promotion.add(orderItemEntity.getPromotionAmount());
                coupon = coupon.add(orderItemEntity.getCouponAmount());
                integrationAmount = integrationAmount.add(orderItemEntity.getIntegrationAmount());
                integration = integration.add(new BigDecimal(orderItemEntity.getGiftIntegration()));
                growth = growth.add(new BigDecimal(orderItemEntity.getGiftGrowth()));
            }

            orderEntity.setPromotionAmount(promotion);
            orderEntity.setCouponAmount(coupon);
            orderEntity.setIntegrationAmount(integrationAmount);

            orderEntity.setIntegration(integration.intValue());
            orderEntity.setGrowth(growth.intValue());

            orderEntity.setTotalAmount(totalAmount);
            orderEntity.setPayAmount(totalAmount.add(orderEntity.getFreightAmount()));
        }
    }

    /**
     * 创建所有订单项信息
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) throws ExecutionException, InterruptedException {
        List<OrderItemVo> currentCartItems = cartFeignService.getCurrentCartItems();
        if (currentCartItems != null && currentCartItems.size() > 0) {
            List<OrderItemEntity> itemEntityList = currentCartItems.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = buildOrderItem(cartItem);
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            }).collect(Collectors.toList());
            return itemEntityList;
        }
        return null;
    }

    /**
     * 创建订单项信息
     *
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();

        //商品spu信息
        R r = productFeignService.getSpuInfoBySkuId(cartItem.getSkuId());
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(data.getId());
        orderItemEntity.setSpuName(data.getSpuName());
        orderItemEntity.setSpuBrand(data.getBrandId().toString());

        //商品sku信息
        orderItemEntity.setSkuId(cartItem.getSkuId());
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPic(cartItem.getImage());
        orderItemEntity.setSkuPrice(cartItem.getPrice());
        orderItemEntity.setSkuQuantity(cartItem.getCount());
        String attrValues = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(attrValues);
        // TODO: 2023/10/13  优惠信息

        //积分信息
        orderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity())).intValue());
        orderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity())).intValue());

        //金额信息
        orderItemEntity.setPromotionAmount(new BigDecimal("0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));

        BigDecimal multiply = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
        BigDecimal realAmount = multiply.subtract(orderItemEntity.getPromotionAmount()).subtract(orderItemEntity.getCouponAmount()).subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realAmount);

        return orderItemEntity;
    }

    private OrderEntity buildOrder(String orderSn) {
        MemberInfo memberInfo = LoginInterceptor.threadLocal.get();
        SubmitOrderVo submitOrderVo = submitOrderVoThreadLocal.get();
        R result = wareFeignService.getFare(submitOrderVo.getAddrId());
        OrderEntity orderEntity = new OrderEntity();
        FareVo fareVoResp = result.getData(new TypeReference<FareVo>() {
        });
        if (fareVoResp != null) {
            orderEntity.setOrderSn(orderSn);
            orderEntity.setFreightAmount(fareVoResp.getFare());
            orderEntity.setReceiverCity(fareVoResp.getAddressVo().getCity());
            orderEntity.setReceiverDetailAddress(fareVoResp.getAddressVo().getDetailAddress());
            orderEntity.setReceiverName(fareVoResp.getAddressVo().getName());
            orderEntity.setReceiverPhone(fareVoResp.getAddressVo().getPhone());
            orderEntity.setReceiverPostCode(fareVoResp.getAddressVo().getPostCode());
            orderEntity.setReceiverProvince(fareVoResp.getAddressVo().getProvince());
            orderEntity.setReceiverRegion(fareVoResp.getAddressVo().getRegion());
        }

        orderEntity.setMemberId(memberInfo.getId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);

        return orderEntity;

    }

}