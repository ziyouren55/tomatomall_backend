package com.example.tomatomall.po;

import javax.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carts_orders_relation")
@Getter
@Setter
public class CartsOrdersRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cartItem_id", nullable = false)
    private Integer cartItemId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;
}

