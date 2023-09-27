package com.garcheng.gulimall.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginVo {

    @NotEmpty(message = "账号不能为空")
    private String loginAccount;

    @NotEmpty(message = "密码不能为空")
    private String password;

}
