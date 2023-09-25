package com.garcheng.gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class RegisterVo {

    @NotEmpty(message = "用户名不能为空!")
    @Length(min = 6,max = 18,message = "用户名需包含6~18个字符")
    private String username;

    @NotEmpty(message = "密码不能为空!")
    @Length(min = 6,max = 18,message = "密码需包含6~18个字符")
    private String password;

    @NotEmpty(message = "手机号不能为空!")
    @Pattern(regexp = "^1[345789]\\d{9}$")
    private String phone;

    @NotEmpty(message = "验证码不能为空!")
    private String code;

}
