package com.example.tomatomall.service;

import com.example.tomatomall.vo.accounts.AccountVO;

public interface AccountService {

    String register(AccountVO accountVO);

    String login(AccountVO accountVO);

    AccountVO getUser(String username);

    AccountVO updateUser(AccountVO accountVO);

}
