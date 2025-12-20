package com.example.tomatomall.util;

import com.example.tomatomall.enums.UserRole;
import com.example.tomatomall.po.Account;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 简单的用户上下文工具，从当前请求的 RequestAttributes 中读取 LoginInterceptor 放入的属性
 * 注意：仅适用于同步 HTTP 请求上下文；异步线程需显式传播上下文或使用 TaskDecorator
 */
public class UserContext {

    public static Integer getCurrentUserId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        Object id = attrs.getAttribute("userId", RequestAttributes.SCOPE_REQUEST);
        if (id instanceof Integer) return (Integer) id;
        return null;
    }

    public static UserRole getCurrentUserRole() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        Object role = attrs.getAttribute("userRole", RequestAttributes.SCOPE_REQUEST);
        if (role instanceof UserRole) return (UserRole) role;
        if (role instanceof String) {
            try {
                return UserRole.valueOf(((String) role).toUpperCase());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static Account getCurrentUser() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        Object acc = attrs.getAttribute("currentUser", RequestAttributes.SCOPE_REQUEST);
        if (acc instanceof Account) return (Account) acc;
        return null;
    }
}


