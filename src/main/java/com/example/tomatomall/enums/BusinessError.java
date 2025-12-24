package com.example.tomatomall.enums;

import lombok.Getter;

@Getter
public enum BusinessError {
    // 认证相关错误
    SCHOOL_NOT_VERIFIED("SCHOOL_NOT_VERIFIED", "用户未通过学校认证"),
    USER_NOT_LOGIN("USER_NOT_LOGIN", "用户未登录"),

    // 资源不存在错误
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "商品不存在"),
    STORE_NOT_FOUND("STORE_NOT_FOUND", "店铺不存在"),

    // 权限相关错误
    PERMISSION_DENIED("PERMISSION_DENIED", "权限不足"),
    STORE_PERMISSION_DENIED("STORE_PERMISSION_DENIED", "店铺权限不足"),

    // 业务逻辑错误
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "库存不足"),
    PRODUCT_ALREADY_EXISTS("PRODUCT_ALREADY_EXISTS", "商品已存在"),

    // 系统错误
    SYSTEM_ERROR("SYSTEM_ERROR", "系统错误"),
    NETWORK_ERROR("NETWORK_ERROR", "网络错误"),
    IMPORT_FAILED("IMPORT_FAILED", "导入失败");

    private final String code;
    private final String message;

    BusinessError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码查找枚举
     */
    public static BusinessError fromCode(String code) {
        if (code == null) {
            return SYSTEM_ERROR;
        }
        for (BusinessError error : values()) {
            if (error.code.equals(code)) {
                return error;
            }
        }
        return SYSTEM_ERROR; // 默认返回系统错误
    }
}
