package com.garcheng.gulimall.member.service.impl;

import com.garcheng.gulimall.member.entity.MemberLevelEntity;
import com.garcheng.gulimall.member.exception.PhoneExitException;
import com.garcheng.gulimall.member.exception.UsernameExitException;
import com.garcheng.gulimall.member.service.MemberLevelService;
import com.garcheng.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public void register(MemberRegisterVo memberRegisterVo) {
        MemberEntity memberEntity = new MemberEntity();

        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        isExitUsername(memberRegisterVo.getUsername());
        memberEntity.setUsername(memberRegisterVo.getUsername());

        isExitMobile(memberRegisterVo.getPhone());
        memberEntity.setMobile(memberRegisterVo.getPhone());



    }

    private void isExitMobile(String phone) {
        int count = count(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count == 0){
            throw new PhoneExitException();
        }
    }

    private void isExitUsername(String username) {
        int count = count(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count == 0){
            throw new UsernameExitException();
        }
    }

}