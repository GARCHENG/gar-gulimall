package com.garcheng.gulimall.cart.interceptor;

import com.garcheng.gulimall.cart.vo.UserInfoTo;
import com.garcheng.gulimall.common.constant.AuthRedisConstant;
import com.garcheng.gulimall.common.constant.CartConstant;
import com.garcheng.gulimall.common.vo.MemberInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        UserInfoTo userInfoTo = new UserInfoTo();
        MemberInfo loginUser = (MemberInfo) session.getAttribute(AuthRedisConstant.LOGIN_USER);
        if (loginUser != null) {
            userInfoTo.setUserId(loginUser.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (CartConstant.TEMP_USER_COOKIES_NAME.equals(cookie.getName())){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }

        if (StringUtils.isEmpty(userInfoTo.getUserKey())){
            userInfoTo.setUserKey(UUID.randomUUID().toString());
        }

        threadLocal.set(userInfoTo);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        if (!userInfoTo.getTempUser()){
            Cookie cookies = new Cookie(CartConstant.TEMP_USER_COOKIES_NAME, userInfoTo.getUserKey());
            cookies.setDomain("gulimall.com");
            cookies.setMaxAge(CartConstant.TEMP_USER_COOKIES_TIMEOUT);
            response.addCookie(cookies);
        }
    }
}
