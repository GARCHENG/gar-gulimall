package com.garcheng.gulimall.member.exception;

public class PhoneExitException extends RuntimeException{
    public PhoneExitException() {
        super("手机号已存在");
    }
}
