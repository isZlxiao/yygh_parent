package com.atguigu.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.R;
import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoApiService;
import com.atguigu.yygh.user.utils.ConstantPropertiesUtil;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "微信扫码登录")
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoApiService userInfoApiService;


    /**
     * 获取微信登录参数
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public R genQrConnect(HttpSession session) throws UnsupportedEncodingException {
        String redirectUrl= URLEncoder.encode(
                ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL,"UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("redirectUri", redirectUrl);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""
        return R.ok().data(map);
    }

    @ApiOperation(value = "微信扫码登录回调")
    @GetMapping("callback")
    public String callback(String code, String state, HttpSession session) {
        //1 获取code 、 state
        System.out.println("code = " + code);
        System.out.println("state = " + state);
        //2 根据code state访问微信获取 access_token 、open_id
            //2.1拼写url
            //方式一
    //        String url ="https://api.weixin.qq.com/sns/oauth2/access_token" +
    //                "?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
            //方式二
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(
                baseAccessTokenUrl.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);

        try {
            //2.2使用httpclient工具发送请求，获取响应
            String accessTokenStr = HttpClientUtils.get(accessTokenUrl);
            //2.3使用工具转换相应类型，取出参数
            JSONObject accessTokenJson = JSONObject.parseObject(accessTokenStr);
            String accessToken = accessTokenJson.getString("access_token");
            String openid = accessTokenJson.getString("openid");

            //3 根据openid查询数据库，获取用户信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid",openid);
            UserInfo userInfo = userInfoApiService.getOne(wrapper);


            //4 用户信息为空走注册步骤，访问微信获取用户信息
            if(StringUtils.isEmpty(userInfo)){
                // 4.1获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);

                String userInfoStr = HttpClientUtils.get(userInfoUrl);
                // 4.2转换为相应类型，提取参数
                JSONObject userInfoJson = JSONObject.parseObject(userInfoStr);
                String nickname = userInfoJson.getString("nickname");
                String headimgurl = userInfoJson.getString("headimgurl"); // 头像
                // 4.3数据存入userinfo 存入数据库
                userInfo = new UserInfo();
                userInfo.setStatus(1); // 0锁定
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setIsDeleted(0);
                userInfoApiService.save(userInfo);
            }
            //5 判断用户是否被锁定
            if(userInfo.getStatus() == 0){
                throw new YyghException(20001,"用户已锁定");
            }
            //6 判断用户是否绑定手机号
            Map<String,String> map = new HashMap<>();
            if(StringUtils.isEmpty(userInfo.getPhone())){
                // 未绑定 map里存openid
                map.put("openid",openid);
            }else{
                // 已绑定 map里存openid=""
                map.put("openid","");
            }
            //7 补全用户信息
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)){
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)){
                name = userInfo.getPhone();
            }

            //8 走登录步骤
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("name",name);
            map.put("token",token);

            //9 重定向返回
            return "redirect:http://localhost:3000/weixin/callback?token="
                    +map.get("token")+ "&openid="+map.get("openid")
                    +"&name="+URLEncoder.encode(map.get("name"),"utf-8");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}