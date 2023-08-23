package com.garcheng.gulimall.product.exception;

import com.garcheng.gulimall.common.utils.R;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.garcheng.gulimall.product")
public class GlobalExceptionHandle {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        Map resultMap = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            resultMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        }
        return R.error().put("data",resultMap);
    }


}
