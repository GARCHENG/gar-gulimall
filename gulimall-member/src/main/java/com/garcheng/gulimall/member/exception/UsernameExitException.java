package com.garcheng.gulimall.member.exception;

public class UsernameExitException extends RuntimeException{
    public UsernameExitException() {
        super("用户名已存在");
    }
}
