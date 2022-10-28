package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>
                                    implements HospitalSetService {



    // 获取签名
    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        if(hospitalSet==null) throw new YyghException(20001,"获取签名失败");
        System.out.println("hospitalSet.getSignKey() = " + hospitalSet.getSignKey());
        return hospitalSet.getSignKey();
    }
}
