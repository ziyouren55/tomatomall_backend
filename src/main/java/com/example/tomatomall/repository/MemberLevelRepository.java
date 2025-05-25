package com.example.tomatomall.repository;

import com.example.tomatomall.po.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberLevelRepository extends JpaRepository<MemberLevel, Integer>
{
    Optional<MemberLevel> findByLevelName(String levelName);
    Optional<MemberLevel> findByMemberLevel(Integer memberLevel);
    List<MemberLevel> findByIsActiveTrue();
    Optional<MemberLevel> findFirstByPointsRequiredLessThanEqualOrderByPointsRequiredDesc(Integer points);
}
