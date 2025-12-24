package com.example.tomatomall.service;

import com.example.tomatomall.po.Shipment;
import com.example.tomatomall.vo.shopping.OrderVO;
import com.example.tomatomall.vo.shopping.PaymentResponseVO;
import com.example.tomatomall.vo.shopping.ShipRequestVO;

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
    OrderVO getOrderForMerchant(Integer orderId, Integer merchantId);

    /**
     * 商家操作：标记订单为已发货（创建 shipment 记录并更新订单状态）
     * @param orderId 订单ID
     * @param merchantId 商家ID
     * @param dto 发货信息（承运商与运单号）
     * @return 已创建的 Shipment 记录
     */
    Shipment shipOrderForMerchant(Integer orderId, Integer merchantId, ShipRequestVO dto);

    /**
     * 获取当前商家需要处理的、待发货的订单列表（状态为 PAID 或 SUCCESS）
     * @param merchantId 商家ID（从上下文获取）
     */
    List<OrderVO> getPendingOrdersForMerchant(Integer merchantId);
    
    /**
     * 获取当前商家已处理的、已发货或已完成的订单列表
     * @param merchantId 商家ID
     */
    List<OrderVO> getProcessedOrdersForMerchant(Integer merchantId);

    /**
     * 用户确认收货：将订单状态从 DELIVERED -> COMPLETED，并通知商家
     * @param orderId 订单ID
     * @param userId 当前用户ID（买家）
     */
    void confirmReceipt(Integer orderId, Integer userId);

    /**
     * 用户取消订单：只允许取消PENDING状态的订单
     * @param orderId 订单ID
     * @param userId 当前用户ID（买家）
     */
    void cancelOrder(Integer orderId, Integer userId);
}
