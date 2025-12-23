package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.SchoolVerification;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.SchoolVerificationRepository;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.SchoolVerificationService;
import com.example.tomatomall.util.UserContext;
import com.example.tomatomall.vo.accounts.SchoolVerificationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

@Service
public class SchoolVerificationServiceImpl implements SchoolVerificationService {

    @Autowired
    private SchoolVerificationRepository schoolVerificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public SchoolVerificationVO submit(SchoolVerificationVO vo) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw TomatoMallException.notLogin();
        }

        // validate minimal fields
        if (vo.getSchoolName() == null || vo.getSchoolName().trim().isEmpty()) {
            throw TomatoMallException.paramError();
        }
        if (vo.getCertificateUrl() == null || vo.getCertificateUrl().trim().isEmpty()) {
            throw TomatoMallException.paramError();
        }

        // hash student id if provided (VO accepts studentId as write-only)
        String studentIdHash = null;
        if (vo.getStudentId() != null && !vo.getStudentId().trim().isEmpty()) {
            String raw = vo.getStudentId().trim();
            studentIdHash = sha256(raw);
        }

        // find existing record
        Optional<SchoolVerification> op = schoolVerificationRepository.findByUserId(currentUserId);
        SchoolVerification entity = op.orElseGet(SchoolVerification::new);
        entity.setUserId(currentUserId);
        entity.setSchoolName(vo.getSchoolName());
        if (studentIdHash != null) entity.setStudentIdHash(studentIdHash);
        entity.setCertificateUrl(vo.getCertificateUrl());
        entity.setStatus("PENDING");
        entity.setSubmittedAt(new Date());
        entity.setRejectedReason(null);

        SchoolVerification saved = schoolVerificationRepository.save(entity);

        SchoolVerificationVO ret = new SchoolVerificationVO();
        BeanUtils.copyProperties(saved, ret);
        return ret;
    }

    @Override
    public SchoolVerificationVO getByUsername(String username) {
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (!accountOpt.isPresent()) {
            throw TomatoMallException.usernameNotFind();
        }
        Integer userId = accountOpt.get().getId();
        return getByUserId(userId);
    }

    @Override
    public SchoolVerificationVO getByUserId(Integer userId) {
        Optional<SchoolVerification> op = schoolVerificationRepository.findByUserId(userId);
        if (!op.isPresent()) {
            return null;
        }
        SchoolVerification entity = op.get();
        SchoolVerificationVO vo = new SchoolVerificationVO();
        BeanUtils.copyProperties(entity, vo);
        // do not expose student id hash
        return vo;
    }

    @Override
    public java.util.List<SchoolVerificationVO> listByStatus(String status) {
        java.util.List<SchoolVerification> list;
        if (status == null || status.trim().isEmpty()) {
            list = schoolVerificationRepository.findAll();
        } else {
            list = schoolVerificationRepository.findAll().stream()
                    .filter(sv -> status.equalsIgnoreCase(sv.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
        }
        java.util.List<SchoolVerificationVO> vos = new java.util.ArrayList<>();
        for (SchoolVerification s : list) {
            SchoolVerificationVO vo = new SchoolVerificationVO();
            BeanUtils.copyProperties(s, vo);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public SchoolVerificationVO approve(Integer id) {
        Optional<SchoolVerification> op = schoolVerificationRepository.findById(id);
        if (!op.isPresent()) {
            throw new com.example.tomatomall.exception.TomatoMallException("认证记录不存在");
        }
        SchoolVerification entity = op.get();
        entity.setStatus("VERIFIED");
        entity.setVerifiedAt(new Date());
        SchoolVerification saved = schoolVerificationRepository.save(entity);
        SchoolVerificationVO vo = new SchoolVerificationVO();
        BeanUtils.copyProperties(saved, vo);
        return vo;
    }

    @Override
    public SchoolVerificationVO reject(Integer id, String reason) {
        Optional<SchoolVerification> op = schoolVerificationRepository.findById(id);
        if (!op.isPresent()) {
            throw new com.example.tomatomall.exception.TomatoMallException("认证记录不存在");
        }
        SchoolVerification entity = op.get();
        entity.setStatus("REJECTED");
        entity.setRejectedReason(reason);
        SchoolVerification saved = schoolVerificationRepository.save(entity);
        SchoolVerificationVO vo = new SchoolVerificationVO();
        BeanUtils.copyProperties(saved, vo);
        return vo;
    }

    // helper: sha256 (unused currently)
    private String sha256(String input) {
        if (input == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}


