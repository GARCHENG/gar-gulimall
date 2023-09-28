package com.garcheng.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.garcheng.gulimall.common.exception.BaseCodeEnum;
import com.garcheng.gulimall.member.exception.AccountNotFindException;
import com.garcheng.gulimall.member.exception.PasswordWrongException;
import com.garcheng.gulimall.member.exception.PhoneExitException;
import com.garcheng.gulimall.member.exception.UsernameExitException;
import com.garcheng.gulimall.member.vo.MemberLoginVo;
import com.garcheng.gulimall.member.vo.MemberRegisterVo;
import com.garcheng.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.garcheng.gulimall.member.entity.MemberEntity;
import com.garcheng.gulimall.member.service.MemberService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.R;



/**
 * 会员
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:05:31
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }

    @PostMapping("register")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo){
        try {
            memberService.register(memberRegisterVo);
            return R.ok();
        }catch (PhoneExitException e){
            return R.error(BaseCodeEnum.PHONE_EXIT_EXCEPTION.getCode(),BaseCodeEnum.PHONE_EXIT_EXCEPTION.getMessage());
        }catch (UsernameExitException e){
            return R.error(BaseCodeEnum.USERNAME_EXIT_EXCEPTION.getCode(),BaseCodeEnum.USERNAME_EXIT_EXCEPTION.getMessage());
        }
    }

    @RequestMapping("login")
    public R login(@RequestBody MemberLoginVo memberLoginVo){
        try {
            MemberEntity memberEntity = memberService.login(memberLoginVo);
            return R.ok().put("msg",memberEntity);
        }catch (PasswordWrongException e){
            return R.error(BaseCodeEnum.PASSWORD_WRONG_EXCEPTION.getCode(),BaseCodeEnum.PASSWORD_WRONG_EXCEPTION.getMessage());
        }catch (AccountNotFindException e){
            return R.error(BaseCodeEnum.ACCOUNT_NOT_FIND_EXCEPTION.getCode(),BaseCodeEnum.ACCOUNT_NOT_FIND_EXCEPTION.getMessage());
        }
    }

    @RequestMapping("/oauth/login")
    public R oauthLogin(@RequestBody SocialUser socialUser){
        MemberEntity memberEntity = null;
        try {
            memberEntity = memberService.oauthLoginOrRegister(socialUser);
            return R.ok().put("data",memberEntity);
        } catch (Exception e) {
            return R.error(BaseCodeEnum.OAUTH_LOGIN_EXCEPTION.getCode(),BaseCodeEnum.OAUTH_LOGIN_EXCEPTION.getMessage());
        }

    }



    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
