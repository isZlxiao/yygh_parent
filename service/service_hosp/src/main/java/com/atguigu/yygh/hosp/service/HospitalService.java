package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface HospitalService {

    // 保存医院信息
    public void saveHospital(Map<String, Object> paramMap);

    // 获取医院对象
    Hospital getHospital(String hoscode);

    // 获取分页列表
    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo queryVo);
}
