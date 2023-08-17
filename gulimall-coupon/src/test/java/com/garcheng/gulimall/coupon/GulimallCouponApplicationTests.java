package com.garcheng.gulimall.coupon;

import com.garcheng.gulimall.coupon.entity.HomeSubjectEntity;
import com.garcheng.gulimall.coupon.service.HomeSubjectService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

}
