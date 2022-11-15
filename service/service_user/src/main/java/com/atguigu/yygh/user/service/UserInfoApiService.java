package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserInfoApiService extends IService<UserInfo> {
    // 会员登录
    Map<String, Object> login(LoginVo loginVo);

    // 根据用户id获取用户信息
    UserInfo getUserInfoByUserId(Long userId);

    // 用户认证
    void userAuth(Long userId, UserAuthVo userAuthVo);
}
