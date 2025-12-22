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

    /**
     * 商家视图：获取属于该商家的订单明细（只返回该商家相关的 order items 以及必要的订单头信息）
     * @param orderId 订单ID
     * @param merchantId 商家ID（当前登录用户ID）
     */
    com.example.tomatomall.vo.shopping.OrderVO getOrderForMerchant(Integer orderId, Integer merchantId);
}
