package com.garcheng.gulimall.auth.controller;

import com.garcheng.gulimall.auth.constant.AuthRedisConstant;
import com.garcheng.gulimall.auth.feign.MemberFeignService;
import com.garcheng.gulimall.auth.service.SmsService;
import com.garcheng.gulimall.auth.vo.RegisterVo;
import com.garcheng.gulimall.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    SmsService smsService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MemberFeignService memberFeignService;


    @GetMapping("sendSmsCode")
    public R sendSmsCode(@RequestParam("phone") String phone){
        String code = (String) redisTemplate.opsForValue().get(AuthRedisConstant.SMS_CODE_PREFIX + phone);
        if (StringUtils.isEmpty(code)){
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
            redisTemplate.opsForValue().set(AuthRedisConstant.SMS_CODE_PREFIX+phone,code,3, TimeUnit.MINUTES);
            return R.ok();
        }else {
            return R.error("验证码已发，请稍后再试");
        }

    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterVo registerVo, BindingResult bindingResult, RedirectAttributesModelMap attributesModelMap){
        if ( bindingResult.hasErrors()){
            Map<String, String> errorMap = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,
                    (obj)-> obj.getDefaultMessage()));
            attributesModelMap.addFlashAttribute("errorMap",errorMap);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //先验证验证码
        String code = (String) redisTemplate.opsForValue().get(AuthRedisConstant.SMS_CODE_PREFIX + registerVo.getPhone());
        if (!StringUtils.isEmpty(code)){
            if (code.equals(registerVo.getCode())){
                // TODO: 2023/9/25  远程调用会员服务注册用户
                R result = memberFeignService.register(registerVo);
                if ("0".equals(result.getCode().toString())){

                }else {

                }
            }else {
                Map<String,String> errorMap = new HashMap<>();
                errorMap.put("code","验证码错误");
                attributesModelMap.addFlashAttribute("errorMap",errorMap);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        }else {
            Map<String,String> errorMap = new HashMap<>();
            errorMap.put("code","验证码已过期");
            attributesModelMap.addFlashAttribute("errorMap",errorMap);
            return "redirect:http://auth.gulimall.com/reg.html";
        }


    }
}
