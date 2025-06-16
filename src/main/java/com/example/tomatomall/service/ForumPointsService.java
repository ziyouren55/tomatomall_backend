package com.example.tomatomall.service;

public interface ForumPointsService {
    // 发帖奖励积分
    void rewardPointsForPost(Integer userId, Integer postId);

    // 回复奖励积分
    void rewardPointsForReply(Integer userId, Integer replyId);
}
