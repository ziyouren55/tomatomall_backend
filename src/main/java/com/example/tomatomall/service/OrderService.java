package com.example.tomatomall.service;

import com.example.tomatomall.vo.shopping.OrderVO;
import com.example.tomatomall.vo.shopping.PaymentResponseVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface OrderService {
    List<OrderVO> getAllOrders();

    PaymentResponseVO initiatePayment(Integer orderId);

    void handleAlipayNotify(HttpServletRequest request);

    void reduceStockpile(Integer orderId);
}
