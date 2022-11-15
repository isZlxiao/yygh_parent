package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

    @Override
    public void saveSchedule(Map<String, Object> map) {
        String jsonString = JSONObject.toJSONString(map);
        Schedule schedule = JSONObject.parseObject(jsonString, Schedule.class);

        Schedule targetSchedule = scheduleRepository.getByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (targetSchedule == null) {
            // 保存
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        } else {
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
        BeanUtils.copyProperties(schedule, queryVo);

        // 排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 模板
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, exampleMatcher);
        // 分页
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<Schedule> pageModel = scheduleRepository.findAll(example, pageable);
        return pageModel;
    }

    // 删除排班
    @Override
    public void removeSchedule(String hoscode, String hosScheduleId) {
        Schedule target = scheduleRepository.getByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (target == null) {
            throw new YyghException(20001, "未找到指定排班信息");
        }
        scheduleRepository.deleteById(target.getId());
    }

    // 根据医院编号和科室编号 ，查询排班规则数据
    @Override
    public Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String depcode) {
        /*Map<String, Object> result = new HashMap<>();

        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),            //查找条件
                Aggregation.group("workDate")   //分组，查找
                        .first("wordDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate"), // 排序
                Aggregation.skip((page-1)*limit),        // 分页
                Aggregation.limit(limit)
                );
        AggregationResults<BookingScheduleRuleVo> aggRes = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingList = aggRes.getMappedResults();

        Aggregation agTotal = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> agTotalRes = mongoTemplate.aggregate(agTotal, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mapRes = agTotalRes.getMappedResults();
        int total = mapRes.size();

        // 转换为周几
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingList) {
            String dayOfWeek = this.getDayOfWeek(new DateTime(bookingScheduleRuleVo.getWorkDate()));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }
        String hosname = hospitalService.getHospName(hoscode);

        result.put("total",total);
        result.put("bookingScheduleRuleList",bookingList);

        Map<String,String> other = new HashMap<>();
        other.put("hosname",hosname);
        result.put("baseMap",other);

        return result;*/  // <--> my code
        //1创建最终返回对象
        Map<String, Object> result = new HashMap<>();
        //2根据筛选、分页条件进行聚合查询（List<BookingScheduleRuleVo>）
        //2.1设置筛选条件对象
        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode);
        //2.2创建聚合查询对象（拼写语句）
        Aggregation agg = Aggregation.newAggregation(
                //2.2.1设置筛选条件
                Aggregation.match(criteria),
                //2.2.2设置分组聚合信息（分组字段+统计字段）
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //2.2.3设置排序
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                //2.2.4设置分页
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        //2.3执行聚合查询
        AggregationResults<BookingScheduleRuleVo> aggregate =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregate.getMappedResults();

        //3根据筛选条件进行聚合查询(total)
        Aggregation aggTotal = Aggregation.newAggregation(
                //2.2.1设置筛选条件
                Aggregation.match(criteria),
                //2.2.2设置分组聚合信息（分组字段+统计字段）
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregateTotal =
                mongoTemplate.aggregate(aggTotal, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> totalList = aggregateTotal.getMappedResults();
        int total = totalList.size();

        //4遍历集合使用工具把排班日期转化成周几
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }
        //5封装数据返回
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);
        result.put("total",total);

        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hosName);
        result.put("baseMap",baseMap);
        return result;

    }

    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        // 查询集合
        List<Schedule> list = scheduleRepository
                .getByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());

        // 翻译字段
        list.forEach(this::packSchedule);

        return list;
    }

    private Schedule packSchedule(Schedule item) {
        // 医院名称
        item.getParam().put("hosname",hospitalService.getHospName(item.getHoscode()));
        // 科室名称
        item.getParam().put("depname",departmentService.getDeptname(item.getHoscode(),item.getDepcode()));
        // 设置对应星期
        item.getParam().put("dayOfWeak",this.getDayOfWeek(new DateTime(item.getWorkDate())));
        return item;
    }


    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

}
