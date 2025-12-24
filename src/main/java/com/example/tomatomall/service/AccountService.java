package com.example.tomatomall.service;

import com.example.tomatomall.vo.accounts.AccountVO;

public interface AccountService {

    String register(AccountVO accountVO);

    String login(AccountVO accountVO);

    AccountVO getUser(String username);

    AccountVO getUserById(Integer id);

    AccountVO updateUser(AccountVO accountVO);
    /**
     * 返回单独的学校/认证信息 VO（不包含 AccountVO），用于前端按需获取
     */
    com.example.tomatomall.vo.accounts.UserSchoolVO getUserSchool(String username);
    com.example.tomatomall.vo.accounts.UserSchoolVO getUserSchoolById(Integer id);

}
