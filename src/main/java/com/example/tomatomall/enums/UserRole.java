package com.example.tomatomall.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("顾客"),
    ADMIN("商家");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    /**
     * 从字符串转换为枚举（不区分大小写）
     */
    public static UserRole fromString(String role) {
        if (role == null) {
            return USER; // 默认角色
        }
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 兼容旧数据或错误数据，尝试匹配
            String upperRole = role.toUpperCase();
            if (upperRole.equals("ADMIN") || upperRole.equals("商家")) {
                return ADMIN;
            }
            return USER;
        }
    }

    /**
     * 验证角色字符串是否有效
     */
    public static boolean isValid(String role) {
        if (role == null) {
            return false;
        }
        try {
            UserRole.valueOf(role.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

