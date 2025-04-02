package com.example.tomatomall.po;

import com.example.tomatomall.enums.OrderStatus;
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
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId", columnDefinition = "INT COMMENT '订单ID'")
    private Integer orderId;

    @Column(name = "userId", nullable = false, columnDefinition = "INT COMMENT '用户ID'")
    private Integer userId;

    @Column(name = "total_amount", nullable = false, columnDefinition = "DECIMAL(10,2) COMMENT '订单总金额'")
    private BigDecimal totalAmount;

    @Column(name = "payment_method", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '支付方式'")
    private String paymentMethod;

    @Column(name = "status", nullable = false,
        columnDefinition = "VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单支付状态（PENDING, SUCCESS, FAILED, TIMEOUT）'")
    private String status = OrderStatus.PENDING.name();

    @Column(name = "create_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间'")
    private Timestamp createTime;
}
