package com.hmdp.interceptor;

import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.utils.UserHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CXWWH
 * @create 2022-10-18-17:36
 * @
 **/
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 获取 session 中的用户信息
        Object cacheUser = request.getSession().getAttribute("user");

        // 判断用户是否为 null
        if( cacheUser == null){
            // 401 认证失败
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        // 将用户信息保存到 ThreadLocal
        UserHolder.saveUser((UserDTO) cacheUser);

        // 放行到 controller 中的方法
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {

        // 销毁 ThreadLocal 中的用户信息，防止内存泄露
        UserHolder.removeUser();
    }
}
