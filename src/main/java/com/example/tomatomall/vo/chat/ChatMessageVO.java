package com.example.tomatomall.vo.chat;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ChatMessageVO {
    private Integer id;
    private Integer sessionId;
    private Integer senderId;
    private String senderRole;
    private String senderName;
    private String senderAvatar;
    private String content;
    private String messageType;
    private String status;
    private Timestamp createdAt;
}
