package com.example.tomatomall.enums;

/**
 * 优惠券类型枚举
 * 支持大小写兼容的字符串转换，提高系统包容性
 */
public enum CouponType {
    /**
     * 可重复兑换的优惠券（出现在领券中心）
     */
    REPEAT("REPEAT"),

    /**
     * 私人优惠券（商家发放，不出现在领券中心）
     */
    PRIVATE("PRIVATE");

    private final String value;

    CouponType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取枚举（大小写兼容）
     */
    public static CouponType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return REPEAT; // 默认值
        }

        // 统一转换为大写进行比较，提高兼容性
        String upperValue = value.trim().toUpperCase();

        for (CouponType type : CouponType.values()) {
            if (type.value.equals(upperValue)) {
                return type;
            }
        }

        // 如果找不到匹配的类型，返回默认值而不是抛出异常
        return REPEAT;
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        switch (this) {
            case REPEAT:
                return "可重复兑换";
            case PRIVATE:
                return "私人优惠券";
            default:
                return this.value;
        }
    }

    /**
     * 验证优惠券类型是否有效
     */
    public boolean isValid() {
        return this == REPEAT || this == PRIVATE;
    }
}
