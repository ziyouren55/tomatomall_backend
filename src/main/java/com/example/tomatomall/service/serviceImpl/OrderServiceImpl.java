package com.example.tomatomall.service.serviceImpl;

import com.alipay.api.AlipayApiException;
import com.example.tomatomall.enums.OrderStatus;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.ForumService;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.AlipayUtil;
import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.OrderVO;
import com.example.tomatomall.vo.shopping.PaymentResponseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    
    /**
     * 订单排序比较器：PENDING状态的订单排在前面，相同状态按创建时间倒序排列
     * 使用静态内部类避免生成匿名内部类
     */
    private static class OrderComparator implements Comparator<OrderVO> {
        @Override
        public int compare(OrderVO o1, OrderVO o2) {
            // PENDING状态的订单排在前面
            if (OrderStatus.PENDING.name().equals(o1.getStatus())
                    && !OrderStatus.PENDING.name().equals(o2.getStatus())) {
                return -1;
            } else if (!OrderStatus.PENDING.name().equals(o1.getStatus())
                    && OrderStatus.PENDING.name().equals(o2.getStatus())) {
                return 1;
            } else {
                // 如果状态相同，按照创建时间倒序排列（最新的在前面）
                if (o1.getCreateTime() == null && o2.getCreateTime() == null) {
                    return 0;
                } else if (o1.getCreateTime() == null) {
                    return 1;
                } else if (o2.getCreateTime() == null) {
                    return -1;
                }
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        }
    }
    
    private static final Comparator<OrderVO> ORDER_COMPARATOR = new OrderComparator();
    
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    AccountRepository accountRepository;

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

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<OrderVO> getAllOrders() {
        List<OrderVO> ordersList = orderRepository.findAll().stream().map(Order::toVO).collect(Collectors.toList());

        // 填充每个订单的详细信息
        for (OrderVO order : ordersList) {
            enrichOrderVO(order);
        }

        // 按照订单状态排序，PENDING状态优先
        ordersList.sort(ORDER_COMPARATOR);

        return ordersList;
    }

    @Override
    public OrderVO getOrderById(Integer orderId, Integer userId) {
        // 查找订单
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw TomatoMallException.orderNotFound();
        }

        Order order = orderOpt.get();
        
        // 验证订单是否属于该用户
        if (!order.getUserId().equals(userId)) {
            throw new TomatoMallException("无权访问该订单");
        }

        // 转换为VO并填充详细信息
        OrderVO orderVO = enrichOrderVO(order);
        return orderVO;
    }

    @Override
    public List<OrderVO> getOrdersByUserId(Integer userId) {
        // 根据用户ID查询订单
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderVO> ordersList = orders.stream().map(Order::toVO).collect(Collectors.toList());

        // 填充每个订单的详细信息
        for (OrderVO order : ordersList) {
            enrichOrderVO(order);
        }

        // 按照订单状态排序，PENDING状态优先
        ordersList.sort(ORDER_COMPARATOR);

        return ordersList;
    }

    /**
     * 填充订单的详细信息（用户信息、购物车项等）
     * @param order 订单实体
     * @return 填充后的订单VO
     */
    private OrderVO enrichOrderVO(Order order) {
        OrderVO orderVO = order.toVO();

        // 获取用户信息
        Optional<Account> account = accountRepository.findById(order.getUserId());
        if (!account.isPresent()) {
            throw TomatoMallException.usernameNotFind();
        }
        orderVO.setName(account.get().getName());
        orderVO.setAddress(account.get().getUsername());
        orderVO.setPhone(account.get().getTelephone());

        // 获取订单关联的购物车项
        List<Integer> cartItemIds = cartsOrdersRelationRepository.findCartItemIdsByOrderId(order.getOrderId());
        List<CartItemVO> cartItems = new ArrayList<>();

        for (Integer cartItemId : cartItemIds) {
            Optional<Cart> cartOpt = cartRepository.findById(cartItemId);
            if (cartOpt.isPresent()) {
                Cart cart = cartOpt.get();

                // 转换为CartItemVO
                CartItemVO cartItemVO = new CartItemVO();
                cartItemVO.setCartItemId(String.valueOf(cart.getCartItemId()));
                cartItemVO.setQuantity(cart.getQuantity());
                if (cart.getState().equals("SHOW")) {
                    cart.setState("HIDDEN");
                    cartRepository.save(cart);
                }
                cartItemVO.setState(cart.getState());

                // 获取商品信息
                Optional<Product> productOpt = productRepository.findById(cart.getProductId());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    BeanUtils.copyProperties(product, cartItemVO);
                    cartItemVO.setProductId(String.valueOf(product.getId()));
                }

                cartItems.add(cartItemVO);
            }
        }

        orderVO.setCartItems(cartItems);
        return orderVO;
    }

    /**
     * 填充订单VO的详细信息（重载方法，接受OrderVO参数）
     * @param orderVO 订单VO
     * @return 填充后的订单VO
     */
    private OrderVO enrichOrderVO(OrderVO orderVO) {
        // 获取用户信息
        Optional<Account> account = accountRepository.findById(orderVO.getUserId());
        if (!account.isPresent()) {
            throw TomatoMallException.usernameNotFind();
        }
        orderVO.setName(account.get().getName());
        orderVO.setAddress(account.get().getUsername());
        orderVO.setPhone(account.get().getTelephone());

        // 获取订单关联的购物车项
        List<Integer> cartItemIds = cartsOrdersRelationRepository.findCartItemIdsByOrderId(orderVO.getOrderId());
        List<CartItemVO> cartItems = new ArrayList<>();

        for (Integer cartItemId : cartItemIds) {
            Optional<Cart> cartOpt = cartRepository.findById(cartItemId);
            if (cartOpt.isPresent()) {
                Cart cart = cartOpt.get();

                // 转换为CartItemVO
                CartItemVO cartItemVO = new CartItemVO();
                cartItemVO.setCartItemId(String.valueOf(cart.getCartItemId()));
                cartItemVO.setQuantity(cart.getQuantity());
                if (cart.getState().equals("SHOW")) {
                    cart.setState("HIDDEN");
                    cartRepository.save(cart);
                }
                cartItemVO.setState(cart.getState());

                // 获取商品信息
                Optional<Product> productOpt = productRepository.findById(cart.getProductId());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    BeanUtils.copyProperties(product, cartItemVO);
                    cartItemVO.setProductId(String.valueOf(product.getId()));
                }

                cartItems.add(cartItemVO);
            }
        }

        orderVO.setCartItems(cartItems);
        return orderVO;
    }

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
                    "TomatoMall订单支付");

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
    public void reduceStockpile(Integer orderId) {
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
            if (!stockpileOptional.isPresent()) {
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
