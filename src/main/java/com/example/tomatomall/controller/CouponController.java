package com.example.tomatomall.controller;

import com.example.tomatomall.service.CouponService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.coupon.ApplyCouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;

    /**
     * 获取所有可兑换的优惠券
     */
    @GetMapping("/available")
    public Response getAvailableCoupons() {
        return Response.buildSuccess(couponService.getAvailableCoupons());
    }

    /**
     * 获取优惠券详情
     */
    @GetMapping("/{couponId}")
    public Response getCouponDetail(@PathVariable Integer couponId) {
        return Response.buildSuccess(couponService.getCouponById(couponId));
    }

    /**
     * 获取用户拥有的优惠券
     */
    @GetMapping("/my")
    public Response getUserCoupons(@RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(couponService.getUserCoupons(userId));
    }

    /**
     * 兑换优惠券
     */
    @PostMapping("/exchange/{couponId}")
    public Response exchangeCoupon(@RequestAttribute("userId") Integer userId, @PathVariable Integer couponId) {
        return Response.buildSuccess(couponService.exchangeCoupon(userId, couponId));
    }

    /**
     * 使用优惠券
     */
    @PostMapping("/apply")
    public Response applyCoupon(@RequestAttribute("userId") Integer userId, @RequestBody ApplyCouponVO applyCouponVO) {
        boolean success = couponService.applyCouponToOrder(
            userId,
            applyCouponVO.getCouponId(),
            applyCouponVO.getOrderId()
        );
        return Response.buildSuccess(success ? "优惠券应用成功" : "优惠券应用失败");
    }
}
