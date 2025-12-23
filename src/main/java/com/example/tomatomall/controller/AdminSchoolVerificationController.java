package com.example.tomatomall.controller;

import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.accounts.SchoolVerificationVO;
import com.example.tomatomall.service.SchoolVerificationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/admin/school-verifications")
public class AdminSchoolVerificationController {

    @Resource
    private SchoolVerificationService schoolVerificationService;

    /**
     * 列表：可按 status 过滤（PENDING/VERIFIED/REJECTED）
     */
    @GetMapping
    public Response<java.util.List<SchoolVerificationVO>> list(@RequestParam(value = "status", required = false) String status) {
        List<SchoolVerificationVO> list = schoolVerificationService.listByStatus(status);
        return Response.buildSuccess(list);
    }

    /**
     * 管理员通过
     */
    @PostMapping("/{id}/approve")
    public Response<SchoolVerificationVO> approve(@PathVariable("id") Integer id) {
        return Response.buildSuccess(schoolVerificationService.approve(id));
    }

    /**
     * 管理员驳回（需要传 reason）
     */
    @PostMapping("/{id}/reject")
    public Response<SchoolVerificationVO> reject(@PathVariable("id") Integer id, @RequestBody(required = false) java.util.Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return Response.buildSuccess(schoolVerificationService.reject(id, reason));
    }
}


