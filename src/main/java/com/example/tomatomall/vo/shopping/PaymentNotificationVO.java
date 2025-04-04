package com.example.tomatomall.vo.shopping;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class PaymentNotificationVO
{
    Integer orderId;
    String paymentStatus;
    Timestamp paymentTime;

}
