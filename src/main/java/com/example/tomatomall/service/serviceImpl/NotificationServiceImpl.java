package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.kafka.NotificationMessage;
import com.example.tomatomall.po.Notification;
import com.example.tomatomall.repository.NotificationRepository;
import com.example.tomatomall.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   ObjectMapper objectMapper,
                                   SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    @Transactional
    public void processNotificationMessage(NotificationMessage msg) {
        try {
            System.out.println("[DEBUG] processNotificationMessage: type=" + msg.getType() + " targetRole=" + msg.getTargetRole() + " targetUserId=" + msg.getTargetUserId());
            Notification n = new Notification();
            n.setType(msg.getType());
            n.setTargetRole(msg.getTargetRole());
            n.setTargetUserId(msg.getTargetUserId());
            String payloadStr = objectMapper.writeValueAsString(msg.getPayload() != null ? msg.getPayload() : new HashMap<String, Object>());
            n.setPayload(payloadStr);
            n.setStatus("SENT");
            System.out.println("[DEBUG] persisting notification payload: " + payloadStr);
            notificationRepository.save(n);
            System.out.println("[DEBUG] notification persisted (approx) for type=" + msg.getType());

            // send via websocket to relevant destination
            try {
                String payloadJson = payloadStr;
                System.out.println("[DEBUG] ws push targetRole=" + msg.getTargetRole() + " targetUserId=" + msg.getTargetUserId() + " payload=" + payloadJson);
                if (msg.getTargetUserId() != null) {
                    // user-specific destination
                    simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(msg.getTargetUserId()), "/queue/notifications", payloadJson);
                    System.out.println("[DEBUG] ws push succeeded for user=" + msg.getTargetUserId());
                } else if ("MERCHANT".equalsIgnoreCase(msg.getTargetRole())) {
                    simpMessagingTemplate.convertAndSend("/topic/merchant/notifications", payloadJson);
                    System.out.println("[DEBUG] ws push succeeded for merchant topic");
                } else {
                    simpMessagingTemplate.convertAndSend("/topic/notifications", payloadJson);
                    System.out.println("[DEBUG] ws push succeeded for general topic");
                }
            } catch (Exception ex) {
                System.out.println("ws push failed: " + ex.getMessage());
            }
        } catch (Exception e) {
            // 在异常情况下，记录一条 FAILED 的 notification 以便后续人工处理
            try {
                Notification n = new Notification();
                n.setType(msg.getType());
                n.setTargetRole(msg.getTargetRole());
                n.setPayload("{}");
                n.setStatus("FAILED");
                notificationRepository.save(n);
            } catch (Exception ex) {
                System.out.println("failed to persist failed-notification: " + ex.getMessage());
            }
        }
    }
}


