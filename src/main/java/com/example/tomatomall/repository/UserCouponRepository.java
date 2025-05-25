package com.example.tomatomall.repository;

import com.example.tomatomall.po.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {
    List<UserCoupon> findByUserIdAndIsUsedFalse(Integer userId);
    Optional<UserCoupon> findFirstByUserIdAndCouponIdAndIsUsedFalse(Integer userId, Integer couponId);
}
