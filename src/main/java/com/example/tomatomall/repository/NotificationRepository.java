package com.example.tomatomall.repository;

import com.example.tomatomall.po.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTargetRoleAndStatus(String targetRole, String status);
    List<Notification> findByTargetUserIdAndReadFlagFalse(Integer userId);

    Page<Notification> findByTargetUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    long countByTargetUserIdAndReadFlagFalse(Integer userId);

    Optional<Notification> findByIdAndTargetUserId(Long id, Integer userId);

    @Modifying
    @Query("UPDATE Notification n SET n.readFlag = true WHERE n.targetUserId = :userId AND n.id IN :ids")
    int markReadByUserAndIds(@Param("userId") Integer userId, @Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE Notification n SET n.readFlag = true WHERE n.targetUserId = :userId AND n.readFlag = false")
    int markAllReadByUser(@Param("userId") Integer userId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.targetUserId = :userId AND n.id IN :ids")
    int deleteByUserAndIds(@Param("userId") Integer userId, @Param("ids") List<Long> ids);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.targetUserId = :userId")
    int deleteAllByUser(@Param("userId") Integer userId);

    List<Notification> findByStatus(String status);
}



