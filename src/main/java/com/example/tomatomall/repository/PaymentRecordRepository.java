package com.example.tomatomall.repository;

import com.example.tomatomall.po.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    Optional<PaymentRecord> findByTradeNo(String tradeNo);
}



