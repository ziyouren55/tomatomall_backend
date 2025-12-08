package com.example.tomatomall.repository;

import com.example.tomatomall.po.MemberLevel;
import com.example.tomatomall.po.MemberPoints;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberPointsRepository extends JpaRepository<MemberPoints, Integer> {
    Optional<MemberPoints> findByUserId(Integer userId);
    long countByCurrentLevelId(Integer levelId);
}
