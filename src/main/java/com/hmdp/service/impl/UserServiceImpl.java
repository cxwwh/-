package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import com.hmdp.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.SessionCookieConfig;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Override
    public Result sendCode(String phone, HttpSession session) {

        // 校验手机号
        if(RegexUtils.isPhoneInvalid(phone)){

            return Result.fail("手机号不合法，请输入正确的手机号。");
        }

        // 手机号校验通过，使用 糊涂工具包 生成 6 位验证码
        // cn.hutool.core.util.RandomUtil;
        String code = RandomUtil.randomNumbers(6);

        // 将验证码保存到 session
        session.setAttribute(phone, code);

        // 使用日志打印代替发送短信验证码
        log.info("成功发送验证码:{}", code);

        // 返回 ok
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {

        // 获取登陆信息中的手机号和验证码
        String phone = loginForm.getPhone();
        String code = loginForm.getCode();

        // 校验手机号
        if(RegexUtils.isPhoneInvalid(phone)){

            return Result.fail("登陆失败，手机号不合法。");
        }

        // 校验验证码
        Object cacheCode = session.getAttribute(phone);
        if( cacheCode == null || !code.equals(cacheCode.toString())){

            return Result.fail("登陆失败，验证码错误。");
        }

        // 根据手机号到数据库查询用户信息
        User user = query().eq("phone", phone).one();

        log.info("user:{}", user);

        // 若用户不存在，则进行注册
        if( user == null){

            // 根据手机号生成用户信息
            user = createUserWithPhone(phone);
            // 将用户信息保存到数据库
            save(user);
        }
        log.info("user:{}", user);

        // 将用户信息保存到 session
        // 这里是将用户的敏感信息去掉，将一些必要信息存入 UserDTO 对象中
        // cn.hutool.core.bean.BeanUtil;
        UserDTO userDTO = BeanUtil.copyProperties(user,UserDTO.class);
        session.setAttribute("user", userDTO);

        return Result.ok();
    }

    private User createUserWithPhone(String phone) {

        // 使用空参构造器创建一个用户
        User user = new User();

        // 设置 phone
        user.setPhone(phone);

        // 生成一个随机的用户名，加上前缀 user_
        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX +
                RandomUtil.randomString(10));

        return user;
    }
}
