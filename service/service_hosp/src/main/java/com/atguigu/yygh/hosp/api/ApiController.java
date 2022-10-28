package com.atguigu.yygh.hosp.api;

import com.atguigu.yygh.common.R;
import com.atguigu.yygh.common.Result;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.hosp.utils.MD5;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;


    @ApiOperation(value = "上传医院")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {

        //1从request里取出参数，转型
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        //2签名校验
        //数据库中的
        String signKey = hospitalSetService.getSignKey((String) map.get("hoscode"));
        String signKeyMD5 = MD5.encrypt(signKey);
        //请求中的
        String sign = (String) map.get("sign");
        System.out.println("sign = " + sign);

        System.out.println("signKeyMD5 = " + signKeyMD5);
        if (!sign.equals(signKeyMD5)) {
            throw new YyghException(20001, "签名验证不通过");
        }
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoData = (String) map.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        map.put("logoData", logoData);


        //3调用接口保存医院信息
        hospitalService.saveHospital(map);

        return Result.ok();
    }

    @ApiOperation(value = "获取医院信息")
    @PostMapping("hospital/show")
    public Result hospital(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(parameterMap);

        String hoscode = (String) paramMap.get("hoscode");
        String sign = (String) paramMap.get("sign");


        Hospital hospital = hospitalService.getHospital(hoscode);
        return Result.ok(hospital);

    }

    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        // 签名验证(略)
        String hoscode = (String) map.get("hoscode");
        String sign = (String) map.get("sign");


        departmentService.saveDepartment(map);
        return Result.ok();


    }

    @ApiOperation(value = "获取分页列表")
    @PostMapping("department/list")
    public Result department(HttpServletRequest request) {
        // 1 获取参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        // 2 签名校验
        String sign = (String) map.get("sign");

        // 3 取出参数、校验封装
        String hoscode = (String) map.get("hoscode");
        DepartmentQueryVo queryVo = new DepartmentQueryVo();
        queryVo.setHoscode(hoscode);

        int page = StringUtils.isEmpty((String) map.get("page")) ? 1 :
                Integer.parseInt((String) map.get("page"));
        int limit = StringUtils.isEmpty((String) map.get("limit")) ? 10 :
                Integer.parseInt((String) map.get("limit"));

        Page<Department> pageModel = departmentService.selectPage(page, limit, queryVo);

        return Result.ok(pageModel);
    }

    @ApiOperation(value = "删除科室")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        //2 签名校验（省略）
        //3取出参数
        String hoscode = (String) map.get("hoscode");
        String depcode = (String) map.get("depcode");
        //4调用接口删除科室
        departmentService.removeDepartment(hoscode, depcode);

        return Result.ok();
    }

    @ApiOperation(value = "上传排班")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        // 签名校验
        //
        scheduleService.saveSchedule(map);
        return Result.ok();
    }

    @ApiOperation(value = "查询排班获取分页列表")
    @PostMapping("schedule/list")
    public Result schedule(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());

        //检验签名
//        String sign = (String) map.get("sign");

        String hoscode = (String) map.get("hoscode");
        int page = StringUtils.isEmpty((String)map.get("page"))?
                1:Integer.parseInt((String)map.get("page"));
        int limit = StringUtils.isEmpty((String)map.get("limit"))?
                10:Integer.parseInt((String)map.get("limit"));
        Page<Schedule> pageModel = scheduleService.selectPage(hoscode,page,limit);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "删除排班")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());

//        String sign = (String) map.get("sign");

        String hoscode = (String) map.get("hoscode");
        String hosScheduleId = (String) map.get("hosScheduleId");
        scheduleService.removeSchedule(hoscode,hosScheduleId);
        return Result.ok();
    }

}
