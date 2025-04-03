package com.example.tomatomall.controller;

import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.vo.accounts.AccountVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Resource
    AccountService accountService;

    /**
     * 获取用户详情
     */
    @GetMapping("/{username}")
    public Response getUser(@PathVariable("username") String username) {
        return Response.buildSuccess(accountService.getUser(username));
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
}
