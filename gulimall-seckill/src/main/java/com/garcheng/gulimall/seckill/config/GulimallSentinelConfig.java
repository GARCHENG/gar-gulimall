package com.garcheng.gulimall.seckill.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.garcheng.gulimall.common.exception.BaseCodeEnum;
import com.garcheng.gulimall.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class GulimallSentinelConfig {

    public GulimallSentinelConfig(){
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler(){
            @Override
            public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
                R error = R.error(BaseCodeEnum.TOO_MANY_REQUEST.getCode(), BaseCodeEnum.TOO_MANY_REQUEST.getMessage());
                httpServletResponse.setContentType("application/json");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.getWriter().write(JSON.toJSONString(error));
            }
        });
    }

}
