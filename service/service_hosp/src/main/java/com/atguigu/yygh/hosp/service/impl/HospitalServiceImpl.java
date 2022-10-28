package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {


    @Autowired
    private HospitalRepository hospitalRepository;


    // 保存医院信息
    @Override
    public void saveHospital(Map<String, Object> paramMap) {
        //1实现参数转型 Map<String, Object> =>json串  => Hospital
        String jsonString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);

        //2根据hoscode查询数据库获取医院信息
        Hospital targetHospital = hospitalRepository.getByHoscode(hospital.getHoscode());
        if(targetHospital==null){
            // 保存新增
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setStatus(0);
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{
            // 更新
            hospital.setId(targetHospital.getId());
            hospital.setCreateTime(targetHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setStatus(targetHospital.getStatus());
            hospital.setIsDeleted(targetHospital.getIsDeleted());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getHospital(String hoscode) {
        return hospitalRepository.getByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo queryVo) {
        //1创建分页对象
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        Pageable pageable = PageRequest.of(page-1,limit,sort);
        //2创建查询条件模板
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(queryVo,hospital);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Hospital> example = Example.of(hospital,matcher);
        //3带条件分页查询
        Page<Hospital> pageModel = hospitalRepository.findAll(example, pageable);
        //4 TODO 遍历集合、跨模块（cmn）翻译字段用于显示

        return pageModel;
    }
}
