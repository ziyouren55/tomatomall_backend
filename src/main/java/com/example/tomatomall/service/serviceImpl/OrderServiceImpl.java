package com.example.tomatomall.service.serviceImpl;

import com.alipay.api.AlipayApiException;
import com.example.tomatomall.enums.OrderStatus;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.CartsOrdersRelation;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.CartsOrdersRelationRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.ForumService;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.AlipayUtil;
import com.example.tomatomall.vo.shopping.PaymentResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartsOrdersRelationRepository cartsOrdersRelationRepository;

    @Autowired
    ForumService forumService;

    @Autowired
    AlipayUtil alipayUtil;

    @Override
    public PaymentResponseVO initiatePayment(Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw TomatoMallException.orderNotFound();
        }

        Order order = orderOpt.get();
        try {
            // 调用AlipayUtil生成支付表单
            String paymentForm = alipayUtil.generatePayForm(
                String.valueOf(orderId),
                order.getTotalAmount().toString(),
                "TomatoMall订单支付"
            );

            PaymentResponseVO response = new PaymentResponseVO();
            response.setOrderId(String.valueOf(orderId));
            response.setPaymentMethod(order.getPaymentMethod());
            response.setTotalAmount(order.getTotalAmount());
            response.setPaymentForm(paymentForm); // 替换硬编码
            return response;
        } catch (AlipayApiException e) {
            throw new TomatoMallException("支付宝支付表单生成失败");
        }
    }

    @Override
    public void handleAlipayNotify(HttpServletRequest request) {
        try {
            // 1. 解析参数并验证签名
            Map<String, String> params = alipayUtil.parseAlipayParams(request);
            boolean isValid = alipayUtil.verifySignature(params);
            if (!isValid) {
                throw new TomatoMallException("支付宝回调签名验证失败");
            }

            // 2. 处理支付成功逻辑
            if ("TRADE_SUCCESS".equals(params.get("trade_status"))) {
                String orderId = params.get("out_trade_no");
                String alipayTradeNo = params.get("trade_no");
                String amount = params.get("total_amount");

                // 幂等性检查
                Optional<Order> orderOpt = orderRepository.findById(Integer.valueOf(orderId));
                if (!orderOpt.isPresent()) {
                    throw TomatoMallException.orderNotFound();
                }
                Order order = orderOpt.get();
                if (OrderStatus.SUCCESS.name().equals(order.getStatus())) {
                    return;
                }

                // 更新订单状态
                order.setStatus(OrderStatus.SUCCESS.name());
                order.setTotalAmount(new BigDecimal(amount));
                orderRepository.save(order);

                // 更新销量并检查是否需要创建论坛
                updateProductSalesAndCheckForum(order.getOrderId());

                // 扣减库存（需加锁）
                reduceStockpile(order.getOrderId());
            }
        } catch (AlipayApiException e) {
            throw new TomatoMallException("支付宝回调处理异常");
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

    /**
 * 更新订单中商品的销量并检查是否需要创建论坛
 */
private void updateProductSalesAndCheckForum(Integer orderId) {
    // 获取订单中的所有商品和数量
    List<CartsOrdersRelation> relations = cartsOrdersRelationRepository.findByOrderId(orderId);

    for (CartsOrdersRelation relation : relations) {
        Optional<Cart> cartOpt = cartRepository.findByCartItemId(relation.getCartItemId());
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            Integer productId = cart.getProductId();
            Integer quantity = cart.getQuantity();

            // 更新销量并检查是否需要创建论坛
            forumService.incrementSalesAndCheckForum(productId, quantity);
        }
    }
}

}
