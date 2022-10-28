package com.atguigu.yygh.cmn.client;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Repository
@FeignClient("service-cmn")
public interface DictFeignClient {

    // 获取数据字典名称(国标)
    @GetMapping(value = "/admin/cmn/dict/getName/{value}")
    public String getName(@PathVariable("value") String value);

    // 获取数据字典名称(自定义)
    @GetMapping(value = "/admin/cmn/dict/getName/{parentDictCode}/{value}")
    public String getName(@PathVariable("parentDictCode") String parentDictCode,
                          @PathVariable("value") String value);
}
