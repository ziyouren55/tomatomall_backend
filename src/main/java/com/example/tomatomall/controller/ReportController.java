package com.example.tomatomall.controller;

import com.example.tomatomall.service.ReportService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.post.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 提交举报
     */
    @PostMapping
    public Response<ReportVO> createReport(@RequestBody @Valid ReportCreateVO reportVO,
                                 @RequestAttribute("userId") Integer userId) {
        ReportVO report = reportService.createReport(reportVO, userId);
        return Response.buildSuccess(report);
    }
}
