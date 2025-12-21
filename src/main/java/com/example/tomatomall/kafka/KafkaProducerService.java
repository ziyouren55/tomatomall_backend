package com.example.tomatomall.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    @Value("${notification.topic:order-events}")
    private String topic;

    public KafkaProducerService(KafkaTemplate<String, NotificationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String key, NotificationMessage msg) {
        kafkaTemplate.send(topic, key, msg);
    }

    public void sendOrderEvent(NotificationMessage msg) {
        System.out.println("[DEBUG] KafkaProducerService.sendOrderEvent topic=" + topic + " key=" + msg.getOrderId() + " payload=" + msg.getPayload());
        kafkaTemplate.send(topic, String.valueOf(msg.getOrderId()), msg);
        System.out.println("[DEBUG] KafkaProducerService.sendOrderEvent invoked for orderId=" + msg.getOrderId());
    }
}



