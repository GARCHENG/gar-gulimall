package com.garcheng.gulimall.auth.feign;

import com.garcheng.gulimall.auth.vo.LoginVo;
import com.garcheng.gulimall.auth.vo.RegisterVo;
import com.garcheng.gulimall.auth.vo.SocialUser;
import com.garcheng.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("member/member/register")
    R register(@RequestBody RegisterVo registerVo);

    @RequestMapping("member/member/login")
    R login(@RequestBody LoginVo memberLoginVo);

    @RequestMapping("member/member/oauth/login")
    public R oauthLogin(@RequestBody SocialUser socialUser);
}
