package com.garcheng.gulimall.order.interceptor;

import com.garcheng.gulimall.common.constant.AuthRedisConstant;
import com.garcheng.gulimall.common.vo.MemberInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberInfo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberInfo loginUser = (MemberInfo) request.getSession().getAttribute(AuthRedisConstant.LOGIN_USER);
        if (loginUser !=null){
            threadLocal.set(loginUser);
            return true;
        }else {
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }

    }
}
