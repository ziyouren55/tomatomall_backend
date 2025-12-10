package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.ForumPointsService;
import com.example.tomatomall.service.PostService;
import com.example.tomatomall.vo.post.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ForumPointsService forumPointsService;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Override
    @Transactional
    public PostVO createPost(PostCreateVO postCreateVO, Integer userId) {
        // 验证论坛存在
        Forum forum = forumRepository.findById(postCreateVO.getForumId())
                .orElseThrow(() -> new TomatoMallException("论坛不存在"));

        // 验证用户存在
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new TomatoMallException("用户不存在"));

        // 创建帖子
        Post post = new Post();
        post.setForumId(postCreateVO.getForumId());
        post.setUserId(userId);
        post.setTitle(postCreateVO.getTitle());
        post.setContent(postCreateVO.getContent());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setReplyCount(0);
        post.setIsSticky(false);
        post.setIsEssence(false);
        post.setStatus("NORMAL");
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());
        post.setImageUrls(postCreateVO.getImageUrlList());


        Post savedPost = postRepository.save(post);

        // 更新论坛帖子数
        forum.setPostCount(forum.getPostCount() + 1);
        forum.setUpdateTime(new Date());
        forumRepository.save(forum);

        // 发放积分
        forumPointsService.rewardPointsForPost(userId, savedPost.getId());

        // 组装返回数据
        PostVO result = savedPost.toVO();
        result.setUsername(user.getUsername());
        result.setUserAvatar(user.getAvatar());
        result.setForumName(forum.getName());

        return result;
    }


    @Override
    public PostVO getPostById(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new TomatoMallException("帖子不存在"));

        // 增加浏览量
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        // 获取用户信息
        Account user = accountRepository.findById(post.getUserId()).orElse(null);

        // 获取论坛信息
        Forum forum = forumRepository.findById(post.getForumId()).orElse(null);

        // 组装返回数据
        PostVO result = post.toVO();
        if (user != null) {
            result.setUsername(user.getUsername());
            result.setUserAvatar(user.getAvatar());
        }

        if (forum != null) {
            result.setForumName(forum.getName());
        }


        // 设置当前用户是否点赞
        if (userId != null) {
            boolean liked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
            result.setIsLiked(liked);
        } else {
            result.setIsLiked(false);
        }

        return result;
    }

    @Override
    @Transactional
    public void deletePost(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new TomatoMallException("帖子不存在"));

        // 验证用户是否有权限删除
        if (!post.getUserId().equals(userId)) {
            throw new TomatoMallException("无权删除该帖子");
        }

        // 软删除帖子
        post.setStatus("DELETED");
        post.setUpdateTime(new Date());
        postRepository.save(post);

        // 更新论坛帖子数
        Optional<Forum> forumOpt = forumRepository.findById(post.getForumId());
        if (forumOpt.isPresent()) {
            Forum forum = forumOpt.get();
            forum.setPostCount(Math.max(0, forum.getPostCount() - 1));
            forum.setUpdateTime(new Date());
            forumRepository.save(forum);
        }
    }

    @Override
    public Page<PostVO> getPostsByForum(Integer forumId, int page, int size) {
        // 验证论坛存在
        forumRepository.findById(forumId)
                .orElseThrow(() -> new TomatoMallException("论坛不存在"));

        // 分页查询帖子
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "isSticky", "createTime"));
        Page<Post> posts = postRepository.findByForumIdAndStatus(forumId, "NORMAL", pageable);

        // 转换为VO
        return posts.map(post -> {
            PostVO vo = post.toVO();

            // 获取用户信息
            Optional<Account> userOpt = accountRepository.findById(post.getUserId());
            userOpt.ifPresent(user -> {
                vo.setUsername(user.getUsername());
                vo.setUserAvatar(user.getAvatar());
            });

            // 获取论坛信息
            Optional<Forum> forumOpt = forumRepository.findById(post.getForumId());
            forumOpt.ifPresent(forum -> vo.setForumName(forum.getName()));

            return vo;
        });
    }

    @Override
    @Transactional
    public PostVO likePost(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new TomatoMallException("帖子不存在"));

        // 如果已点赞，则取消点赞
        Optional<PostLike> likeOpt = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (likeOpt.isPresent()) {
            postLikeRepository.delete(likeOpt.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            post.setUpdateTime(new Date());
            postRepository.save(post);
            PostVO vo = getPostById(postId, userId);
            vo.setIsLiked(false);
            return vo;
        }

        // 未点赞则点赞
        PostLike like = new PostLike();
        like.setPostId(postId);
        like.setUserId(userId);
        like.setCreateTime(new Date());
        postLikeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        post.setUpdateTime(new Date());
        postRepository.save(post);

        PostVO vo = getPostById(postId, userId);
        vo.setIsLiked(true);
        return vo;
    }
}
