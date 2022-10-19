package com.hmdp.config;

import com.hmdp.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author CXWWH
 * @create 2022-10-18-17:47
 * @
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()) // 添加登陆拦截器
                // 根据实际需求放行用户无需登录就可访问的请求路径
                .excludePathPatterns(
                        "/shop/**",
                        "/shop-type/**",
                        "/voucher/**", // 优惠券查询
                        "/upload/**", // 上传，为了测试方便，这里也放行
                        "/blog/hot",
                        "/user/code",
                        "/user/login"
                );
    }
}
