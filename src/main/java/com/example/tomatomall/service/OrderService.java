package com.example.tomatomall.service;

import com.example.tomatomall.vo.shopping.OrderVO;
import com.example.tomatomall.vo.shopping.PaymentResponseVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface OrderService {
    List<OrderVO> getAllOrders();

    /**
     * 根据订单ID和用户ID获取订单详情
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 订单详情
     */
    OrderVO getOrderById(Integer orderId, Integer userId);

    /**
     * 根据用户ID获取该用户的所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderVO> getOrdersByUserId(Integer userId);

    PaymentResponseVO initiatePayment(Integer orderId);

    void handleAlipayNotify(HttpServletRequest request);

    void reduceStockpile(Integer orderId);
}
