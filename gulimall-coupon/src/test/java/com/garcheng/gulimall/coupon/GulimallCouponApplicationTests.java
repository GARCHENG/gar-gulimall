package com.garcheng.gulimall.coupon;

import com.garcheng.gulimall.coupon.entity.HomeSubjectEntity;
import com.garcheng.gulimall.coupon.service.HomeSubjectService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
class GulimallCouponApplicationTests {

    @Autowired
    private HomeSubjectService homeSubjectService;

    @Test
    void contextLoads() {
        List<HomeSubjectEntity> list = homeSubjectService.list();
        list.forEach(e-> System.out.println(e));
    }

    @Test
    public void test1() {
        LocalDate endDay = LocalDate.now().plusDays(2);
        LocalTime max = LocalTime.MAX;
        LocalDateTime endDateTime = LocalDateTime.of(endDay, max);
        String end = endDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(end);
    }

}
