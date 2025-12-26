package com.example.tomatomall.controller;

import com.example.tomatomall.service.ForumService;
import com.example.tomatomall.service.ReportService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.forum.ForumVO;
import com.example.tomatomall.vo.post.ReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/forum")
public class AdminForumController {

    @Autowired
    private ForumService forumService;

    @Autowired
    private ReportService reportService;

    /**
     * 创建书籍论坛
     */
    //todo 将book相关的命名重新改为product，需前后端一致
    @PostMapping("/books/{bookId}/forum")
    public Response<ForumVO> createProductForum(@PathVariable Integer bookId) {
        return Response.buildSuccess(forumService.createProductForum(bookId));
    }

    /**
     * 确保书籍论坛存在：
     * 若已存在则直接返回；若不存在则创建后返回
     */
    @PostMapping("/books/{bookId}/forum/ensure")
    public Response<ForumVO> ensureProductForum(@PathVariable Integer bookId) {
        return Response.buildSuccess(forumService.ensureProductForum(bookId));
    }

    /**
     * 获取待处理的举报列表
     */
    @GetMapping("/reports/pending")
    public Response<Page<ReportVO>> getPendingReports(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        Page<ReportVO> reports = reportService.getPendingReports(page, size);
        return Response.buildSuccess(reports);
    }

    /**
     * 处理举报
     */
    @PutMapping("/reports/{reportId}")
    public Response<ReportVO> processReport(@PathVariable Integer reportId,
                                  @RequestParam String status,
                                  @RequestAttribute("userId") Integer processedBy) {
        ReportVO report = reportService.processReport(reportId, status, processedBy);
        return Response.buildSuccess(report);
    }
}
