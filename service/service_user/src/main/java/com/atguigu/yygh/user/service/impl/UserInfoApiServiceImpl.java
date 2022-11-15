package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.common.handler.YyghException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoApiMapper;
import com.atguigu.yygh.user.service.UserInfoApiService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoApiServiceImpl
        extends ServiceImpl<UserInfoApiMapper, UserInfo>
        implements UserInfoApiService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 会员登录
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //1 取参，验空
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(20001, "登录参数有误");
        }
        //2 校验验证码
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (!code.equals(redisCode)) {
            throw new YyghException(20001, "验证码有误");
        }
        Map<String, Object> map = new HashMap<>();
        // 判断openid是否为空，为空走原流程、不为空绑定手机号
        String openid = loginVo.getOpenid();
        if (StringUtils.isEmpty(openid)) {
            //3 使用手机号查询用户
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            UserInfo user = baseMapper.selectOne(wrapper);
            //4 用户信息不存在 --> 注册
            if (user == null) {
                user = new UserInfo();
                user.setPhone(phone);
                user.setStatus(1);
                baseMapper.insert(user);
            }
            //5 判断用户是否被锁定
            if (user.getStatus() == 0) {
                throw new YyghException(20001, "账户被锁定");
            }
            //6 补全用户名
            String name = user.getName();
            if (StringUtils.isEmpty(name)) name = user.getNickName();
            if (StringUtils.isEmpty(name)) name = user.getPhone();

            //7 走登录步骤
            String token = JwtHelper.createToken(user.getId(), name);
            map.put("name", name);
            map.put("token", token);
        }else{
            // openid不为空
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid", openid);
            UserInfo user = baseMapper.selectOne(wrapper);
            user.setPhone(phone);
            baseMapper.updateById(user);

            //5 判断用户是否被锁定
            if (user.getStatus() == 0) {
                throw new YyghException(20001, "账户被锁定");
            }
            //6 补全用户名
            String name = user.getName();
            if (StringUtils.isEmpty(name)) name = user.getNickName();
            if (StringUtils.isEmpty(name)) name = user.getPhone();

            //7 走登录步骤
            String token = JwtHelper.createToken(user.getId(), name);
            map.put("name", name);
            map.put("token", token);
        }


        //(自加) 登陆成功后将redis中验证码删除
//        redisTemplate.delete(phone);
        return map;
    }

    // 根据用户id获取用户信息
    @Override
    public UserInfo getUserInfoByUserId(Long userId) {
        UserInfo userInfo = this.packUserInfo(baseMapper.selectById(userId));
        return userInfo;
    }

    // 用户认证
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);
        BeanUtils.copyProperties(userAuthVo,userInfo);
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        baseMapper.updateById(userInfo);
    }

    // 翻译字段
    private UserInfo packUserInfo(UserInfo userInfo) {
        String statusNameByStatus = AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus());
        userInfo.getParam().put("authStatusString",statusNameByStatus);
        return userInfo;
    }


}
