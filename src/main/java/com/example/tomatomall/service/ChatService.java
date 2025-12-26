package com.example.tomatomall.service;

import com.example.tomatomall.po.ChatMessage;
import com.example.tomatomall.po.ChatSession;
import com.example.tomatomall.vo.PageResultVO;
import com.example.tomatomall.vo.chat.ChatMessageVO;
import com.example.tomatomall.vo.chat.ChatSessionVO;

import java.util.List;

public interface ChatService {

    // 会话管理
    List<ChatSessionVO> getUserChatSessions(Integer userId);
    ChatSessionVO getChatSession(Integer sessionId, Integer userId);
    ChatSession createOrGetSession(Integer customerId, Integer storeId);
    ChatSession createOrGetSessionWithCustomer(Integer merchantId, Integer customerId);

    // 消息管理
    PageResultVO<ChatMessageVO> getSessionMessages(Integer sessionId, Integer userId, Integer page, Integer pageSize);
    ChatMessage sendMessage(Integer sessionId, Integer senderId, String content, String messageType);

    // 状态管理
    void markSessionAsRead(Integer sessionId, Integer userId);
    void markAllSessionsAsRead(Integer userId);

    // 统计
    long getTotalUnreadCount(Integer userId);

    // 删除会话
    void archiveSession(Integer sessionId, Integer userId);
}
