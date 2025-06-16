package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Post;
import com.example.tomatomall.po.Reply;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.PostRepository;
import com.example.tomatomall.repository.ReplyRepository;
import com.example.tomatomall.service.ForumPointsService;
import com.example.tomatomall.service.ReplyService;
import com.example.tomatomall.vo.post.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ForumPointsService forumPointsService;

    @Override
    @Transactional
    public ReplyVO createReply(ReplyCreateVO replyVO, Integer userId) {
        // 验证帖子存在
        Post post = postRepository.findById(replyVO.getPostId())
                .orElseThrow(() -> new TomatoMallException("帖子不存在"));

        // 验证用户存在
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new TomatoMallException("用户不存在"));

        // 如果是回复其他回复，验证父回复存在
        String parentUsername = null;
        if (replyVO.getParentId() != null) {
            Reply parentReply = replyRepository.findById(replyVO.getParentId())
                    .orElseThrow(() -> new TomatoMallException("被回复的内容不存在"));

            // 获取被回复用户的用户名
            Optional<Account> parentUser = accountRepository.findById(parentReply.getUserId());
            if (parentUser.isPresent()) {
                parentUsername = parentUser.get().getUsername();
            }
        }

        // 创建回复
        Reply reply = new Reply();
        reply.setPostId(replyVO.getPostId());
        reply.setUserId(userId);
        reply.setContent(replyVO.getContent());
        reply.setParentId(replyVO.getParentId());
        reply.setLikeCount(0);
        reply.setStatus("NORMAL");
        reply.setCreateTime(new Date());
        reply.setUpdateTime(new Date());

        Reply savedReply = replyRepository.save(reply);

        // 更新帖子回复数和最后回复时间
        post.setReplyCount(post.getReplyCount() + 1);
        post.setLastReplyTime(new Date());
        post.setUpdateTime(new Date());
        postRepository.save(post);

        // 发放积分
        forumPointsService.rewardPointsForReply(userId, savedReply.getId());

        // 组装返回数据
        ReplyVO result = savedReply.toVO();
        result.setUsername(user.getUsername());
        result.setUserAvatar(user.getAvatar());
        result.setParentUsername(parentUsername);
        result.setIsLiked(false);

        return result;
    }

    @Override
    @Transactional
    public void deleteReply(Integer replyId, Integer userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new TomatoMallException("回复不存在"));

        // 验证用户是否有权限删除
        if (!reply.getUserId().equals(userId)) {
            throw new TomatoMallException("无权删除该回复");
        }

        // 软删除回复
        reply.setStatus("DELETED");
        reply.setUpdateTime(new Date());
        replyRepository.save(reply);

        // 更新帖子回复数
        Optional<Post> postOpt = postRepository.findById(reply.getPostId());
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setReplyCount(Math.max(0, post.getReplyCount() - 1));
            post.setUpdateTime(new Date());
            postRepository.save(post);
        }
    }

    @Override
    public Page<ReplyVO> getRepliesByPost(Integer postId, int page, int size) {
        // 验证帖子存在
        postRepository.findById(postId)
                .orElseThrow(() -> new TomatoMallException("帖子不存在"));

        // 分页查询回复
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createTime"));
        Page<Reply> replies = replyRepository.findByPostIdAndStatus(postId, "NORMAL", pageable);

        // 转换为VO
        return replies.map(reply -> {
            ReplyVO vo = reply.toVO();

            // 获取用户信息
            Optional<Account> userOpt = accountRepository.findById(reply.getUserId());
            userOpt.ifPresent(user -> {
                vo.setUsername(user.getUsername());
                vo.setUserAvatar(user.getAvatar());
            });

            // 获取父回复信息
            if (reply.getParentId() != null) {
                Optional<Reply> parentReplyOpt = replyRepository.findById(reply.getParentId());
                if (parentReplyOpt.isPresent()) {
                    Optional<Account> parentUserOpt = accountRepository.findById(parentReplyOpt.get().getUserId());
                    parentUserOpt.ifPresent(parentUser -> vo.setParentUsername(parentUser.getUsername()));
                }
            }

            // 获取子回复
            List<Reply> childReplies = replyRepository.findByPostIdAndParentIdAndStatus(
                    postId, reply.getId(), "NORMAL");

            if (!childReplies.isEmpty()) {
                List<ReplyVO> childReplyVOs = childReplies.stream()
                        .map(childReply -> {
                            ReplyVO childVo = childReply.toVO();

                            // 获取子回复用户信息
                            Optional<Account> childUserOpt = accountRepository.findById(childReply.getUserId());
                            childUserOpt.ifPresent(childUser -> {
                                childVo.setUsername(childUser.getUsername());
                                childVo.setUserAvatar(childUser.getAvatar());
                            });

                            // 获取被回复人用户名
                            childVo.setParentUsername(vo.getUsername());

                            return childVo;
                        })
                        .collect(Collectors.toList());

                vo.setChildReplies(childReplyVOs);
            }

            return vo;
        });
    }

    @Override
    @Transactional
    public ReplyVO likeReply(Integer replyId, Integer userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new TomatoMallException("回复不存在"));

        // 检查用户是否已点赞（实际项目中应该有专门的点赞记录表）
        // 这里简化处理，直接增加点赞数
        reply.setLikeCount(reply.getLikeCount() + 1);
        reply.setUpdateTime(new Date());

        Reply updatedReply = replyRepository.save(reply);

        // 返回更新后的回复
        ReplyVO vo = updatedReply.toVO();

        // 获取用户信息
        Optional<Account> userOpt = accountRepository.findById(updatedReply.getUserId());
        userOpt.ifPresent(user -> {
            vo.setUsername(user.getUsername());
            vo.setUserAvatar(user.getAvatar());
        });

        // 获取父回复信息
        if (updatedReply.getParentId() != null) {
            Optional<Reply> parentReplyOpt = replyRepository.findById(updatedReply.getParentId());
            if (parentReplyOpt.isPresent()) {
                Optional<Account> parentUserOpt = accountRepository.findById(parentReplyOpt.get().getUserId());
                parentUserOpt.ifPresent(parentUser -> vo.setParentUsername(parentUser.getUsername()));
            }
        }

        vo.setIsLiked(true);

        return vo;
    }
}
