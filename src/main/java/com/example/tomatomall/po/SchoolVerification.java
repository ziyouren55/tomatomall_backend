package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "school_verification")
public class SchoolVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    // 存储学号的哈希值（不存明文）
    @Column(name = "student_id_hash", nullable = false)
    private String studentIdHash;

    @Column(name = "certificate_url")
    private String certificateUrl;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'UNVERIFIED'")
    private String status = "UNVERIFIED";

    @Column(name = "submitted_at")
    private Date submittedAt;

    @Column(name = "verified_at")
    private Date verifiedAt;

    @Column(name = "rejected_reason")
    private String rejectedReason;

}
