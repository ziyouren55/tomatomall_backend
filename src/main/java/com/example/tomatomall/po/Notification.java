package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_user_id")
    private Integer targetUserId;

    @Column(name = "target_role", columnDefinition = "VARCHAR(64)")
    private String targetRole;

    @Column(name = "type", columnDefinition = "VARCHAR(64)")
    private String type;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    private String status = "PENDING";

    @Column(name = "retry_count", columnDefinition = "INT")
    private Integer retryCount = 0;

    @Column(name = "read_flag", columnDefinition = "BOOLEAN")
    private Boolean readFlag = false;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}



