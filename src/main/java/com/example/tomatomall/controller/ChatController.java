package com.example.tomatomall.controller;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.ChatMessage;
import com.example.tomatomall.po.ChatSession;
import com.example.tomatomall.service.ChatService;
import com.example.tomatomall.util.UserContext;
import com.example.tomatomall.vo.PageResultVO;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.chat.ChatMessageVO;
import com.example.tomatomall.vo.chat.ChatSessionVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    // 获取用户的聊天会话列表
    @GetMapping("/sessions")
    public Response<List<ChatSessionVO>> getChatSessions() {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();

        List<ChatSessionVO> sessions = chatService.getUserChatSessions(currentUserId);
        return Response.buildSuccess(sessions);
    }

    // 获取特定会话详情
    @GetMapping("/sessions/{sessionId}")
    public Response<ChatSessionVO> getChatSession(@PathVariable Integer sessionId) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();

        ChatSessionVO session = chatService.getChatSession(sessionId, currentUserId);
        return Response.buildSuccess(session);
    }

    // 获取会话消息历史
    @GetMapping("/sessions/{sessionId}/messages")
    public Response<PageResultVO<ChatMessageVO>> getSessionMessages(
            @PathVariable Integer sessionId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();

        PageResultVO<ChatMessageVO> messages = chatService.getSessionMessages(sessionId, currentUserId, page, pageSize);
        return Response.buildSuccess(messages);
    }

    // 创建或获取与商家的聊天会话
    @PostMapping("/sessions")
    public Response<ChatSessionVO> createSession(@RequestBody CreateSessionRequest request) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();

        ChatSession session = chatService.createOrGetSession(currentUserId, request.getStoreId());
        ChatSessionVO sessionVO = chatService.getChatSession(session.getId(), currentUserId);
        return Response.buildSuccess(sessionVO);
    }

    // 发送消息
    @PostMapping("/sessions/{sessionId}/messages")
    public Response<ChatMessageVO> sendMessage(@PathVariable Integer sessionId, @RequestBody SendMessageRequest request) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();

        // 验证会话权限（senderRole由后端确定）
        chatService.getChatSession(sessionId, currentUserId);

        ChatMessage message = chatService.sendMessage(sessionId, currentUserId,
                                                    request.getContent(), request.getMessageType());
        ChatMessageVO messageVO = new ChatMessageVO();
        // 设置messageVO的基本信息
        messageVO.setId(message.getId());
        messageVO.setSessionId(message.getSessionId());
        messageVO.setSenderId(message.getSenderId());
        messageVO.setSenderRole(message.getSenderRole());
        messageVO.setContent(message.getContent());
        messageVO.setMessageType(message.getMessageType());
        messageVO.setStatus(message.getStatus());
        messageVO.setCreatedAt(message.getCreatedAt());

        return Response.buildSuccess(messageVO);
    }

    // 标记会话为已读
    @PostMapping("/sessions/{sessionId}/mark-read")
    public Response<String> markSessionAsRead(@PathVariable Integer sessionId) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();

        chatService.markSessionAsRead(sessionId, currentUserId);
        return Response.buildSuccess("标记已读成功");
    }

    // 标记所有会话为已读
    @PostMapping("/mark-all-read")
    public Response<String> markAllSessionsAsRead() {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();

        chatService.markAllSessionsAsRead(currentUserId);
        return Response.buildSuccess("标记所有已读成功");
    }

    // 获取未读消息总数
    @GetMapping("/unread-count")
    public Response<Long> getUnreadCount() {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();

        long count = chatService.getTotalUnreadCount(currentUserId);
        return Response.buildSuccess(count);
    }

    // 归档会话
    @PostMapping("/sessions/{sessionId}/archive")
    public Response<String> archiveSession(@PathVariable Integer sessionId) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();

        chatService.archiveSession(sessionId, currentUserId);
        return Response.buildSuccess("会话已归档");
    }
}

// 请求类
class CreateSessionRequest {
    private Integer storeId;

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
}

class SendMessageRequest {
    private String content;
    private String messageType = "TEXT";

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
