package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_records", uniqueConstraints = @UniqueConstraint(columnNames = {"trade_no"}))
public class PaymentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_no", nullable = false, columnDefinition = "VARCHAR(128) COMMENT '第三方交易号'")
    private String tradeNo;

    @Column(name = "order_id", nullable = false, columnDefinition = "INT COMMENT '系统订单ID'")
    private Integer orderId;

    @Column(name = "amount", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal amount;

    @Column(name = "raw_notify", columnDefinition = "TEXT")
    private String rawNotify;

    @Column(name = "status", columnDefinition = "VARCHAR(32)")
    private String status;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }
}



