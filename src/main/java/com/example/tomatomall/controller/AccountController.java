package com.example.tomatomall.controller;

import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.service.SchoolVerificationService;
import com.example.tomatomall.vo.accounts.AccountVO;
import com.example.tomatomall.vo.accounts.SchoolVerificationVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Resource
    AccountService accountService;

    @Resource
    SchoolVerificationService schoolVerificationService;

    /**
     * 获取用户详情
     */
    @GetMapping("/{username}")
    public Response getUser(@PathVariable("username") String username) {
        return Response.buildSuccess(accountService.getUser(username));
    }

    /**
     * 根据用户ID获取用户详情（公开，用于前端根据 merchantId 跳转到用户名页面）
     */
    @GetMapping("/id/{id}")
    public Response getUserById(@PathVariable("id") Integer id) {
        return Response.buildSuccess(accountService.getUserById(id));
    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response createUser(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.register(accountVO));
    }

    /**
     * 更新用户信息
     */
    @PutMapping()
    public Response updateUser(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.updateUser(accountVO));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response login(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.login(accountVO));
    }

    /**
     * 提交学校认证（当前登录用户）
     */
    @PostMapping("/school-verification")
    public Response submitSchoolVerification(@RequestBody SchoolVerificationVO vo) {
        return Response.buildSuccess(schoolVerificationService.submit(vo));
    }

    /**
     * 查询某用户的学校认证状态
     */
    @GetMapping("/{username}/school-verification")
    public Response getSchoolVerification(@PathVariable("username") String username) {
        return Response.buildSuccess(schoolVerificationService.getByUsername(username));
    }
}
