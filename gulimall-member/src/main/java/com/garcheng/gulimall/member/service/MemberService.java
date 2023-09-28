package com.garcheng.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.member.entity.MemberEntity;
import com.garcheng.gulimall.member.exception.AccountNotFindException;
import com.garcheng.gulimall.member.exception.PasswordWrongException;
import com.garcheng.gulimall.member.exception.PhoneExitException;
import com.garcheng.gulimall.member.exception.UsernameExitException;
import com.garcheng.gulimall.member.vo.MemberLoginVo;
import com.garcheng.gulimall.member.vo.MemberRegisterVo;
import com.garcheng.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:05:31
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo memberRegisterVo) throws PhoneExitException, UsernameExitException;

    MemberEntity login(MemberLoginVo memberLoginVo) throws PasswordWrongException, AccountNotFindException;

    MemberEntity oauthLoginOrRegister(SocialUser socialUser) throws Exception;
}

