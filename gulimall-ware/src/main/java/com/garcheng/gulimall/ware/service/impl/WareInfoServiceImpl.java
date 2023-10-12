package com.garcheng.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.ware.feign.MemberFeignService;
import com.garcheng.gulimall.ware.vo.MemberAddressVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.Query;

import com.garcheng.gulimall.ware.dao.WareInfoDao;
import com.garcheng.gulimall.ware.entity.WareInfoEntity;
import com.garcheng.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and(obj -> {
                obj.eq("id",key)
                        .or()
                        .like("name",key)
                        .or()
                        .like("address",key);
            });
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public BigDecimal getFare(Long addrId) {
        R data = memberFeignService.info(addrId);
        MemberAddressVo memberAddressVo = data.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {});
        if (memberAddressVo != null) {
            String phone = memberAddressVo.getPhone();
            return new BigDecimal(phone.substring(phone.length()-2,phone.length()));
        }
        return null;
    }

}