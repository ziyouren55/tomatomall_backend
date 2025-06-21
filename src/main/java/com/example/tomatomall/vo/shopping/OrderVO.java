package com.example.tomatomall.vo.shopping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderVO {
    private Integer orderId;
    private Integer userId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private Timestamp createTime;
    private String name;
    private String phone;
    private String address;
    private String status;

    // 添加订单关联的购物车项信息
    private List<CartItemVO> cartItems;
}
