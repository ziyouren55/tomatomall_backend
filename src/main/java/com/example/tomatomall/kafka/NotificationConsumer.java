package com.example.tomatomall.kafka;

import com.example.tomatomall.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${notification.topic:order-events}", groupId = "notification-service")
    public void onMessage(String payload) {
        try {
            System.out.println("[DEBUG] NotificationConsumer received payload: " + payload);
            NotificationMessage msg = objectMapper.readValue(payload, NotificationMessage.class);
            notificationService.processNotificationMessage(msg);
        } catch (Exception e) {
            System.out.println("notificationService processing failed: " + e.getMessage());
        }
    }
}



