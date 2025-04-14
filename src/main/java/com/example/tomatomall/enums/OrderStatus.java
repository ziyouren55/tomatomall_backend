package com.example.tomatomall.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("待支付"),
    SUCCESS("支付成功"),
    FAILED("支付失败"),
    TIMEOUT("支付超时");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

}
