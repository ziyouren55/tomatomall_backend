package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Order;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.shopping.PaymentResponseVO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.Option;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Override
    public PaymentResponseVO payInit(Integer orderId){
        PaymentResponseVO paymentResponseVO = new PaymentResponseVO();
        paymentResponseVO.setOrderId(String.valueOf(orderId));

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()){
            paymentResponseVO.setPaymentMethod(order.get().getPaymentMethod());
            paymentResponseVO.setTotalAmount(order.get().getTotalAmount());
            paymentResponseVO.setPaymentForm("暂未实现");
        }
        return paymentResponseVO;
    }

    @Override
    public void updateOrderStatus(String orderId, String alipayTradeNo, String amount) {
        Optional<Order> order = orderRepository.findById(Integer.valueOf(orderId));
        if (order.isPresent()){
            order.get().setStatus("SUCCESS");
            orderRepository.save(order.get());
        }
    }

}
