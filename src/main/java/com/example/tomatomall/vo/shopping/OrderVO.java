package com.example.tomatomall.vo.shopping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class OrderVO
{
    private Integer orderId;
    private Integer userId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private Timestamp createTime;
    private String status;

}
