package com.garcheng.gulimall.coupon.service.impl;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.coupon.dao.SeckillSessionDao;
import com.garcheng.gulimall.coupon.entity.SeckillSessionEntity;
import com.garcheng.gulimall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLastest3DaySession() {
        List<SeckillSessionEntity> list = list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime(),endTime()));
        return list;
    }

    private String startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        LocalDateTime dateTime = LocalDateTime.of(now, min);
        String start = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return start;
    }

    private String endTime() {
        LocalDate endDay = LocalDate.now().plus(Duration.ofDays(2));
        LocalTime max = LocalTime.MAX;
        LocalDateTime endDateTime = LocalDateTime.of(endDay, max);
        String end = endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return end;
    }

}