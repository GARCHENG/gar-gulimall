package com.garcheng.gulimall.member.dao;

import com.garcheng.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:05:31
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
