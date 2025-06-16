package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Post;
import com.example.tomatomall.po.Reply;
import com.example.tomatomall.po.Report;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.PostRepository;
import com.example.tomatomall.repository.ReplyRepository;
import com.example.tomatomall.repository.ReportRepository;
import com.example.tomatomall.service.ReportService;
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
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Override
    @Transactional
    public ReportVO createReport(ReportCreateVO reportVO, Integer userId) {
        // 验证用户存在
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new TomatoMallException("用户不存在"));

        // 验证目标内容存在
        if ("POST".equals(reportVO.getTargetType())) {
            postRepository.findById(reportVO.getTargetId())
                    .orElseThrow(() -> new TomatoMallException("举报的帖子不存在"));
        } else if ("REPLY".equals(reportVO.getTargetType())) {
            replyRepository.findById(reportVO.getTargetId())
                    .orElseThrow(() -> new TomatoMallException("举报的回复不存在"));
        } else {
            throw new TomatoMallException("不支持的举报目标类型");
        }

        // 检查是否重复举报
        boolean hasReported = reportRepository.existsByUserIdAndTargetIdAndTargetTypeAndStatus(
                userId, reportVO.getTargetId(), reportVO.getTargetType(), "PENDING");
        if (hasReported) {
            throw new TomatoMallException("您已经举报过该内容，请勿重复举报");
        }

        // 创建举报记录
        Report report = new Report();
        report.setUserId(userId);
        report.setTargetId(reportVO.getTargetId());
        report.setTargetType(reportVO.getTargetType());
        report.setReasonType(reportVO.getReasonType());
        report.setDescription(reportVO.getDescription());
        report.setStatus("PENDING");
        report.setCreateTime(new Date());

        Report savedReport = reportRepository.save(report);

        // 组装返回数据
        ReportVO result = savedReport.toVO();
        result.setUsername(user.getUsername());

        // 获取被举报内容摘要
        String targetContent = getTargetContent(reportVO.getTargetId(), reportVO.getTargetType());
        result.setTargetContent(targetContent);

        return result;
    }

    // 获取被举报内容的摘要
    private String getTargetContent(Integer targetId, String targetType) {
        String content = "";

        if ("POST".equals(targetType)) {
            Optional<Post> postOpt = postRepository.findById(targetId);
            if (postOpt.isPresent()) {
                String fullContent = postOpt.get().getContent();
                content = fullContent.length() > 50 ? fullContent.substring(0, 50) + "..." : fullContent;
            }
        } else if ("REPLY".equals(targetType)) {
            Optional<Reply> replyOpt = replyRepository.findById(targetId);
            if (replyOpt.isPresent()) {
                String fullContent = replyOpt.get().getContent();
                content = fullContent.length() > 50 ? fullContent.substring(0, 50) + "..." : fullContent;
            }
        }

        return content;
    }

    @Override
    @Transactional
    public ReportVO processReport(Integer reportId, String status, Integer processedBy) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new TomatoMallException("举报记录不存在"));

        // 验证处理人存在
        accountRepository.findById(processedBy)
                .orElseThrow(() -> new TomatoMallException("处理人不存在"));

        // 更新举报状态
        report.setStatus(status);
        report.setProcessedBy(processedBy);
        report.setProcessedTime(new Date());

        Report updatedReport = reportRepository.save(report);

        // 如果举报通过，处理被举报内容（隐藏或删除）
        if ("APPROVED".equals(status)) {
            if ("POST".equals(report.getTargetType())) {
                Optional<Post> postOpt = postRepository.findById(report.getTargetId());
                postOpt.ifPresent(post -> {
                    post.setStatus("HIDDEN");
                    post.setUpdateTime(new Date());
                    postRepository.save(post);
                });
            } else if ("REPLY".equals(report.getTargetType())) {
                Optional<Reply> replyOpt = replyRepository.findById(report.getTargetId());
                replyOpt.ifPresent(reply -> {
                    reply.setStatus("HIDDEN");
                    reply.setUpdateTime(new Date());
                    replyRepository.save(reply);
                });
            }
        }

        // 组装返回数据
        ReportVO result = updatedReport.toVO();

        // 获取用户信息
        Optional<Account> userOpt = accountRepository.findById(updatedReport.getUserId());
        userOpt.ifPresent(user -> result.setUsername(user.getUsername()));

        // 获取被举报内容摘要
        String targetContent = getTargetContent(updatedReport.getTargetId(), updatedReport.getTargetType());
        result.setTargetContent(targetContent);

        return result;
    }

    @Override
    public Page<ReportVO> getPendingReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createTime"));
        Page<Report> reports = reportRepository.findByStatus("PENDING", pageable);

        return reports.map(report -> {
            ReportVO vo = report.toVO();

            // 获取举报用户信息
            Optional<Account> userOpt = accountRepository.findById(report.getUserId());
            userOpt.ifPresent(user -> vo.setUsername(user.getUsername()));

            // 获取被举报内容摘要
            String targetContent = getTargetContent(report.getTargetId(), report.getTargetType());
            vo.setTargetContent(targetContent);

            return vo;
        });
    }
}
