package com.atguigu.yygh.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.yygh.common.R;
import org.springframework.stereotype.Service;
import com.atguigu.yygh.msm.service.MsmService;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {

    // 发送验证码
    @Override
    public boolean sendCode(String phone,  Map<String, String> paramsMap) {
        // 1 验空
        if (StringUtils.isEmpty(phone)) return false;
        /*  真实环境代码
        //2创建客户端
        DefaultProfile profile =
                DefaultProfile.getProfile("default",
                "LTAI5tJi6f9Do4zUzmWAeaoo", "VZ3ttdQZj7TaeDO4ZgMmbJeAhDeBO9");
        IAcsClient client = new DefaultAcsClient(profile);
        //3创建请求对象，设置参数
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "我的谷粒在线教育网站");
        request.putQueryParameter("TemplateCode", "SMS_183195440");
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(paramsMap));
        try {
            //4调用客户端方法，发送请求拿响应
            CommonResponse response = client.getCommonResponse(request);
            //5从响应中获取结果
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }
        */
        // 测试环境，不真发送短信
        return true;
    }
}
