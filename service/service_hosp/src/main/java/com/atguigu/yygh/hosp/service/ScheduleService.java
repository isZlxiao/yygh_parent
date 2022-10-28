package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {
    // 上传排班
    void saveSchedule(Map<String, Object> map);
    // 查询排班获取分页列表
    Page<Schedule> selectPage(String hoscode, int page, int limit);
    // 删除排班
    void removeSchedule(String hoscode, String hosScheduleId);
}
