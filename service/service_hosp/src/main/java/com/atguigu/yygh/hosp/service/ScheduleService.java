package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    // 上传排班
    void saveSchedule(Map<String, Object> map);
    // 查询排班获取分页列表
    Page<Schedule> selectPage(String hoscode, int page, int limit);
    // 删除排班
    void removeSchedule(String hoscode, String hosScheduleId);
    // 根据医院编号和科室编号，查询排班规则数据
    Map<String,Object> getScheduleRule(Long page, Long limit, String hoscode, String depcode);
    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);
}
