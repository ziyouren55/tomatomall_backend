package com.example.tomatomall.vo.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;
import com.example.tomatomall.enums.CouponType;

@Setter
@Getter
@NoArgsConstructor
public class UserCouponVO {
    private Integer id;
    private Integer userId;
    private Integer couponId;
    private String couponName;
    private String couponDescription;
    private BigDecimal discountAmount;
    private BigDecimal discountPercentage;
    private BigDecimal minimumPurchase;
    private Integer pointsRequired;
    private Boolean isUsed;
    private Date usedTime;
    private Integer orderId;
    private Date validFrom;
    private Date validTo;
    private Boolean isActive;
    private Integer productId;
    private Integer merchantId;
    private String issuedRemark;
    private CouponType type;
}
