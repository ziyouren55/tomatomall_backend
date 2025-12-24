package com.example.tomatomall.repository;

import com.example.tomatomall.po.SchoolVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolVerificationRepository extends JpaRepository<SchoolVerification, Integer> {
    Optional<SchoolVerification> findByUserId(Integer userId);
    java.util.List<SchoolVerification> findByStatus(String status);
    java.util.List<SchoolVerification> findBySchoolNameContainingIgnoreCaseAndStatus(String schoolName, String status);
}


