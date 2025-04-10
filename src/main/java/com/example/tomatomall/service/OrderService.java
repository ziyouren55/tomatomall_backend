package com.example.tomatomall.service;

import com.example.tomatomall.vo.shopping.PaymentResponseVO;

public interface OrderService {
    PaymentResponseVO payInit(Integer orderId);

    void updateOrderStatus(String orderId, String alipayTradeNo, String amount);
}
