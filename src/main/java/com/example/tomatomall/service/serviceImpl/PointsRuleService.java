package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PointsRuleService {
    @Autowired
    private MemberService memberService;

    // 订单完成后增加积分
    public void addPointsForOrder(Integer userId, Integer orderId, BigDecimal orderAmount) {
        // 基础规则：每消费1元获得10积分
        Integer pointsToAdd = orderAmount.intValue() * 10;
        memberService.addUserPoints(userId, pointsToAdd, "PURCHASE", orderId, "购买商品获得积分");
    }

    // 论坛发帖获得积分
    public void addPointsForForumPost(Integer userId, Integer postId) {
        memberService.addUserPoints(userId, 5, "FORUM_POST", postId, "论坛发帖获得积分");
    }

    // 书评评论获得积分
    public void addPointsForBookReview(Integer userId, Integer reviewId) {
        memberService.addUserPoints(userId, 3, "REVIEW", reviewId, "发表书评获得积分");
    }

    // 特殊活动积分奖励
    public void addPointsForSpecialActivity(Integer userId, Integer activityId, Integer points, String description) {
        memberService.addUserPoints(userId, points, "ACTIVITY", activityId, description);
    }
}
