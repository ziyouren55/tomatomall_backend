package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "points_records")
@Getter
@Setter
@NoArgsConstructor
public class PointsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "points_change", nullable = false)
    private Integer pointsChange;

    //todo 可修改为enums
    @Column(name = "record_type", nullable = false)
    private String recordType; // PURCHASE, FORUM_POST, REVIEW, EXCHANGE, SYSTEM

    @Column(name = "reference_id")
    private Integer referenceId; // 关联的订单ID、评论ID等

    @Column(name = "description")
    private String description;

    @Column(name = "create_time", nullable = false)
    private Date createTime;
}
