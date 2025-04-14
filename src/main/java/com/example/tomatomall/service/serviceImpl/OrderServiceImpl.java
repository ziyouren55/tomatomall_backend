package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.OrderStatus;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.CartsOrdersRelationRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.shopping.PaymentResponseVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartsOrdersRelationRepository cartsOrdersRelationRepository;

    @Override
    public PaymentResponseVO initiatePayment(Integer orderId){
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

            if(OrderStatus.SUCCESS.name().equals(order.get().getStatus()))
                return;

            order.get().setStatus(OrderStatus.SUCCESS.name());
            order.get().setTotalAmount(new BigDecimal(amount));
            orderRepository.save(order.get());
        }
    }

    @Override
    public void reduceStockpile(Integer orderId)
    {
        List<Integer> cartItemIdList = cartsOrdersRelationRepository.findCartItemIdsByOrderId(orderId);
        // 遍历 cartItemId 列表，查询对应的 Cart 条目
        for (Integer cartItemId : cartItemIdList) {
            Optional<Cart> cartOptional = cartRepository.findById(cartItemId);
            if (!cartOptional.isPresent()) {
                throw TomatoMallException.cartItemNotFind();
            }
            Cart cart = cartOptional.get();
            Integer productId = cart.getProductId();
            Integer quantity = cart.getQuantity();

            // 查询对应的 Stockpile 条目
            Optional<Stockpile> stockpileOptional = stockpileRepository.findByProductId(productId);
            if (!stockpileOptional.isPresent())
            {
                throw TomatoMallException.stockpileNotFind();
            }
            Stockpile stockpile = stockpileOptional.get();

            // 减少库存
            stockpile.setAmount(stockpile.getAmount() - quantity);
            stockpileRepository.save(stockpile);
        }
    }

}
