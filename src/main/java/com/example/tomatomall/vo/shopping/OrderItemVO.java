package com.example.tomatomall.vo.shopping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemVO {
    private Integer orderId;
    private Integer productId;
    private String title;
    private String cover;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}

