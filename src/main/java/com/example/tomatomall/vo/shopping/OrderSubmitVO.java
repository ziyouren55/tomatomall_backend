package com.example.tomatomall.vo.shopping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderSubmitVO {
    private String orderId;
    private String username;
    private String totalAmount;
    private String paymentMethod;
    private String createTime;
    private String status;
}
