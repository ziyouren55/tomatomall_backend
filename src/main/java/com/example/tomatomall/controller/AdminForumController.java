package com.example.tomatomall.controller;

import com.example.tomatomall.service.ForumService;
import com.example.tomatomall.service.ReportService;
import com.example.tomatomall.vo.Response;
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
    @PostMapping("/books/{bookId}/forum")
    public Response createBookForum(@PathVariable Integer bookId) {
        return Response.buildSuccess(forumService.createBookForum(bookId));
    }

    /**
     * 获取待处理的举报列表
     */
    @GetMapping("/reports/pending")
    public Response getPendingReports(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        Page<ReportVO> reports = reportService.getPendingReports(page, size);
        return Response.buildSuccess(reports);
    }

    /**
     * 处理举报
     */
    @PutMapping("/reports/{reportId}")
    public Response processReport(@PathVariable Integer reportId,
                                  @RequestParam String status,
                                  @RequestAttribute("userId") Integer processedBy) {
        ReportVO report = reportService.processReport(reportId, status, processedBy);
        return Response.buildSuccess(report);
    }
}
