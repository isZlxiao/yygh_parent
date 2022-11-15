package com.atguigu.yygh.user.controller;

import com.atguigu.yygh.common.R;
import com.atguigu.yygh.common.utils.AuthContextHolder;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoApiService;
import com.atguigu.yygh.user.utils.IpUtils;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "用户登录接口")
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoApiService userInfoApiService;

    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        String ipAddr = IpUtils.getIpAddr(request);
        loginVo.setIp(ipAddr);
        Map<String, Object> map = userInfoApiService.login(loginVo);
        return R.ok().data(map);
    }

    //获取用户id信息接口
    @ApiOperation(value = "根据用户id获取用户信息")
    @GetMapping("auth/getUserInfo")
    public R getUserInfo(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoApiService.getUserInfoByUserId(userId);
        return R.ok().data("userInfo", userInfo);
    }

    // 用户认证
    @ApiOperation(value = "用户认证")
    @PostMapping("auth/userAuth")
    public R userAuth(@RequestBody UserAuthVo userAuthVo,
                      HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        userInfoApiService.userAuth(userId,userAuthVo);
        return R.ok();
    }
}