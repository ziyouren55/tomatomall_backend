package com.example.tomatomall.controller;

import com.example.tomatomall.service.ReplyService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.post.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/replies")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    /**
     * 发表回复
     */
    @PostMapping
    public Response<ReplyVO> createReply(@RequestBody @Valid ReplyCreateVO replyVO,
                                @RequestAttribute("userId") Integer userId) {
        ReplyVO reply = replyService.createReply(replyVO, userId);
        return Response.buildSuccess(reply);
    }

    /**
     * 删除回复
     */
    @DeleteMapping("/{replyId}")
    public Response<String> deleteReply(@PathVariable Integer replyId,
                                @RequestAttribute("userId") Integer userId) {
        replyService.deleteReply(replyId, userId);
        return Response.buildSuccess("回复删除成功");
    }

    /**
     * 获取帖子的回复列表
     */
    @GetMapping("/post/{postId}")
    public Response<Page<ReplyVO>> getRepliesByPost(@PathVariable Integer postId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        Page<ReplyVO> replies = replyService.getRepliesByPost(postId, page, size);
        return Response.buildSuccess(replies);
    }

    /**
     * 点赞回复
     */
    @PostMapping("/{replyId}/like")
    public Response<ReplyVO> likeReply(@PathVariable Integer replyId,
                              @RequestAttribute("userId") Integer userId) {
        ReplyVO reply = replyService.likeReply(replyId, userId);
        return Response.buildSuccess(reply);
    }
}
