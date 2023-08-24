package com.garcheng.gulimall.common.exception;

import lombok.Data;

public enum BaseCodeEnum {

    UNKONW_EXCEPTION(10000,"系统未知异常"),

    VAILD_EXCEPTION(10001,"参数校验失败");

    private Integer code;

    private String message;

    BaseCodeEnum(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
