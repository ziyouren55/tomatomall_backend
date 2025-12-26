package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_coupons")
@Getter
@Setter
@NoArgsConstructor
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "coupon_id", nullable = false)
    private Integer couponId;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "used_time")
    private Date usedTime;

    @Column(name = "order_id")
    private Integer orderId; // 使用该优惠券的订单ID

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "product_id")
    private Integer productId; // 限制使用的商品ID，为null表示无限制

    @Column(name = "merchant_id")
    private Integer merchantId; // 发放优惠券的商家ID

    @Column(name = "issued_remark")
    private String issuedRemark; // 发放时的备注信息

    // 便捷方法：检查优惠券是否有商品限制
    public boolean hasProductRestriction() {
        return productId != null;
    }

    // 便捷方法：检查优惠券是否可用于指定商品
    public boolean canUseForProduct(Integer productId) {
        return this.productId == null || this.productId.equals(productId);
    }
}
