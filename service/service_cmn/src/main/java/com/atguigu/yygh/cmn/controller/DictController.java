package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.R;
import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(description = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin  // 跨域问题
public class DictController {

    @Autowired
    private DictService dictService;


    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public R findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChildData(id);
        return R.ok().data("list", list);
    }

    @ApiOperation(value = "导出数据")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response){
        dictService.exportData(response);
    }

    @ApiOperation(value = "导入数据")
    @PostMapping("importData")
    public R importData(MultipartFile file){
        dictService.importData(file);
        return R.ok();
    }
}
