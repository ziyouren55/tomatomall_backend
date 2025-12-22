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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.example.tomatomall.vo.PageResultVO;
import java.util.List;
import java.util.Optional;

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
            // persist notification and use saved entity as WS payload (includes id/type/status/timestamps)
            Notification saved = notificationRepository.save(n);
            System.out.println("[DEBUG] notification persisted (id=" + saved.getId() + ", type=" + saved.getType() + ")");

            // send via websocket the saved notification JSON so frontend receives DB-standard fields
            try {
                String notificationStr = objectMapper.writeValueAsString(saved);
                System.out.println("[DEBUG] ws push targetRole=" + msg.getTargetRole() + " targetUserId=" + msg.getTargetUserId()
                    + " payload=" + payloadStr);
                if (msg.getTargetUserId() != null) {
                    // user-specific destination
                    simpMessagingTemplate.convertAndSendToUser(
                        String.valueOf(msg.getTargetUserId()), "/queue/notifications", notificationStr);
                    System.out.println("[DEBUG] ws push succeeded for user=" + msg.getTargetUserId());
                } else if ("MERCHANT".equalsIgnoreCase(msg.getTargetRole())) {
                    simpMessagingTemplate.convertAndSend("/topic/merchant/notifications", notificationStr);
                    System.out.println("[DEBUG] ws push succeeded for merchant topic");
                } else {
                    simpMessagingTemplate.convertAndSend("/topic/notifications", notificationStr);
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

    @Override
    public long countUnreadByUserId(Integer userId) {
        if (userId == null) return 0L;
        return notificationRepository.countByTargetUserIdAndReadFlagFalse(userId);
    }

    @Override
    public PageResultVO<Notification> getNotificationsByUserId(Integer userId, Integer page, Integer pageSize) {
        if (userId == null) return new PageResultVO<>(List.of(), 0L, page == null ? 0 : page, pageSize == null ? 20 : pageSize);
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize <= 0) pageSize = 20;
        Page<Notification> p = notificationRepository.findByTargetUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, pageSize));
        return new PageResultVO<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    @Override
    public Optional<Notification> getNotificationDetailForUser(Long id, Integer userId) {
        if (userId == null) return Optional.empty();
        return notificationRepository.findByIdAndTargetUserId(id, userId);
    }

    @Override
    @Transactional
    public int markReadByUserId(Integer userId, List<Long> ids) {
        if (userId == null || ids == null || ids.isEmpty()) return 0;
        return notificationRepository.markReadByUserAndIds(userId, ids);
    }

    @Override
    @Transactional
    public int markAllReadByUserId(Integer userId) {
        if (userId == null) return 0;
        return notificationRepository.markAllReadByUser(userId);
    }

    @Override
    @Transactional
    public int deleteByUserId(Integer userId, List<Long> ids) {
        if (userId == null || ids == null || ids.isEmpty()) return 0;
        return notificationRepository.deleteByUserAndIds(userId, ids);
    }

    @Override
    @Transactional
    public int deleteAllByUserId(Integer userId) {
        if (userId == null) return 0;
        return notificationRepository.deleteAllByUser(userId);
    }

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
}


