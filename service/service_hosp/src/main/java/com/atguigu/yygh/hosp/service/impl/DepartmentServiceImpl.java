package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Department dept = JSONObject.parseObject(jsonString, Department.class);

        Department targetDepartment = departmentRepository.getByHoscodeAndDepcode(dept.getHoscode(),dept.getDepcode());
        if(targetDepartment==null){
            // 新增
            dept.setCreateTime(new Date());
            dept.setUpdateTime(new Date());
            dept.setIsDeleted(0);
            departmentRepository.save(dept);
        }else{
            // 更新
            dept.setUpdateTime(new Date());
            dept.setId(targetDepartment.getId());
            dept.setCreateTime(targetDepartment.getCreateTime());
            dept.setIsDeleted(0);
            departmentRepository.save(dept);
        }

    }

    @Override
    public Page<Department> selectPage(int page, int limit, DepartmentQueryVo queryVo) {

            // 创建排序对象
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
            // 创建分页对象
        Pageable pageable = PageRequest.of((page-1),limit,sort);
        // 封装查询条件
        Department dep = new Department();
        BeanUtils.copyProperties(queryVo,dep);
        // 模板构造器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        // 创建查询条件模板
        Example<Department> example = Example.of(dep,exampleMatcher);

        // 分页查询
        Page<Department> pageModel = departmentRepository.findAll(example,pageable);
        return pageModel;
    }

    @Override
    public void removeDepartment(String hoscode, String depcode) {
        Department department = departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);
        if(department==null){
            throw new YyghException(20001,"未在数据库找到指定数据");
        }
        departmentRepository.deleteById(department.getId());
    }


}
