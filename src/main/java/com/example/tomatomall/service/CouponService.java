package com.example.tomatomall.service;

import com.example.tomatomall.vo.coupon.CouponVO;
import com.example.tomatomall.vo.coupon.UserCouponVO;

import java.util.List;

public interface CouponService {
    // 优惠券管理
    List<CouponVO> getAllCoupons();
    CouponVO getCouponById(Integer couponId);
    List<CouponVO> getAvailableCoupons();
    CouponVO createCoupon(CouponVO couponVO);
    CouponVO updateCoupon(CouponVO couponVO);

    // 用户优惠券相关
    List<UserCouponVO> getUserCoupons(Integer userId);
    UserCouponVO exchangeCoupon(Integer userId, Integer couponId);
    boolean applyCouponToOrder(Integer userId, Integer couponId, Integer orderId);
    UserCouponVO getUserCouponById(Integer userCouponId);

    // 新增管理员方法
    UserCouponVO issueCouponToUser(Integer couponId, Integer userId, String remark);
}
