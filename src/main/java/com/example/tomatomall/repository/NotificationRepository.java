package com.example.tomatomall.repository;

import com.example.tomatomall.po.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTargetRoleAndStatus(String targetRole, String status);
    List<Notification> findByTargetUserIdAndReadFlagFalse(Integer userId);
}



