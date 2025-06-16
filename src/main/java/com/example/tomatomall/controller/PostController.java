package com.example.tomatomall.controller;

import com.example.tomatomall.service.PostService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.post.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 发布帖子
     */
    @PostMapping
    public Response createPost(@RequestBody @Valid PostCreateVO postVO,
                               @RequestAttribute("userId") Integer userId) {
        PostVO post = postService.createPost(postVO, userId);
        return Response.buildSuccess(post);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/{postId}")
    public Response getPostDetail(@PathVariable Integer postId,
                                  @RequestAttribute("userId") Integer userId) {
        PostVO post = postService.getPostById(postId, userId);
        return Response.buildSuccess(post);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{postId}")
    public Response deletePost(@PathVariable Integer postId,
                               @RequestAttribute("userId") Integer userId) {
        postService.deletePost(postId, userId);
        return Response.buildSuccess("帖子删除成功");
    }

    /**
     * 获取论坛中的帖子列表（从section改为forum）
     */
    @GetMapping("/forum/{forumId}")
    public Response getPostsByForum(@PathVariable Integer forumId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Page<PostVO> posts = postService.getPostsByForum(forumId, page, size);
        return Response.buildSuccess(posts);
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/{postId}/like")
    public Response likePost(@PathVariable Integer postId,
                             @RequestAttribute("userId") Integer userId) {
        PostVO post = postService.likePost(postId, userId);
        return Response.buildSuccess(post);
    }
}
