package com.example.tomatomall.vo.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class IssueChatCouponVO {
    private Integer sessionId;        // 聊天会话ID
    private Integer productId;        // 指定的商品ID
    private BigDecimal discountAmount; // 折扣金额
    private BigDecimal discountPercentage; // 折扣百分比
    private BigDecimal minimumPurchase; // 最低消费金额
    private Integer validDays;        // 有效天数
    private String remark;           // 发放备注
}
