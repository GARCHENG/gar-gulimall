package com.garcheng.gulimall.common.exception;


public enum BaseCodeEnum {

    UNKONW_EXCEPTION(10000,"系统未知异常"),

    VAILD_EXCEPTION(10001,"参数校验失败"),

    PRODUCT_UP_EXCEPTION(11000,"商品上架失败"),

    PHONE_EXIT_EXCEPTION(12000,"手机号已存在"),

    USERNAME_EXIT_EXCEPTION(12001,"用户名已存在"),

    PASSWORD_WRONG_EXCEPTION(12002,"密码错误"),

    ACCOUNT_NOT_FIND_EXCEPTION(12003,"没有找到该用户");

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
