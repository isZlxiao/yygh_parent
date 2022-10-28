package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void saveSchedule(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Schedule schedule = JSONObject.parseObject(jsonString, Schedule.class);

        Schedule targetSchedule = scheduleRepository.getByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());

        if(targetSchedule==null){
            // 保存
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else{
            // 更新
            schedule.setId(targetSchedule.getId());
            schedule.setCreateTime(targetSchedule.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(targetSchedule.getIsDeleted());
            scheduleRepository.save(schedule);
        }
    }

    // 查询排班获取分页列表
    @Override
    public Page<Schedule> selectPage(String hoscode, int page, int limit) {
        ScheduleQueryVo queryVo = new ScheduleQueryVo();
        queryVo.setHoscode(hoscode);

        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(schedule,queryVo);

        // 排序
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        // 模板
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule,exampleMatcher);
        // 分页
        Pageable pageable = PageRequest.of(page-1,limit,sort);

        Page<Schedule> pageModel = scheduleRepository.findAll(example, pageable);
        return pageModel;
    }

    // 删除排班
    @Override
    public void removeSchedule(String hoscode, String hosScheduleId) {
        Schedule target = scheduleRepository.getByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(target == null){
            throw new YyghException(20001,"未找到指定排班信息");
        }
        scheduleRepository.deleteById(target.getId());
    }
}
