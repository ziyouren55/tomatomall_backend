package com.example.tomatomall.service.serviceImpl;

import com.alipay.api.AlipayApiException;
import com.example.tomatomall.enums.OrderStatus;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.ForumService;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.AlipayUtil;
import com.example.tomatomall.vo.shopping.OrderVO;
import com.example.tomatomall.vo.shopping.OrderItemVO;
import com.example.tomatomall.vo.shopping.PaymentResponseVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.tomatomall.po.PaymentRecord;
import com.example.tomatomall.repository.PaymentRecordRepository;
import com.example.tomatomall.kafka.KafkaProducerService;
import com.example.tomatomall.kafka.NotificationMessage;
import java.util.HashMap;
import java.util.Map;

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
            if (OrderStatus.PENDING.getCode().equals(o1.getStatus())
                    && !OrderStatus.PENDING.getCode().equals(o2.getStatus())) {
                return -1;
            } else if (!OrderStatus.PENDING.getCode().equals(o1.getStatus())
                    && OrderStatus.PENDING.getCode().equals(o2.getStatus())) {
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
    ForumService forumService;

    @Autowired
    AlipayUtil alipayUtil;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    PaymentRecordRepository paymentRecordRepository;

    @Autowired
    KafkaProducerService kafkaProducerService;
    
    @Autowired
    StoreRepository storeRepository;

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

        // 获取订单明细（快照）
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
        List<OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItem oi : orderItems) {
            OrderItemVO vo = new OrderItemVO();
            BeanUtils.copyProperties(oi, vo);
            // populate store/merchant names on VO (do not rely on entity storing names)
            if (oi.getStoreId() != null) {
                storeRepository.findById(oi.getStoreId()).ifPresent(store -> {
                    vo.setStoreName(store.getName());
                    if (store.getMerchantId() != null) {
                        accountRepository.findById(store.getMerchantId()).ifPresent(acc -> vo.setMerchantName(acc.getName()));
                    }
                });
            } else if (oi.getMerchantId() != null) {
                accountRepository.findById(oi.getMerchantId()).ifPresent(acc -> vo.setMerchantName(acc.getName()));
            }
            itemVOs.add(vo);
                }
        orderVO.setOrderItems(itemVOs);
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

        // 获取订单明细（快照）
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderVO.getOrderId());
        List<OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItem oi : orderItems) {
            OrderItemVO vo = new OrderItemVO();
            BeanUtils.copyProperties(oi, vo);
            // populate store/merchant names on VO
            if (oi.getStoreId() != null) {
                storeRepository.findById(oi.getStoreId()).ifPresent(store -> {
                    vo.setStoreName(store.getName());
                    if (store.getMerchantId() != null) {
                        accountRepository.findById(store.getMerchantId()).ifPresent(acc -> vo.setMerchantName(acc.getName()));
                    }
                });
            } else if (oi.getMerchantId() != null) {
                accountRepository.findById(oi.getMerchantId()).ifPresent(acc -> vo.setMerchantName(acc.getName()));
            }
            itemVOs.add(vo);
                }
        orderVO.setOrderItems(itemVOs);
        return orderVO;
    }

    @Override
    public OrderVO getOrderForMerchant(Integer orderId, Integer merchantId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw TomatoMallException.orderNotFound();
        }
        Order order = orderOpt.get();

        // 查询只属于该商家的订单明细（使用去规范化的 merchant_id）
        List<OrderItem> myItems = orderItemRepository.findByOrderIdAndMerchantId(orderId, merchantId);
        if (myItems == null || myItems.isEmpty()) {
            // 兼容：若没有 merchant_id 填充历史记录，则回退到通过 product->store 判断
            List<OrderItem> allItems = orderItemRepository.findByOrderId(orderId);
            for (OrderItem oi : allItems) {
                if (oi.getStoreId() != null) {
                    storeRepository.findById(oi.getStoreId()).ifPresent(store -> {
                        if (store.getMerchantId() != null && store.getMerchantId().equals(merchantId)) {
                            myItems.add(oi);
                        }
                    });
                } else {
                    // last resort: check product->store
                    productRepository.findById(oi.getProductId()).ifPresent(product -> {
                        Integer sId = product.getStoreId();
                        if (sId != null) {
                            storeRepository.findById(sId).ifPresent(store -> {
                                if (store.getMerchantId() != null && store.getMerchantId().equals(merchantId)) {
                                    myItems.add(oi);
                                }
                            });
                        }
                    });
                }
            }
        }

        if (myItems.isEmpty()) {
            throw TomatoMallException.permissionDenied();
        }

        // 构造返回 VO，只包含该商家的明细
        OrderVO vo = order.toVO();
        // 填写买家基础信息（可按需裁剪）
        Optional<Account> account = accountRepository.findById(order.getUserId());
        account.ifPresent(a -> {
            vo.setName(a.getName());
            vo.setPhone(a.getTelephone());
            vo.setAddress(a.getUsername());
        });

        List<OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItem oi : myItems) {
            OrderItemVO ivo = new OrderItemVO();
            BeanUtils.copyProperties(oi, ivo);
            itemVOs.add(ivo);
        }
        vo.setOrderItems(itemVOs);
        return vo;
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
        System.out.println("收到回调通知");
        try {
            // 1. 解析参数并验证签名
            Map<String, String> params = alipayUtil.parseAlipayParams(request);
            boolean isValid = alipayUtil.verifySignature(params);
            if (!isValid) {
                throw new TomatoMallException("支付宝回调签名验证失败");
            }

            // 2. 处理支付成功逻辑
            if ("TRADE_SUCCESS".equals(params.get("trade_status"))) {
                String orderIdStr = params.get("out_trade_no");
                String alipayTradeNo = params.get("trade_no");
                String amount = params.get("total_amount");

                // 先做支付流水幂等插入（以第三方交易号为幂等键）
                if (alipayTradeNo != null && paymentRecordRepository.findByTradeNo(alipayTradeNo).isPresent()) {
                    // 已处理该第三方流水，幂等返回
                    return;
                }

                // 保存 payment record
                try {
                    PaymentRecord pr = new PaymentRecord();
                    pr.setTradeNo(alipayTradeNo);
                    pr.setOrderId(Integer.valueOf(orderIdStr));
                    pr.setAmount(new BigDecimal(amount));
                    pr.setRawNotify(params.toString());
                    pr.setStatus("SUCCESS");
                    paymentRecordRepository.save(pr);
                    System.out.println("[DEBUG] paymentRecord saved: tradeNo=" + alipayTradeNo + " orderId=" + orderIdStr + " amount=" + amount);
                } catch (Exception ex) {
                    // 若写入 payment record 失败则记录并继续（不要阻塞回调）
                    System.out.println("payment record save failed: " + ex.getMessage());
                }

                // 幂等性检查：读取订单并判断状态
                Optional<Order> orderOpt = orderRepository.findById(Integer.valueOf(orderIdStr));
                if (!orderOpt.isPresent()) {
                    throw TomatoMallException.orderNotFound();
                }
                Order order = orderOpt.get();
                if (OrderStatus.SUCCESS.getCode().equals(order.getStatus())) {
                    return;
                }

                // 更新订单状态
                System.out.println("[DEBUG] updating order status to SUCCESS for orderId=" + order.getOrderId());
                order.setStatus(OrderStatus.SUCCESS.getCode());
                System.out.println("amount: " + amount);
                order.setTotalAmount(new BigDecimal(amount));
                orderRepository.save(order);
                System.out.println("[DEBUG] order updated to SUCCESS: orderId=" + order.getOrderId() + " amount=" + order.getTotalAmount());

                // 更新销量并检查是否需要创建论坛
                updateProductSalesAndCheckForum(order.getOrderId());

                // 扣减库存（需加锁）
                reduceStockpile(order.getOrderId());

                // 生产 Kafka 事件，通知下游（异步消费）
                try {
                    // 根据订单明细确定对应的商家（通过商品->store->merchantId）
                    List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
                    Map<Integer, Map<String, Object>> merchantPayloads = new HashMap<>();
                    for (OrderItem oi : orderItems) {
                        Integer productId = oi.getProductId();
                        productRepository.findById(productId).ifPresent(product -> {
                            Integer storeId = product.getStoreId();
                            if (storeId != null) {
                                storeRepository.findById(storeId).ifPresent(store -> {
                                    Integer merchantId = store.getMerchantId();
                                    if (merchantId != null) {
                                        // accumulate payload per merchant (simple: last one wins)
                                        Map<String, Object> payload = merchantPayloads.getOrDefault(merchantId, new HashMap<>());
                                        payload.put("orderId", order.getOrderId());
                                        payload.put("amount", order.getTotalAmount() != null ? order.getTotalAmount().toString() : amount);
                                        payload.put("storeId", store.getId());
                                        payload.put("merchantId", merchantId);
                                        merchantPayloads.put(merchantId, payload);
                                    }
                                });
                            }
                        });
                    }

                    // 对每个商家发送一条通知消息（targetUserId = merchantId）
                    for (Map.Entry<Integer, Map<String, Object>> e : merchantPayloads.entrySet()) {
                        Integer merchantId = e.getKey();
                        Map<String, Object> payload = e.getValue();
                        NotificationMessage nm = new NotificationMessage();
                        nm.setType("ORDER_PAID");
                        nm.setOrderId(order.getOrderId());
                        nm.setTargetRole("MERCHANT");
                        nm.setTargetUserId(merchantId);
                        nm.setPayload(payload);
                        System.out.println("[DEBUG] about to send ORDER_PAID kafka event for orderId=" + order.getOrderId() + " merchant=" + merchantId);
                        kafkaProducerService.sendOrderEvent(nm);
                    }
                    System.out.println("[DEBUG] kafka send invoked for orderId=" + order.getOrderId());
                } catch (Exception ex) {
                    System.out.println("kafka send failed: " + ex.getMessage());
                }
            }
        } catch (AlipayApiException e) {
            throw new TomatoMallException("支付宝回调处理异常");
        }
    }

    @Override
    public void reduceStockpile(Integer orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        // 遍历订单明细，扣减库存
        for (OrderItem item : orderItems) {
            Integer productId = item.getProductId();
            Integer quantity = item.getQuantity();

            // 查询对应的 Stockpile 条目
            Optional<Stockpile> stockpileOptional = stockpileRepository.findByProduct_Id(productId);
            if (!stockpileOptional.isPresent()) {
                // 如果库存不存在，这是数据异常，但为了不阻塞订单处理，我们记录错误并继续
                // 在实际生产环境中，应该记录日志并告警
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
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : orderItems) {
            Integer productId = item.getProductId();
            Integer quantity = item.getQuantity();
                // 更新销量并检查是否需要创建论坛
                forumService.incrementSalesAndCheckForum(productId, quantity);
        }
    }

}
