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
@Table(name = "order_shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "merchant_id", nullable = false)
    private Integer merchantId;

    @Column(name = "carrier", columnDefinition = "VARCHAR(100)")
    private String carrier;

    @Column(name = "tracking_no", columnDefinition = "VARCHAR(100)")
    private String trackingNo;

    @Column(name = "shipped_at", columnDefinition = "TIMESTAMP")
    private Timestamp shippedAt;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = new Timestamp(System.currentTimeMillis());
        if (shippedAt == null) shippedAt = createdAt;
    }
}


