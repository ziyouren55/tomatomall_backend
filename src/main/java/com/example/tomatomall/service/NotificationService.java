package com.example.tomatomall.service;

import com.example.tomatomall.kafka.NotificationMessage;
import com.example.tomatomall.po.Notification;
import com.example.tomatomall.vo.PageResultVO;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    /**
     * 处理来自 Kafka 的通知消息，持久化并执行必要的业务操作（如推送）
     */
    void processNotificationMessage(NotificationMessage msg);

    // 基于用户 ID 的操作（Controller 从 UserContext 获取 userId 后调用）
    long countUnreadByUserId(Integer userId);

    PageResultVO<Notification> getNotificationsByUserId(Integer userId, Integer page, Integer pageSize);

    Optional<Notification> getNotificationDetailForUser(Long id, Integer userId);

    int markReadByUserId(Integer userId, List<Long> ids);

    int markAllReadByUserId(Integer userId);

    int deleteByUserId(Integer userId, List<Long> ids);

    int deleteAllByUserId(Integer userId);

    // 系统/管理员操作
    Notification createNotification(Notification notification);
}



