package com.example.tomatomall.repository;

import com.example.tomatomall.po.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 分页获取会话消息
    Page<ChatMessage> findBySessionIdOrderByCreatedAtDesc(Integer sessionId, Pageable pageable);

    // 获取会话的最新消息
    ChatMessage findTopBySessionIdOrderByCreatedAtDesc(Integer sessionId);
}
