package com.example.tomatomall.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: DingXiaoyu
 * @Date: 0:17 2023/11/26
 *
 * 这个类实现了WebMvcConfigurer接口，
 * 表示会被SpringBoot接受，
 * 这个类的作用是配置拦截器。
 * addInterceptors方法配置了拦截器，
 * 添加了loginInterceptor作为拦截器，
 * 并且设置除了register和login的所有接口都需要通过该拦截器。
*/
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                // 排除不需要登录的公开接口
                .excludePathPatterns(
                        "/api/accounts/login",           // 登录接口
                        "/api/accounts",                  // 注册接口（POST）
                        "/api/products",                  // 商品列表接口（GET）- 允许未登录用户浏览
                        "/api/products/search",          // 商品搜索接口（GET）- 允许未登录用户搜索
                        "/api/forums",                    // 论坛列表接口（GET）
                        "/api/forums/active",             // 活跃论坛接口（GET）
                        "/api/advertisements",            // 广告列表接口（GET）
                        "/api/images"                     // 图片上传接口（POST）- 允许未登录用户上传，用于注册时上传头像等场景
                )
                .order(1);
    }

}
