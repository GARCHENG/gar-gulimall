package com.garcheng.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.garcheng.gulimall.common.constant.AuthRedisConstant;
import com.garcheng.gulimall.auth.feign.MemberFeignService;
import com.garcheng.gulimall.auth.service.SmsService;
import com.garcheng.gulimall.auth.vo.LoginVo;
import com.garcheng.gulimall.auth.vo.RegisterVo;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.common.vo.MemberInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.http.HttpSession;
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
    StringRedisTemplate redisTemplate;
    @Autowired
    MemberFeignService memberFeignService;


    @GetMapping("sendSmsCode")
    @ResponseBody
    public R sendSmsCode(@RequestParam("phone") String phone) {
        String code = (String) redisTemplate.opsForValue().get(AuthRedisConstant.SMS_CODE_PREFIX + phone);
        if (StringUtils.isEmpty(code)) {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 5);
            redisTemplate.opsForValue().set(AuthRedisConstant.SMS_CODE_PREFIX + phone, code, 3, TimeUnit.MINUTES);
            return R.ok();
        } else {
            return R.error("验证码已发，请稍后再试");
        }

    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object loginUser = session.getAttribute(AuthRedisConstant.LOGIN_USER);
        if (loginUser != null){
            return "redirect:http://gulimall.com";
        }
        return "login";
    }

    @PostMapping("/register")
    public String register(@Valid RegisterVo registerVo, BindingResult bindingResult, RedirectAttributesModelMap attributesModelMap) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,
                    (obj) -> obj.getDefaultMessage()));
            attributesModelMap.addFlashAttribute("errors", errorMap);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //先验证验证码
        String code = (String) redisTemplate.opsForValue().get(AuthRedisConstant.SMS_CODE_PREFIX + registerVo.getPhone());
        if (!StringUtils.isEmpty(code)) {
            if (code.equals(registerVo.getCode())) {
                redisTemplate.delete(AuthRedisConstant.SMS_CODE_PREFIX + registerVo.getPhone());
                // TODO: 2023/9/25  远程调用会员服务注册用户
                R result = memberFeignService.register(registerVo);
                if ("0".equals(result.getCode().toString())) {
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("code", result.get("msg").toString());
                    attributesModelMap.addFlashAttribute("errors", errorMap);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            } else {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("code", "验证码错误");
                attributesModelMap.addFlashAttribute("errors", errorMap);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "验证码已过期");
            attributesModelMap.addFlashAttribute("errors", errorMap);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

    }

    @PostMapping("/login")
    public String login(LoginVo loginVo, BindingResult bindingResult,
                        RedirectAttributesModelMap attributesModelMap,
                        HttpSession session){
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,
                    (obj) -> obj.getDefaultMessage()));
            attributesModelMap.addFlashAttribute("errors", errorMap);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        R loginResult = memberFeignService.login(loginVo);
        if (loginResult.getCode() == 0){
            MemberInfo memberResponseVo = loginResult.getData(new TypeReference<MemberInfo>() {});
            session.setAttribute(AuthRedisConstant.LOGIN_USER,memberResponseVo);
            return "redirect:http://gulimall.com";
        }else {
            Map<Object, String> errorMap = new HashMap<>();
            errorMap.put("code",loginResult.get("msg").toString());
            attributesModelMap.addFlashAttribute("errors",errorMap);
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }
}
