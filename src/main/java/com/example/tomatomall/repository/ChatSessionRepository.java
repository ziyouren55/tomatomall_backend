package com.example.tomatomall.repository;

import com.example.tomatomall.po.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Integer> {

    // 查找用户的所有聊天会话
    @Query("SELECT cs FROM ChatSession cs WHERE (cs.customerId = ?1 OR cs.merchantId = ?1) AND cs.status = 'ACTIVE' ORDER BY cs.lastMessageTime DESC")
    List<ChatSession> findActiveSessionsByUserId(Integer userId);

    // 根据顾客和店铺查找会话
    Optional<ChatSession> findByCustomerIdAndStoreId(Integer customerId, Integer storeId);

    // 统计用户未读消息总数
    @Query("SELECT COALESCE(SUM(CASE WHEN cs.customerId = ?1 THEN cs.unreadCountCustomer ELSE cs.unreadCountMerchant END), 0) FROM ChatSession cs WHERE (cs.customerId = ?1 OR cs.merchantId = ?1) AND cs.status = 'ACTIVE'")
    Integer countTotalUnreadByUserId(Integer userId);
}
