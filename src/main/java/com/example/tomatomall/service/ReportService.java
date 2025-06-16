package com.example.tomatomall.service;

import com.example.tomatomall.vo.post.ReportCreateVO;
import com.example.tomatomall.vo.post.ReportVO;
import org.springframework.data.domain.Page;

public interface ReportService {
    // 提交举报
    ReportVO createReport(ReportCreateVO reportVO, Integer userId);

    // 处理举报
    ReportVO processReport(Integer reportId, String status, Integer processedBy);

    // 获取待处理的举报列表
    Page<ReportVO> getPendingReports(int page, int size);
}
