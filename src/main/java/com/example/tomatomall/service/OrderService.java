package com.example.tomatomall.service;

import com.example.tomatomall.vo.shopping.PaymentResponseVO;

import javax.servlet.http.HttpServletRequest;

public interface OrderService {
    PaymentResponseVO initiatePayment(Integer orderId);

    void handleAlipayNotify(HttpServletRequest request);

    void reduceStockpile(Integer orderId);
}
