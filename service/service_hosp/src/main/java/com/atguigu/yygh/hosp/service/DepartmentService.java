package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    // 上传科室
    void saveDepartment(Map<String, Object> map);
    // 查询分页
    Page<Department> selectPage(int page, int limit, DepartmentQueryVo queryVo);

    // 删除科室
    void removeDepartment(String hoscode, String depcode);

    // 查询所有科室列表
    List<DepartmentVo> findDeptTree(String hoscode);


    // 查询科室名称
    String getDeptname(String hoscode, String depcode);
}
