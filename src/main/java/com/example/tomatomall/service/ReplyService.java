package com.example.tomatomall.service;

import com.example.tomatomall.vo.post.ReplyCreateVO;
import com.example.tomatomall.vo.post.ReplyVO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReplyService {
    // 发表回复
    ReplyVO createReply(ReplyCreateVO replyVO, Integer userId);

    // 删除回复
    void deleteReply(Integer replyId, Integer userId);

    // 获取帖子的回复列表
    Page<ReplyVO> getRepliesByPost(Integer postId, int page, int size);

    // 点赞回复
    ReplyVO likeReply(Integer replyId, Integer userId);
}
