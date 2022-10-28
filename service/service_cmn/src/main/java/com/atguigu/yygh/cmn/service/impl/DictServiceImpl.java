package com.atguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {


    @Autowired
    private DictListener dictListener;


    // 根据数据id查询子数据列表
    @Override
    @Cacheable(value = "dict", key = "'selectIndexList'+#id")
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> dicts = baseMapper.selectList(wrapper);

        for (Dict dict : dicts) {
            boolean hasChild = this.isChildren(dict.getId());
            dict.setHasChildren(hasChild);
        }
        return dicts;
    }

    // 导出数据
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            //1设置response基本参数
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            //2
            List<Dict> dictList = baseMapper.selectList(null);
            List<DictEeVo> list = new ArrayList<>();
            for (Dict dict : dictList) {
                DictEeVo vo = new DictEeVo();
                BeanUtils.copyProperties(dict, vo);
                list.add(vo);
            }
            //3
            EasyExcel.write(response.getOutputStream(), DictEeVo.class)
                    .sheet("数据字典")
                    .doWrite(list);

        } catch (IOException e) {
            e.printStackTrace();
            throw new YyghException(20001, "导出数据出错");
        }
    }

    // 导入数据
    @Override
    public void importData(MultipartFile file) {
        try {
            //1 获取导入参数
            InputStream inputStream = file.getInputStream();
            // 2导入数据
            EasyExcel.read(inputStream,DictEeVo.class,dictListener)
                        .sheet().doRead();


        } catch (IOException e) {
            e.printStackTrace();
            throw new YyghException(20001,"导入数据出错");
        }

    }

    // 获取数据字典名称
    @Override
    public String getNameByParentDictCodeAndValue(String parentDictCode, String value) {
        if(StringUtils.isEmpty(parentDictCode)){
            // 国标数据查询
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            if(dict!=null) return dict.getName();
        }else{
            // 自定义数据查询
            //2.1根据字典编码查询父级别数据
            Dict parentDict = this.getParentDict(parentDictCode);
            //2.2根据父级别id+value查询数据
            QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_id",parentDict.getId());
            queryWrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(queryWrapper);
            return dict.getName();
        }

        return null;
    }

    // 根据父节点 dictCode 查询父节点
    private Dict getParentDict(String parentDictCode) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code",parentDictCode);
        return baseMapper.selectOne(wrapper);
    }


    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
