package com.example.tomatomall.vo.shopping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponseVO
{
    String paymentForm;
    String orderId;
    BigDecimal totalAmount;
    String paymentMethod;
    String tradeNO;
}
