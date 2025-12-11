package com.example.tomatomall.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("PENDING", "待支付"),
    PAID("PAID", "已支付"),
    SUCCESS("SUCCESS", "支付成功"),
    DELIVERED("DELIVERED", "已发货"),
    COMPLETED("COMPLETED", "已完成"),
    FAILED("FAILED", "支付失败"),
    CANCELLED("CANCELLED", "已取消"),
    TIMEOUT("TIMEOUT", "支付超时");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public static OrderStatus fromCode(String code) {
        if (code == null) return null;
        for (OrderStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return null;
    }
}
