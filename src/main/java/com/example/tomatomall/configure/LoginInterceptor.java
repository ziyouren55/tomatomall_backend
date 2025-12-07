package com.example.tomatomall.configure;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: DingXiaoyu
 * @Date: 0:17 2023/11/26
 * 这个类定制了一个登录的拦截器，
 * SpringBoot的拦截器标准为HandlerInterceptor接口，
 * 这个类实现了这个接口，表示是SpringBoot标准下的，
 * 在preHandle方法中，通过获取请求头Header中的token，
 * 判断了token是否合法，如果不合法则抛异常，
 * 合法则将用户信息存储到request的session中。
 */
@Component
public class LoginInterceptor implements HandlerInterceptor
{

    @Autowired
    TokenUtil tokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        // 白名单路径的二次验证
        if (isWhitelistedRequest(request))
        {
            return true; // 放行白名单请求
        }

        // 其他所有请求强制检查Token
        String token = request.getHeader("token");
        if (token != null && tokenUtil.verifyToken(token))
        {
            Account account = tokenUtil.getAccount(token);
            request.getSession().setAttribute("currentUser", account);
            request.setAttribute("currentUser", account);
            request.setAttribute("userId",account.getId());
            request.setAttribute("username",account.getUsername());
            return true;
        }
        else
        {
            throw TomatoMallException.notLogin();
        }
    }

    // 白名单逻辑集中管理
    private boolean isWhitelistedRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 注册接口：POST /api/accounts
        if ("POST".equalsIgnoreCase(method) && "/api/accounts".equals(path)) {
            return true;
        }

        // 商品列表接口：GET /api/products（不包括商品详情 /api/products/{id}）
        if ("GET".equalsIgnoreCase(method) && "/api/products".equals(path)) {
            return true;
        }

        // 论坛列表接口：GET /api/forums（不包括论坛详情 /api/forums/{id}）
        if ("GET".equalsIgnoreCase(method) && "/api/forums".equals(path)) {
            return true;
        }

        // 活跃论坛接口：GET /api/forums/active
        if ("GET".equalsIgnoreCase(method) && "/api/forums/active".equals(path)) {
            return true;
        }

        // 广告列表接口：GET /api/advertisements
        if ("GET".equalsIgnoreCase(method) && "/api/advertisements".equals(path)) {
            return true;
        }

        // 其他白名单规则可在此扩展
        return false;
    }
}

// 白名单逻辑集中管

