package com.garcheng.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.garcheng.gulimall.auth.feign.MemberFeignService;
import com.garcheng.gulimall.auth.vo.MemberResponseVo;
import com.garcheng.gulimall.auth.vo.SocialUser;
import com.garcheng.gulimall.common.utils.HttpUtils;
import com.garcheng.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/oauth2.0")
@Slf4j
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;


    @GetMapping("/weibo/success")
    public String oauthWeiboSuccess(String code, HttpSession session) throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("client_id","1990736870");
        map.put("client_secret","3b14e3d820802d615a5d4a8e64b3c47e");
        map.put("grant_type","authorization_code");
        map.put("code",code);
        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");

        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, new HashMap<>());

        if (response.getStatusLine().getStatusCode() == 200){
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            R result = memberFeignService.oauthLogin(socialUser);
            if (result.getCode() == 0){
                MemberResponseVo memberResponseVo = result.getData(new TypeReference<MemberResponseVo>() {});
                log.info("用户：{} 登录成功",memberResponseVo.getUsername());
                session.setAttribute("loginUser",memberResponseVo);
                return "redirect:http://gulimall.com";
            }else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        }else {
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }


}
