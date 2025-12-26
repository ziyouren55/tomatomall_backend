package com.example.tomatomall.controller;

import com.example.tomatomall.service.CouponService;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.coupon.IssueChatCouponVO;
import com.example.tomatomall.vo.coupon.UserCouponVO;
import com.example.tomatomall.vo.products.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/coupons")
public class MerchantCouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private ProductService productService;

    /**
     * 获取商家管理的商品列表（用于优惠券发放）
     */
    @GetMapping("/products")
    public Response<?> getMerchantProducts(@RequestAttribute("userId") Integer merchantId) {
        List<ProductVO> products = productService.getProductsByMerchantId(merchantId);
        return Response.buildSuccess(products);
    }

    /**
     * 为聊天用户发放指定商品的优惠券
     */
    @PostMapping("/issue-to-chat")
    public Response<?> issueCouponToChatUser(@RequestAttribute("userId") Integer merchantId,
                                           @RequestBody IssueChatCouponVO request) {
        try {
            UserCouponVO result = couponService.createAndIssueChatCoupon(merchantId, request);
            return Response.buildSuccess(result);
        } catch (Exception e) {
            return Response.buildFailure(e.getMessage(), "500");
        }
    }

    /**
     * 获取商家发放的优惠券历史
     */
    @GetMapping("/issued-history")
    public Response<?> getIssuedCoupons(@RequestAttribute("userId") Integer merchantId) {
        List<UserCouponVO> issuedCoupons = couponService.getCouponsIssuedByMerchant(merchantId);
        return Response.buildSuccess(issuedCoupons);
    }
}
