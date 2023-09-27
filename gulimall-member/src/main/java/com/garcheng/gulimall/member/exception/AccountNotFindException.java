package com.garcheng.gulimall.member.exception;

public class AccountNotFindException extends RuntimeException{
    public AccountNotFindException(){
        super("没有该用户");
    }
}
