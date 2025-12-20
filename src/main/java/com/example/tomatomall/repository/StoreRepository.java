package com.example.tomatomall.repository;

import com.example.tomatomall.po.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    List<Store> findByMerchantId(Integer merchantId);
    Page<Store> findByMerchantId(Integer merchantId, Pageable pageable);
}


