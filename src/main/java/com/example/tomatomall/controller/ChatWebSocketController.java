package com.example.tomatomall.controller;

import com.example.tomatomall.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class ChatWebSocketController {

    @Resource
    private ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatWebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // 从header中获取用户ID（通过TokenHandshakeHandler设置）
        if (headerAccessor.getUser() == null || headerAccessor.getUser().getName() == null) {
            return;
        }
        String userId = headerAccessor.getUser().getName();

        // 发送消息（senderRole由后端根据用户身份确定）
        chatService.sendMessage(message.getSessionId(), Integer.valueOf(userId),
                              message.getContent(), message.getMessageType());
    }

    @MessageMapping("/chat.mark-read")
    public void markAsRead(@Payload MarkReadMessage message, SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getUser() == null || headerAccessor.getUser().getName() == null) {
            return;
        }
        String userId = headerAccessor.getUser().getName();

        chatService.markSessionAsRead(message.getSessionId(), Integer.valueOf(userId));
    }
}

// WebSocket消息类
class ChatWebSocketMessage {
    private Integer sessionId;
    private String content;
    private String messageType = "TEXT";

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

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

class MarkReadMessage {
    private Integer sessionId;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }
}
