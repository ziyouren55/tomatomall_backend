package com.example.tomatomall.service;

import com.example.tomatomall.vo.accounts.SchoolVerificationVO;

public interface SchoolVerificationService {
    SchoolVerificationVO submit(SchoolVerificationVO vo);
    SchoolVerificationVO getByUsername(String username);
    SchoolVerificationVO getByUserId(Integer userId);
    java.util.List<com.example.tomatomall.vo.accounts.SchoolVerificationVO> listByStatus(String status);
    com.example.tomatomall.vo.accounts.SchoolVerificationVO approve(Integer id);
    com.example.tomatomall.vo.accounts.SchoolVerificationVO reject(Integer id, String reason);
}


