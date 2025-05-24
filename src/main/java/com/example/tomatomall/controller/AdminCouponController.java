package com.example.tomatomall.controller;

import com.example.tomatomall.service.CouponService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.coupon.CouponIssueVO;
import com.example.tomatomall.vo.coupon.CouponVO;
import com.example.tomatomall.vo.coupon.UserCouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {
    @Autowired
    private CouponService couponService;

    /**
     * 获取所有优惠券
     */
    @GetMapping()
    public Response getAllCoupons() {
        return Response.buildSuccess(couponService.getAllCoupons());
    }

    /**
     * 根据ID获取优惠券
     */
    @GetMapping("/{couponId}")
    public Response getCouponById(@PathVariable Integer couponId) {
        return Response.buildSuccess(couponService.getCouponById(couponId));
    }

    /**
     * 创建优惠券
     */
    @PostMapping()
    public Response createCoupon(@RequestBody CouponVO couponVO) {
        return Response.buildSuccess(couponService.createCoupon(couponVO));
    }

    /**
     * 更新优惠券
     */
    @PutMapping("/{couponId}")
    public Response updateCoupon(@PathVariable Integer couponId, @RequestBody CouponVO couponVO) {
        couponVO.setId(couponId);
        return Response.buildSuccess(couponService.updateCoupon(couponVO));
    }

    /**
     * 查看用户优惠券
     */
    @GetMapping("/user/{userId}")
    public Response getUserCoupons(@PathVariable Integer userId) {
        return Response.buildSuccess(couponService.getUserCoupons(userId));
    }

    /**
     * 为用户发放优惠券（管理员操作，无需扣减积分）
     */
    @PostMapping("/issue")
    public Response issueCouponToUser(@RequestBody CouponIssueVO issueVO) {
        UserCouponVO result = couponService.issueCouponToUser(
            issueVO.getCouponId(),
            issueVO.getUserId(),
            issueVO.getRemark()
        );
        return Response.buildSuccess(result);
    }
}
