package com.garcheng.gulimall.member.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MemberLoginVo {

    private String loginAccount;

    private String password;
}
