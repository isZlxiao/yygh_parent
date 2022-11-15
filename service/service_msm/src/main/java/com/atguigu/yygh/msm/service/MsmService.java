package com.atguigu.yygh.msm.service;

import java.util.Map;

public interface MsmService {
    // 发送验证码
    boolean sendCode(String phone,  Map<String, String> paramsMap);
}
