package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {
    // 上传科室
    void saveDepartment(Map<String, Object> map);
    // 查询分页
    Page<Department> selectPage(int page, int limit, DepartmentQueryVo queryVo);

    // 删除科室
    void removeDepartment(String hoscode, String depcode);
}
