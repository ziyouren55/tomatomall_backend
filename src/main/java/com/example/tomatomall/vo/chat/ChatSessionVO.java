package com.example.tomatomall.vo.chat;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ChatSessionVO {
    private Integer id;
    private Integer customerId;
    private Integer merchantId;
    private Integer storeId;
    private String storeName;
    private String customerName;
    private String customerAvatar;
    private String merchantName;
    private String merchantAvatar;
    private String lastMessage;
    private Timestamp lastMessageTime;
    private Integer unreadCountCustomer;
    private Integer unreadCountMerchant;
    private String status;
}
