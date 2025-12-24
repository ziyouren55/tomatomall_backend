package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.ChatService;
import com.example.tomatomall.vo.PageResultVO;
import com.example.tomatomall.vo.chat.ChatMessageVO;
import com.example.tomatomall.vo.chat.ChatSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public List<ChatSessionVO> getUserChatSessions(Integer userId) {
        List<ChatSession> sessions = chatSessionRepository.findActiveSessionsByUserId(userId);
        return sessions.stream().map(this::convertToSessionVO).collect(Collectors.toList());
    }

    @Override
    public ChatSessionVO getChatSession(Integer sessionId, Integer userId) {
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw TomatoMallException.chatSessionNotFound();
        }

        ChatSession session = sessionOpt.get();
        if (!session.getCustomerId().equals(userId) && !session.getMerchantId().equals(userId)) {
            throw TomatoMallException.permissionDenied();
        }

        return convertToSessionVO(session);
    }

    @Override
    @Transactional
    public ChatSession createOrGetSession(Integer customerId, Integer storeId) {
        Optional<ChatSession> existingSession = chatSessionRepository.findByCustomerIdAndStoreId(customerId, storeId);
        if (existingSession.isPresent()) {
            return existingSession.get();
        }

        // 创建新会话
        ChatSession session = new ChatSession();
        session.setCustomerId(customerId);
        session.setStoreId(storeId);

        // 获取店铺对应的商家ID
        Optional<Store> storeOpt = storeRepository.findById(storeId);
        if (storeOpt.isEmpty()) {
            throw TomatoMallException.storeNotFind();
        }
        session.setMerchantId(storeOpt.get().getMerchantId());

        return chatSessionRepository.save(session);
    }

    @Override
    public PageResultVO<ChatMessageVO> getSessionMessages(Integer sessionId, Integer userId, Integer page, Integer pageSize) {
        // 验证用户权限
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw TomatoMallException.chatSessionNotFound();
        }

        ChatSession session = sessionOpt.get();
        if (!session.getCustomerId().equals(userId) && !session.getMerchantId().equals(userId)) {
            throw TomatoMallException.permissionDenied();
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtDesc(sessionId, pageable);

        List<ChatMessageVO> messageVOs = messages.getContent().stream()
                .map(this::convertToMessageVO)
                .collect(Collectors.toList());

        return new PageResultVO<ChatMessageVO>(messageVOs, messages.getTotalElements(), page, pageSize);
    }

    @Override
    @Transactional
    public ChatMessage sendMessage(Integer sessionId, Integer senderId, String content, String messageType) {
        // 验证会话存在且用户有权限
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw TomatoMallException.chatSessionNotFound();
        }

        ChatSession session = sessionOpt.get();

        // 根据senderId确定实际的发送者角色
        String actualSenderRole;
        if (session.getCustomerId().equals(senderId)) {
            actualSenderRole = "CUSTOMER";
        } else if (session.getMerchantId().equals(senderId)) {
            actualSenderRole = "MERCHANT";
        } else {
            // 用户不是该会话的参与者
            throw TomatoMallException.permissionDenied();
        }

        // 创建消息
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderId(senderId);
        message.setSenderRole(actualSenderRole);
        message.setContent(content);
        message.setMessageType(messageType);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // 更新会话的最后消息信息
        session.setLastMessage(content);
        session.setLastMessageTime(savedMessage.getCreatedAt());

        // 更新未读消息计数
        boolean isCustomer = "CUSTOMER".equals(actualSenderRole);
        if (isCustomer) {
            session.setUnreadCountMerchant(session.getUnreadCountMerchant() + 1);
        } else {
            session.setUnreadCountCustomer(session.getUnreadCountCustomer() + 1);
        }

        chatSessionRepository.save(session);

        // 通过WebSocket推送消息
        ChatMessageVO messageVO = convertToMessageVO(savedMessage);

        // 推送给接收方
        simpMessagingTemplate.convertAndSendToUser(
            String.valueOf(isCustomer ? session.getMerchantId() : session.getCustomerId()),
            "/chat",
            messageVO
        );

        // 也推送给自己，确保发送方能看到自己的消息（替换临时消息）
        simpMessagingTemplate.convertAndSendToUser(
            String.valueOf(senderId),
            "/chat",
            messageVO
        );

        return savedMessage;
    }

    @Override
    @Transactional
    public void markSessionAsRead(Integer sessionId, Integer userId) {
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return;
        }

        ChatSession session = sessionOpt.get();
        if (session.getCustomerId().equals(userId)) {
            session.setUnreadCountCustomer(0);
        } else if (session.getMerchantId().equals(userId)) {
            session.setUnreadCountMerchant(0);
        } else {
            throw TomatoMallException.permissionDenied();
        }

        chatSessionRepository.save(session);
    }

    @Override
    @Transactional
    public void markAllSessionsAsRead(Integer userId) {
        List<ChatSession> sessions = chatSessionRepository.findActiveSessionsByUserId(userId);
        for (ChatSession session : sessions) {
            if (session.getCustomerId().equals(userId)) {
                session.setUnreadCountCustomer(0);
            } else {
                session.setUnreadCountMerchant(0);
            }
        }
        chatSessionRepository.saveAll(sessions);
    }

    @Override
    public long getTotalUnreadCount(Integer userId) {
        return chatSessionRepository.countTotalUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void archiveSession(Integer sessionId, Integer userId) {
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return;
        }

        ChatSession session = sessionOpt.get();
        if (!session.getCustomerId().equals(userId) && !session.getMerchantId().equals(userId)) {
            throw TomatoMallException.permissionDenied();
        }

        session.setStatus("ARCHIVED");
        chatSessionRepository.save(session);
    }

    private ChatSessionVO convertToSessionVO(ChatSession session) {
        ChatSessionVO vo = new ChatSessionVO();
        vo.setId(session.getId());
        vo.setCustomerId(session.getCustomerId());
        vo.setMerchantId(session.getMerchantId());
        vo.setStoreId(session.getStoreId());
        vo.setLastMessage(session.getLastMessage());
        vo.setLastMessageTime(session.getLastMessageTime());
        vo.setUnreadCountCustomer(session.getUnreadCountCustomer());
        vo.setUnreadCountMerchant(session.getUnreadCountMerchant());
        vo.setStatus(session.getStatus());

        // 获取店铺信息
        Optional<Store> storeOpt = storeRepository.findById(session.getStoreId());
        if (storeOpt.isPresent()) {
            vo.setStoreName(storeOpt.get().getName());
        }

        // 获取用户信息
        Optional<Account> customerOpt = accountRepository.findById(session.getCustomerId());
        if (customerOpt.isPresent()) {
            vo.setCustomerName(customerOpt.get().getName());
            vo.setCustomerAvatar(customerOpt.get().getAvatar());
        }

        Optional<Account> merchantOpt = accountRepository.findById(session.getMerchantId());
        if (merchantOpt.isPresent()) {
            vo.setMerchantName(merchantOpt.get().getName());
            vo.setMerchantAvatar(merchantOpt.get().getAvatar());
        }

        return vo;
    }

    private ChatMessageVO convertToMessageVO(ChatMessage message) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(message.getId());
        vo.setSessionId(message.getSessionId());
        vo.setSenderId(message.getSenderId());
        vo.setSenderRole(message.getSenderRole());
        vo.setContent(message.getContent());
        vo.setMessageType(message.getMessageType());
        vo.setStatus(message.getStatus());
        vo.setCreatedAt(message.getCreatedAt());

        // 获取发送者信息
        Optional<Account> senderOpt = accountRepository.findById(message.getSenderId());
        if (senderOpt.isPresent()) {
            vo.setSenderName(senderOpt.get().getName());
            vo.setSenderAvatar(senderOpt.get().getAvatar());
        }

        return vo;
    }
}
