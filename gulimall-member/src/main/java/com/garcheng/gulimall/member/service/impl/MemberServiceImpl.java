package com.garcheng.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.garcheng.gulimall.common.utils.HttpUtils;
import com.garcheng.gulimall.member.entity.MemberLevelEntity;
import com.garcheng.gulimall.member.exception.AccountNotFindException;
import com.garcheng.gulimall.member.exception.PasswordWrongException;
import com.garcheng.gulimall.member.exception.PhoneExitException;
import com.garcheng.gulimall.member.exception.UsernameExitException;
import com.garcheng.gulimall.member.service.MemberLevelService;
import com.garcheng.gulimall.member.vo.MemberLoginVo;
import com.garcheng.gulimall.member.vo.MemberRegisterVo;
import com.garcheng.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.member.dao.MemberDao;
import com.garcheng.gulimall.member.entity.MemberEntity;
import com.garcheng.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo memberRegisterVo) throws PhoneExitException, UsernameExitException {
        MemberEntity memberEntity = new MemberEntity();

        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        isExitUsername(memberRegisterVo.getUsername());
        memberEntity.setUsername(memberRegisterVo.getUsername());

        isExitMobile(memberRegisterVo.getPhone());
        memberEntity.setMobile(memberRegisterVo.getPhone());

        memberEntity.setNickname(memberRegisterVo.getUsername());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        memberEntity.setPassword(encoder.encode(memberRegisterVo.getPassword()));

        this.save(memberEntity);

    }

    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) throws PasswordWrongException, AccountNotFindException {
        QueryWrapper<MemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", memberLoginVo.getLoginAccount()).or().eq("mobile", memberLoginVo.getLoginAccount());
        MemberEntity memberEntity = getOne(queryWrapper);
        if (memberEntity != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean matches = encoder.matches(memberLoginVo.getPassword(), memberEntity.getPassword());
            if (matches) {
                return memberEntity;
            } else {
                throw new PasswordWrongException();
            }
        } else {
            throw new AccountNotFindException();
        }

    }

    @Override
    public MemberEntity oauthLoginOrRegister(SocialUser socialUser) throws Exception {
        MemberEntity one = getOne(new QueryWrapper<MemberEntity>().eq("oauth_uid", socialUser.getUid()));
        if (one != null) {
            one.setAccessToken(socialUser.getAccess_token());
            one.setExpiresIn(socialUser.getExpires_in());
            updateById(one);
            return one;
        } else {
            MemberEntity create = new MemberEntity();
            Map<String, String> querys = new HashMap<>();
            querys.put("access_token", socialUser.getAccess_token());
            querys.put("uid", socialUser.getUid());
            HttpResponse httpResponse = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), querys);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String gender = (String) jsonObject.get("gender");

                create.setNickname((String) jsonObject.get("name"));
                create.setGender("m".equals(gender) ? 1 : 0);
                create.setHeader((String) jsonObject.get("profile_image_url"));

                MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
                create.setLevelId(memberLevelEntity.getId());

                create.setAccessToken(socialUser.getAccess_token());
                create.setOauthUid(socialUser.getUid());
                create.setExpiresIn(socialUser.getExpires_in());

                save(create);
            }
            return create;
        }
    }

    private void isExitMobile(String phone) throws PhoneExitException {
        int count = count(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count != 0) {
            throw new PhoneExitException();
        }
    }

    private void isExitUsername(String username) throws UsernameExitException {
        int count = count(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count != 0) {
            throw new UsernameExitException();
        }
    }

}