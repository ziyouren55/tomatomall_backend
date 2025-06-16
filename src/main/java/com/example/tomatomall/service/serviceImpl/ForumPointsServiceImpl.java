package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.ForumPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForumPointsServiceImpl implements ForumPointsService {

    @Autowired
    private PointsRuleService pointsRuleService;

    @Override
    public void rewardPointsForPost(Integer userId, Integer postId) {
        // 发帖奖励5积分
        pointsRuleService.addPointsForForumPost(userId, postId);
    }

    @Override
    public void rewardPointsForReply(Integer userId, Integer replyId) {
        // 回复奖励3积分
        pointsRuleService.addPointsForBookReview(userId, replyId);
    }
}
