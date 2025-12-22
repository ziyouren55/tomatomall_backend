package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Notification;
import com.example.tomatomall.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class NotificationRetryJob {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 扫描 status = 'PENDING' 的通知并尝试发送（每 30 秒）
     */
    @Scheduled(fixedDelayString = "${notifications.retry.ms:30000}")
    @Transactional
    public void resendPendingNotifications() {
        try {
            List<Notification> pendings = notificationRepository.findByStatus("PENDING");
            for (Notification n : pendings) {
                try {
                    String payload = n.getPayload() == null ? "{}" : n.getPayload();
                    if (n.getTargetUserId() != null) {
                        simpMessagingTemplate.convertAndSendToUser(
                                String.valueOf(n.getTargetUserId()), "/queue/notifications", payload);
                    } else if (n.getTargetRole() != null && "MERCHANT".equalsIgnoreCase(n.getTargetRole())) {
                        simpMessagingTemplate.convertAndSend("/topic/merchant/notifications", payload);
                    } else {
                        simpMessagingTemplate.convertAndSend("/topic/notifications", payload);
                    }
                    n.setStatus("SENT");
                    notificationRepository.save(n);
                } catch (Exception ex) {
                    // 下一轮重试
                    System.out.println("NotificationRetryJob: resend failed for id=" + n.getId() + " error:" + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("NotificationRetryJob: scan failed: " + e.getMessage());
        }
    }
}


