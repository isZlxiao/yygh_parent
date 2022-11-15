package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Department dept = JSONObject.parseObject(jsonString, Department.class);

        Department targetDepartment = departmentRepository.getByHoscodeAndDepcode(dept.getHoscode(), dept.getDepcode());
        if (targetDepartment == null) {
            // 新增
            dept.setCreateTime(new Date());
            dept.setUpdateTime(new Date());
            dept.setIsDeleted(0);
            departmentRepository.save(dept);
        } else {
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
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 创建分页对象
        Pageable pageable = PageRequest.of((page - 1), limit, sort);
        // 封装查询条件
        Department dep = new Department();
        BeanUtils.copyProperties(queryVo, dep);
        // 模板构造器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        // 创建查询条件模板
        Example<Department> example = Example.of(dep, exampleMatcher);

        // 分页查询
        Page<Department> pageModel = departmentRepository.findAll(example, pageable);
        return pageModel;
    }

    @Override
    public void removeDepartment(String hoscode, String depcode) {
        Department department = departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);
        if (department == null) {
            throw new YyghException(20001, "未在数据库找到指定数据");
        }
        departmentRepository.deleteById(department.getId());
    }

    // 查询所有科室列表
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //1、创建最终返回集合
        List<DepartmentVo> result = new ArrayList<>();
        //2根据hoscode查询所有科室信息List<Department>
        List<Department> allDept = departmentRepository.getByHoscode(hoscode);
        //3把List<Department>转化成Map(实现分组) k:bigcode  v:List<Department>
        Map<String, List<Department>> deptMap =     // key: bigcode  value: list<Department>
                allDept.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //4遍历map，封装大科室信息
        for (Map.Entry<String, List<Department>> entry : deptMap.entrySet()) {
            DepartmentVo temp = new DepartmentVo();
            temp.setDepcode(entry.getKey());
            temp.setDepname(entry.getValue().get(0).getBigname());
            //5封装小科室信息，存入集合
            List<DepartmentVo> tempList = new ArrayList<>();
            for (Department department : entry.getValue()) {
                DepartmentVo deptVo = new DepartmentVo();
                BeanUtils.copyProperties(department, deptVo);
                tempList.add(deptVo);
            }
            //6小科室信息集合存入大科室对象
            temp.setChildren(tempList);
            //7大科室对象存入最终返回集合
            result.add(temp);
        }

        return result;
    }

    @Override
    public String getDeptname(String hoscode, String depcode) {
        Department tempDep = departmentRepository.getByHoscodeAndDepcode(hoscode, depcode);
        if(tempDep==null) throw new YyghException(20001,"未找到指定科室");
        return tempDep.getDepname();
    }


}
