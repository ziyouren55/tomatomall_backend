package com.example.tomatomall.repository;

import com.example.tomatomall.po.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderId(Integer orderId);
    List<OrderItem> findByOrderIdAndMerchantId(Integer orderId, Integer merchantId);
    List<OrderItem> findByOrderIdAndStoreId(Integer orderId, Integer storeId);
}

