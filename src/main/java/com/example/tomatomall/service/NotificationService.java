package com.example.tomatomall.service;

import com.example.tomatomall.kafka.NotificationMessage;

public interface NotificationService {
    /**
     * 处理来自 Kafka 的通知消息，持久化并执行必要的业务操作（如推送）
     */
    void processNotificationMessage(NotificationMessage msg);
}



