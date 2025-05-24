package com.example.tomatomall.repository;

import com.example.tomatomall.po.MemberLevel;
import com.example.tomatomall.po.MemberPoints;
import com.example.tomatomall.po.PointsRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointsRecordRepository extends JpaRepository<PointsRecord, Integer> {
    List<PointsRecord> findByUserIdOrderByCreateTimeDesc(Integer userId);
    List<PointsRecord> findByUserIdAndRecordType(Integer userId, String recordType);
}
