package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",
            columnDefinition = "INT COMMENT '订单明细id'")
    private Integer id;

    @Column(name = "order_id", nullable = false,
            columnDefinition = "INT COMMENT '订单id，关联订单表'")
    private Integer orderId;

    @Column(name = "product_id", nullable = false,
            columnDefinition = "INT COMMENT '商品id'")
    private Integer productId;

    @Column(name = "title",
            columnDefinition = "VARCHAR(255) COMMENT '商品标题快照'")
    private String title;

    @Column(name = "cover",
            columnDefinition = "VARCHAR(500) COMMENT '商品封面快照'")
    private String cover;

    @Column(name = "price",
            columnDefinition = "DECIMAL(10,2) COMMENT '下单时单价'")
    private BigDecimal price;

    @Column(name = "quantity",
            columnDefinition = "INT COMMENT '购买数量'")
    private Integer quantity;

    @Column(name = "subtotal",
            columnDefinition = "DECIMAL(10,2) COMMENT '小计 = price * quantity'")
    private BigDecimal subtotal;
}

