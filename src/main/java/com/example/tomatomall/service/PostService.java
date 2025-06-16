package com.example.tomatomall.service;

import com.example.tomatomall.vo.post.PostCreateVO;
import com.example.tomatomall.vo.post.PostVO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    // 发布帖子
    PostVO createPost(PostCreateVO postVO, Integer userId);

    // 获取帖子详情
    PostVO getPostById(Integer postId, Integer userId);

    // 删除帖子
    void deletePost(Integer postId, Integer userId);

    // 获取论坛中的帖子列表（从getPostsBySection改为getPostsByForum）
    Page<PostVO> getPostsByForum(Integer forumId, int page, int size);

    // 点赞帖子
    PostVO likePost(Integer postId, Integer userId);
}
