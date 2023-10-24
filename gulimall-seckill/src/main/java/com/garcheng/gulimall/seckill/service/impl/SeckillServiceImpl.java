package com.garcheng.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.garcheng.gulimall.common.to.mq.SeckillOrderTo;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.seckill.feign.CouponFeignService;
import com.garcheng.gulimall.seckill.feign.ProductFeignService;
import com.garcheng.gulimall.seckill.interceptor.LoginInterceptor;
import com.garcheng.gulimall.seckill.service.SeckillService;
import com.garcheng.gulimall.seckill.to.SeckillSkuRedisTo;
import com.garcheng.gulimall.seckill.vo.SeckillSessionVo;
import com.garcheng.gulimall.seckill.vo.SeckillSkuRelationVo;
import com.garcheng.gulimall.seckill.vo.SkuVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    public static final String SECKILL_SESSIONS_PREFIX = "seckill:sessions:";

    public static final String SECKILL_SKUS_PREFIX = "seckill:skus";

    public static final String SECKILL_USERS_PREFIX = "seckill:users:";

    public static final String SECKILL_STOCK_SEMAPHORE = "seckill:stock:";

    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void uploadSeckillSkuLastest3Day() {
        R r = couponFeignService.getLastest3DaySession();
        if (r.getCode() == 0) {
            List<SeckillSessionVo> data = r.getData(new TypeReference<List<SeckillSessionVo>>() {
            });
            //保存秒杀活动信息至redis
            saveSeckillSessionInfoToRedis(data);
            //保存秒杀活动的商品信息至redis
            saveSeckillSkuInfoToRedis(data);
            // TODO: 2023/10/24 去库存服务进行锁库存
        }
    }


    private void saveSeckillSessionInfoToRedis(List<SeckillSessionVo> sessions) {
        if (sessions != null && sessions.size() > 0) {
            sessions.stream().forEach(session -> {
                long start = session.getStartTime().getTime();
                long end = session.getEndTime().getTime();
                String key = SECKILL_SESSIONS_PREFIX + start + "_" + end;
                if (!stringRedisTemplate.hasKey(key)) {
                    List<SeckillSkuRelationVo> relationEntities = session.getRelationEntities();
                    if (relationEntities != null) {
                        // TODO: 2023/10/23 后面添加关联的商品无法添加至redis
                        List<String> skuIds = relationEntities.stream().map(relation -> relation.getPromotionSessionId() + "_" + relation.getSkuId().toString())
                                .collect(Collectors.toList());
                        // TODO: 2023/10/24 设置过期时间
                        stringRedisTemplate.opsForList().leftPushAll(key, skuIds);
                    }
                }
            });
        }
    }

    private void saveSeckillSkuInfoToRedis(List<SeckillSessionVo> sessions) {
        if (sessions != null && sessions.size() > 0) {
            sessions.stream().forEach(session -> {
                BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SECKILL_SKUS_PREFIX);

                List<SeckillSkuRelationVo> relationEntities = session.getRelationEntities();
                if (relationEntities != null) {
                    relationEntities.stream().forEach(seckillSku -> {
                        if (!ops.hasKey(seckillSku.getPromotionSessionId() + "_" + seckillSku.getSkuId().toString())) {
                            SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();
                            BeanUtils.copyProperties(seckillSku, seckillSkuRedisTo);
                            //查找sku基本信息
                            R r = productFeignService.getSkuInfo(seckillSku.getSkuId());
                            if (r.getCode() == 0) {
                                SkuVo skuInfo = r.getData("skuInfo", new TypeReference<SkuVo>() {
                                });
                                seckillSkuRedisTo.setSkuVo(skuInfo);
                            }
                            //设置时间
                            seckillSkuRedisTo.setStartTime(session.getStartTime().getTime());
                            seckillSkuRedisTo.setEndTime(session.getEndTime().getTime());
                            //设置商品的秒杀随机码
                            String token = UUID.randomUUID().toString().replace("-", "");
                            seckillSkuRedisTo.setRandomCode(token);
                            //设置分布式库存信号量(redis) 带随机码来减 限流
                            RSemaphore semaphore = redissonClient.getSemaphore(SECKILL_STOCK_SEMAPHORE + token);
                            semaphore.trySetPermits(Integer.parseInt(seckillSku.getSeckillCount().toString()));

                            // TODO: 2023/10/24 设置过期时间
                            ops.put(seckillSku.getPromotionSessionId() + "_" + seckillSku.getSkuId().toString(), JSON.toJSONString(seckillSkuRedisTo));
                        }
                    });
                }
            });
        }

    }


    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillProduct() {
        long currentTime = new Date().getTime();
        Set<String> sessionKeys = stringRedisTemplate.keys(SECKILL_SESSIONS_PREFIX + "*");
        if (sessionKeys != null && sessionKeys.size() > 0) {
            for (String sessionKey : sessionKeys) {
                String timeRange = sessionKey.replace("seckill:sessions:", "");
                String[] split = timeRange.split("_");
                if (currentTime >= Long.parseLong(split[0]) && currentTime <= Long.parseLong(split[1])) {
                    List<String> range = stringRedisTemplate.opsForList().range(sessionKey, -100, 100);
                    if (range != null && range.size() > 0) {
                        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps("seckill:skus");
                        List<String> skuJsons = hashOps.multiGet(range);
                        if (skuJsons != null) {
                            List<SeckillSkuRedisTo> collect = skuJsons.stream().map(skuJson -> {
                                SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(skuJson, SeckillSkuRedisTo.class);
                                seckillSkuRedisTo.setRandomCode(null);
                                return seckillSkuRedisTo;
                            }).collect(Collectors.toList());
                            return collect;
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public SeckillSkuRedisTo getSeckillInfoBySkuId(Long skuId) {
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SECKILL_SKUS_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null) {
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                if (key.matches(regx)) {
                    String skuSeckillJson = hashOps.get(key);
                    SeckillSkuRedisTo to = JSON.parseObject(skuSeckillJson, SeckillSkuRedisTo.class);
                    // TODO: 2023/10/23 随机码处理
                    //判断当前时间是不是秒杀时间
                    Long startTime = to.getStartTime();
                    Long endTime = to.getEndTime();
                    long current = new Date().getTime();
                    if (current >= startTime && current <= endTime) {
                    } else {
                        to.setRandomCode(null);
                    }
                    return to;
                }
            }
        }
        return null;
    }

    // TODO: 2023/10/24 过期时间设置
    // TODO: 2023/10/24 秒杀后续流程 收货地址选择
    @Override
    public String kill(String killId, String key, Integer num) {
        //校验合法性
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SECKILL_SKUS_PREFIX);
        String seckillJson = hashOps.get(killId);
        if (seckillJson != null) {
            SeckillSkuRedisTo redisTo = JSON.parseObject(seckillJson, SeckillSkuRedisTo.class);
            //校验是否在秒杀期内
            long current = new Date().getTime();
            if (current >= redisTo.getStartTime() && current <= redisTo.getEndTime()){
                //校验key是否与该商品的随机码一致
                if (key.equals(redisTo.getRandomCode()) && killId.equals(redisTo.getPromotionSessionId()+"_"+ redisTo.getSkuId())){
                    //校验购买数量是否符合要求
                    if (num <= Integer.parseInt(redisTo.getSeckillLimit().toString())){
                        //校验该用户是否重复购买 秒杀成功后去redis进行占位
                        //userid_sessionId_skuId
                        String userKey = SECKILL_USERS_PREFIX+LoginInterceptor.threadLocal.get().getId()+"_"+redisTo.getPromotionSessionId()+"_"+redisTo.getSkuId();
                        // TODO: 2023/10/24 若用户购买数量未达限制数量并再次购买？
                        Long timeOut = Math.abs(current - redisTo.getEndTime());
                        Boolean absent = stringRedisTemplate.opsForValue().setIfAbsent(userKey, num.toString(), timeOut, TimeUnit.MILLISECONDS);
                        if (absent){
                            //该账号未重复购买 去尝试获取信号量
                            RSemaphore semaphore = redissonClient.getSemaphore(SECKILL_STOCK_SEMAPHORE + key);
                            boolean kill = semaphore.tryAcquire(num);
                            if (kill){
                                //秒杀成功 快速返回 发mq下单 进行流量的削峰
                                String orderSn = IdWorker.getTimeId();
                                SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
                                seckillOrderTo.setOrderSn(orderSn);
                                seckillOrderTo.setCount(num);
                                seckillOrderTo.setSeckillPrice(redisTo.getSeckillPrice());
                                seckillOrderTo.setSkuId(redisTo.getSkuId());
                                seckillOrderTo.setSessionId(redisTo.getPromotionSessionId());
                                seckillOrderTo.setMemberId(LoginInterceptor.threadLocal.get().getId());
                                rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order",seckillOrderTo);
                                return orderSn;
                            }else {
                                stringRedisTemplate.delete(userKey);
                                log.warn("【"+userKey+"】秒杀失败,信号量获取失败");
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


}
