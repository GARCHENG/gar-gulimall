package com.garcheng.gulimall.product.exception;

import com.garcheng.gulimall.common.exception.BaseCodeEnum;
import com.garcheng.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.garcheng.gulimall.product")
public class GlobalExceptionHandle {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        log.error("数据校验除了问题{},异常类型为：{}",exception.getMessage(),exception.getClass());
        Map resultMap = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            resultMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        }
        return R.error(BaseCodeEnum.VAILD_EXCEPTION.getCode(),BaseCodeEnum.VAILD_EXCEPTION.getMessage()).put("data",resultMap);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("数据校验除了问题{},异常类型为：{}",throwable.getMessage(),throwable.getClass());
        return R.error(BaseCodeEnum.UNKONW_EXCEPTION.getCode(),BaseCodeEnum.UNKONW_EXCEPTION.getMessage());
    }


}
