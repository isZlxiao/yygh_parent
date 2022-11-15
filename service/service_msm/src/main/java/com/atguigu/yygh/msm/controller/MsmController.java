package com.atguigu.yygh.msm.controller;


import com.atguigu.yygh.common.R;
import com.atguigu.yygh.msm.utils.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.atguigu.yygh.msm.service.MsmService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "短信发送")
@RestController
@RequestMapping("/api/msm")
public class MsmController {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private MsmService msmService;


    @ApiOperation(value = "发送验证码")
    @GetMapping(value = "/send/{phone}")
    public R code(@PathVariable String phone) {
        //1 根据手机号查询redis,看是否过期
        String redisCode = redisTemplate.opsForValue().get(phone);
        //2验证码存在返回成功，存在说明还未过期，不发送新验证码
        if(!StringUtils.isEmpty(redisCode)){
            return R.ok();
        }
        //3验证码不存在，生成验证码封装到map中
        String code = RandomUtil.getFourBitRandom();
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
        //4调用方法发送验证码
        boolean isSend = msmService.sendCode(phone,map);
        //5将验证码存入redis并设置5分钟有效时间
        if(isSend){
            //发送成功
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.ok();
        }
        // 发送失败
        return R.error();
    }

}
