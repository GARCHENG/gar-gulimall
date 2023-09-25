package com.garcheng.gulimall.auth.feign;

import com.garcheng.gulimall.auth.vo.RegisterVo;
import com.garcheng.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("register")
    public R register(@RequestBody RegisterVo registerVo);
}
