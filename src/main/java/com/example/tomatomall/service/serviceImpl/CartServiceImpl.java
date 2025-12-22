package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.OrderStatus;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.shopping.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    OrderItemRepository orderItemRepository;
    
    @Autowired
    private StoreRepository storeRepository;

    @Override
    public CartItemVO addProductToCart(CartItemVO cartItemVO, Integer userId) {
        Integer productId = Integer.valueOf(cartItemVO.getProductId());

        Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent())
            throw TomatoMallException.productNotFind();

        // 检查库存（不在购物车阶段扣减库存）
        Optional<Stockpile> stockpileOpt = stockpileRepository.findByProduct_Id(productId);
        if (!stockpileOpt.isPresent()) {
            // 如果库存不存在，创建默认库存记录
            Stockpile newStockpile = new Stockpile();
            newStockpile.setProduct(product.get());
            newStockpile.setAmount(0);
            newStockpile.setFrozen(0);
            stockpileRepository.save(newStockpile);
            throw TomatoMallException.cartItemQuantityOutOfStock();
        }

        Stockpile stockpile = stockpileOpt.get();
        if (stockpile.getAmount() < cartItemVO.getQuantity()) {
            throw TomatoMallException.cartItemQuantityOutOfStock();
        }

        // 若已存在同商品的购物车项，则叠加数量；否则新建
        Optional<Cart> existingOpt = cartRepository.findByUserIdAndProductId(userId, productId);
        Cart savedCart;
        if (existingOpt.isPresent()) {
            Cart existing = existingOpt.get();
            int newQty = existing.getQuantity() + cartItemVO.getQuantity();
            if (stockpile.getAmount() < cartItemVO.getQuantity()) {
                throw TomatoMallException.cartItemQuantityOutOfStock();
            }
            existing.setQuantity(newQty);
            savedCart = cartRepository.save(existing);
        } else {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setProductId(Integer.valueOf(cartItemVO.getProductId()));
            newCart.setQuantity(cartItemVO.getQuantity());
            savedCart = cartRepository.save(newCart);
        }

        CartItemVO newCartItemVO = new CartItemVO();
        BeanUtils.copyProperties(product.get(), newCartItemVO);
        newCartItemVO.setProductId(String.valueOf(productId));
        newCartItemVO.setCartItemId(String.valueOf(savedCart.getCartItemId()));
        newCartItemVO.setQuantity(cartItemVO.getQuantity());

        return newCartItemVO;
    }

    @Override
    public String deleteProductFromCart(String cartItemId) {
        Optional<Cart> cart = cartRepository.findByCartItemId(Integer.valueOf(cartItemId));
        if (!cart.isPresent())
            throw TomatoMallException.cartItemNotFind();

        cartRepository.deleteById(Integer.valueOf(cartItemId));

        return "删除成功";
    }

    @Override
    public String updateCartItemQuantity(String cartItemId, UpdateQuantityVO updateQuantityVO, Integer userId) {
        Optional<Cart> cart = cartRepository.findByCartItemId(Integer.valueOf(cartItemId));
        if (!cart.isPresent())
            throw TomatoMallException.cartItemNotFind();

        // 检查库存（不在购物车阶段扣减库存）
        Integer productId = cart.get().getProductId();
        Optional<Stockpile> stockpileOpt = stockpileRepository.findByProduct_Id(productId);
        if (!stockpileOpt.isPresent()) {
            // 如果库存不存在，创建默认库存记录
            Optional<Product> product = productRepository.findById(productId);
            if (!product.isPresent()) {
                throw TomatoMallException.productNotFind();
            }
            Stockpile newStockpile = new Stockpile();
            newStockpile.setProduct(product.get());
            newStockpile.setAmount(0);
            newStockpile.setFrozen(0);
            stockpileRepository.save(newStockpile);
            throw TomatoMallException.cartItemQuantityOutOfStock();
        }

        Stockpile stockpile = stockpileOpt.get();
        if (stockpile.getAmount() < updateQuantityVO.getQuantity()) {
            throw TomatoMallException.cartItemQuantityOutOfStock();
        }

        cart.get().setQuantity(updateQuantityVO.getQuantity());
        cartRepository.save(cart.get());
        return "修改数量成功";
    }

    @Override
    public CartItemsVO getProductListFromCart(Integer userId) {
        // 仅查询当前用户的购物车
        List<Cart> cartList = cartRepository.findByUserId(userId);
        CartItemsVO cartItemsVO = new CartItemsVO();
        List<CartItemVO> cartItemVOS = new ArrayList<>();
        double totalAmount = 0.0;
        for (Cart cart : cartList) {
            CartItemVO cartItemVO = toCartItemVO(cart);
            cartItemVOS.add(cartItemVO);
            totalAmount = totalAmount + cartItemVO.getPrice();
        }

        cartItemsVO.setCartItems(cartItemVOS);
        cartItemsVO.setTotal(cartItemVOS.size());
        cartItemsVO.setTotalAmount(totalAmount);

        return cartItemsVO;
    }

    private CartItemVO toCartItemVO(Cart cart) {
        CartItemVO cartItemVO = new CartItemVO();

        Integer productId = cart.getProductId();
        Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent())
            throw TomatoMallException.productNotFind();

        BeanUtils.copyProperties(product.get(), cartItemVO);
        cartItemVO.setProductId(String.valueOf(productId));
        cartItemVO.setCartItemId(String.valueOf(cart.getCartItemId()));
        cartItemVO.setQuantity(cart.getQuantity());

        return cartItemVO;
    }

    public OrderSubmitVO submitOrder(OrderCheckoutVO orderCheckoutVO, Account account) {
        List<Cart> cartItems = new ArrayList<>();
        List<String> cartItemIds = orderCheckoutVO.getCartItemIds();
        String payment_method = orderCheckoutVO.getPaymentMethod();
        String username = account.getUsername();
        int userId = account.getId();

        double totalAmount = 0;
        int cartItemId_int = Integer.parseInt(cartItemIds.get(0));
        for (String cartItemId : cartItemIds) {
            cartItemId_int = Integer.parseInt(cartItemId);
            Optional<Cart> newCart = cartRepository.findByCartItemId(cartItemId_int);
            if (newCart.isPresent()) {
                cartItems.add(newCart.get());
                Optional<Stockpile> stockpile = stockpileRepository.findByProduct_Id(newCart.get().getProductId());
                if (stockpile.isPresent()) {
                    if (stockpile.get().getAmount() < 0) {
                        throw TomatoMallException.cartItemQuantityOutOfStock();
                    } else {
                        Optional<Product> product = productRepository.findById(newCart.get().getProductId());
                        if (!product.isPresent()) {
                            throw TomatoMallException.productNotFind();
                        }
                        totalAmount = totalAmount + newCart.get().getQuantity() * product.get().getPrice();
                    }
                } else {
                    throw TomatoMallException.stockpileNotFind();
                }
            } else {
                throw TomatoMallException.cartItemNotFind();
            }
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING.getCode());
        order.setPaymentMethod(payment_method);
        order.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));
        orderRepository.save(order);
        Integer orderId = order.getOrderId();

        // 生成订单明细快照，并移除购物车项
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cartItem : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrderId(orderId);
            oi.setProductId(cartItem.getProductId());

            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (!productOpt.isPresent()) {
                throw TomatoMallException.productNotFind();
            }
            Product product = productOpt.get();
            oi.setTitle(product.getTitle());
            oi.setCover(product.getCover());
            BigDecimal price = BigDecimal.valueOf(product.getPrice());
            oi.setPrice(price);
            oi.setQuantity(cartItem.getQuantity());
            oi.setSubtotal(price.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            // fill store/merchant snapshot for faster merchant queries and history retention
            Integer storeId = product.getStoreId();
            oi.setStoreId(storeId);
            if (storeId != null) {
                storeRepository.findById(storeId).ifPresent(store -> {
                    oi.setMerchantId(store.getMerchantId());
                });
            }
            orderItems.add(oi);
        }
        orderItemRepository.saveAll(orderItems);

        cartRepository.deleteAll(cartItems);

        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setUsername(username);
        orderSubmitVO.setPaymentMethod(payment_method);
        orderSubmitVO.setTotalAmount(String.valueOf(totalAmount));
        orderSubmitVO.setCreateTime(String.valueOf(LocalDateTime.now()));
        orderSubmitVO.setStatus(OrderStatus.PENDING.getCode());
        orderSubmitVO.setOrderId(String.valueOf(orderId));

        return orderSubmitVO;
    }
}
