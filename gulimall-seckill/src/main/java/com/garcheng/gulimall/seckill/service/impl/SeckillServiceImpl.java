package com.garcheng.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.seckill.feign.CouponFeignService;
import com.garcheng.gulimall.seckill.feign.ProductFeignService;
import com.garcheng.gulimall.seckill.service.SeckillService;
import com.garcheng.gulimall.seckill.to.SeckillSkuRedisTo;
import com.garcheng.gulimall.seckill.vo.SeckillSessionVo;
import com.garcheng.gulimall.seckill.vo.SeckillSkuRelationVo;
import com.garcheng.gulimall.seckill.vo.SkuVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {

    public static final String SECKILL_SESSIONS_PREFIX = "seckill:sessions:";

    public static final String SECKILL_SKUS_PREFIX = "seckill:skus";

    public static final String SECKILL_STOCK_SEMAPHORE = "seckill:stock:";

    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public void uploadSeckillSkuLastest3Day() {
        R r = couponFeignService.getLastest3DaySession();
        if (r.getCode() == 0) {
            List<SeckillSessionVo> data = r.getData(new TypeReference<List<SeckillSessionVo>>() {});
            //保存秒杀活动信息至redis
            saveSeckillSessionInfoToRedis(data);
            //保存秒杀活动的商品信息至redis
            saveSeckillSkuInfoToRedis(data);
        }
    }

    private void saveSeckillSessionInfoToRedis(List<SeckillSessionVo> sessions) {
        if (sessions != null && sessions.size() >0){
            sessions.stream().forEach(session -> {
                long start = session.getStartTime().getTime();
                long end = session.getEndTime().getTime();
                String key = SECKILL_SESSIONS_PREFIX + start + "_" + end;
                if (!stringRedisTemplate.hasKey(key)){
                    List<SeckillSkuRelationVo> relationEntities = session.getRelationEntities();
                    if (relationEntities != null) {
                        List<String> skuIds = relationEntities.stream().map(relation -> relation.getId()+"_"+relation.getSkuId().toString())
                                .collect(Collectors.toList());
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
                        if (!ops.hasKey(seckillSku.getId()+"_"+seckillSku.getSkuId().toString())){
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

                            ops.put(seckillSku.getId()+"_"+seckillSku.getSkuId().toString(), JSON.toJSONString(seckillSkuRedisTo));
                        }
                    });
                }
            });
        }

    }


}
