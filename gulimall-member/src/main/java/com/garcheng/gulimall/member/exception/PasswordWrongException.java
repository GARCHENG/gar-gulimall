package com.garcheng.gulimall.member.exception;

public class PasswordWrongException extends RuntimeException{
    public PasswordWrongException(){
        super("密码错误");
    }
}
