package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.R;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
//@CrossOrigin
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation("医院设置列表")
    // 查所有
    @GetMapping("findAll")
    public R findAll(){
        /*try {
            int i = 10/0;
        } catch (Exception e) {
            throw new YyghException(444,"自定义异常");
        }*/
        List<HospitalSet> list = hospitalSetService.list(null);
        return R.ok().data("list",list);
    }

    @ApiOperation("根据id删除医院设置")
    // 根据id 删除
    @DeleteMapping("{id}")
    public R remById(@PathVariable Long id){
        boolean rem = hospitalSetService.removeById(id);
        if(rem) return R.ok();
        return R.error();
    }

    @ApiOperation("带条件带分页查询")
    @PostMapping("{page}/{limit}")
    public R findHos(@PathVariable Long page, @PathVariable Long limit,
                     @RequestBody HospitalSetQueryVo queryVo){
        Page<HospitalSet> pageParam = new Page<>(page,limit);
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();

        String hosname = queryVo.getHosname();
        String hoscode = queryVo.getHoscode();
        if(!StringUtils.isEmpty(hosname)){
            queryWrapper.like("hosname",hosname);
        }
        if(!StringUtils.isEmpty(hoscode)){
            queryWrapper.eq("hoscode",hoscode);
        }
        Page<HospitalSet> pageModel = hospitalSetService.page(pageParam, queryWrapper);
        return R.ok().data("pageModel",pageModel);
    }

    @ApiOperation("分页查询")
    @GetMapping("{page}/{limit}")
    public R findHos(@PathVariable Long page, @PathVariable Long limit){
        Page<HospitalSet> pageParam = new Page<>(page,limit);
        Page<HospitalSet> pageModel = hospitalSetService.page(pageParam);
        return R.ok().data("pageModel",pageModel);
    }

    @ApiOperation("新增医院设置")
    @PostMapping("save")
    public R save(@RequestBody HospitalSet hospitalSet){
        boolean save = hospitalSetService.save(hospitalSet);
        if(save) return R.ok();
        return R.error();
    }

    @ApiOperation("根据id获取医院设置对象")
    @GetMapping("getById/{id}")
    public R getById(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return R.ok().data("hospitalSet",hospitalSet);
    }

    @ApiOperation("修改医院设置")
    @PutMapping("update")
    public R update(@RequestBody HospitalSet hospitalSet){
        boolean up = hospitalSetService.updateById(hospitalSet);
        if(up) return R.ok();
        return R.error();
    }

    @ApiOperation("根据id 批量删除")
    @DeleteMapping("batchRemove")
    public R batchRemove(@RequestBody List<Long> ids){
        boolean rem = hospitalSetService.removeByIds(ids);
        if(rem) return R.ok();
        return R.error();
    }

    @ApiOperation("设置医院设置状态:锁定/解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public R lockAndUnlock(@PathVariable Long id,@PathVariable Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean up = hospitalSetService.updateById(hospitalSet);
        if(up) return R.ok();
        return R.error();
    }

    @ApiOperation("模拟登录")
    @PostMapping("login")
//    {"code":20000,"data":{"token":"admin-token"}}  - post
    public R login(){
        return R.ok().data("token","admin-token");
    }


//    {"code":20000,"data":{"roles":["admin"],"introduction":"I am a super administrator",
//    "avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif",
//    "name":"Super Admin"}}  - get
    @ApiOperation("模拟获取用户信息")
    @GetMapping("info")
    public R getInfo(){
        Map<String,Object> map = new HashMap<>();
        map.put("roles","admin");
        map.put("introduction","I am a super administrator");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name","Super Admin");
        return R.ok().data(map);
    }
}
