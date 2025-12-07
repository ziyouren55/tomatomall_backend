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
    CartsOrdersRelationRepository cartsOrdersRelationRepository;

    @Override
    public CartItemVO addProductToCart(CartItemVO cartItemVO, Integer userId) {
        Integer productId = Integer.valueOf(cartItemVO.getProductId());

        Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent())
            throw TomatoMallException.productNotFind();

        Optional<Cart> cartCheck = cartRepository.findByUserIdAndProductId(userId, productId);
        if (cartCheck.isPresent())
            throw TomatoMallException.cartItemAlreadyExists();

        // 检查库存
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
        if (stockpile.getAmount() < cartItemVO.getQuantity())
            throw TomatoMallException.cartItemQuantityOutOfStock();


        Cart newCart = new Cart();
        newCart.setUserId(userId);
        newCart.setProductId(Integer.valueOf(cartItemVO.getProductId()));
        newCart.setQuantity(cartItemVO.getQuantity());
        newCart.setState("SHOW"); // 设置初始状态为SHOW
        cartRepository.save(newCart);

        stockpile.setAmount(stockpile.getAmount() - newCart.getQuantity());
        stockpileRepository.save(stockpile);

        Optional<Cart> cart = cartRepository.findByUserIdAndProductId(userId, productId);
        if (!cart.isPresent())
            throw TomatoMallException.cartItemNotFind();

        CartItemVO newCartItemVO = new CartItemVO();
        BeanUtils.copyProperties(product.get(), newCartItemVO);
        newCartItemVO.setProductId(String.valueOf(productId));
        newCartItemVO.setCartItemId(String.valueOf(cart.get().getCartItemId()));
        newCartItemVO.setQuantity(cartItemVO.getQuantity());

        return newCartItemVO;
    }

    @Override
    public String deleteProductFromCart(String cartItemId) {
        Optional<Cart> cart = cartRepository.findByCartItemId(Integer.valueOf(cartItemId));
        if (!cart.isPresent())
            throw TomatoMallException.cartItemNotFind();

        Optional<Stockpile> stockpileOpt = stockpileRepository.findByProduct_Id(cart.get().getProductId());
        if(!stockpileOpt.isPresent()) {
            // 如果库存不存在，说明数据异常，但为了不阻塞删除操作，我们跳过库存恢复
            cartRepository.deleteById(Integer.valueOf(cartItemId));
            return "删除成功（库存记录不存在，已跳过库存恢复）";
        }
        Stockpile stockpile = stockpileOpt.get();
        stockpile.setAmount(stockpile.getAmount() + cart.get().getQuantity());
        stockpileRepository.save(stockpile);
        cartRepository.deleteById(Integer.valueOf(cartItemId));

        return "删除成功";
    }

    @Override
    public String updateCartItemQuantity(String cartItemId, UpdateQuantityVO updateQuantityVO, Integer userId) {
        Optional<Cart> cart = cartRepository.findByCartItemId(Integer.valueOf(cartItemId));
        if (!cart.isPresent())
            throw TomatoMallException.cartItemNotFind();

        // 检查库存
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

        int firstAmount = cart.get().getQuantity();
        int lastAmount = updateQuantityVO.getQuantity();

        int num = firstAmount - lastAmount;
        int new_stock = stockpile.getAmount() + num;
        if (new_stock  < 0)
            throw TomatoMallException.cartItemQuantityOutOfStock();

        stockpile.setAmount(new_stock);

        stockpileRepository.save(stockpile);

        cart.get().setQuantity(updateQuantityVO.getQuantity());
        cartRepository.save(cart.get());
        return "修改数量成功";
    }

    @Override
    public CartItemsVO getProductListFromCart(Integer userId) {
        List<Cart> cartList = cartRepository.findAll();
        CartItemsVO cartItemsVO = new CartItemsVO();
        List<CartItemVO> cartItemVOS = new ArrayList<>();
        double totalAmount = 0.0;
        for (Cart cart : cartList) {
            // 只显示SHOW状态的购物车项
            if ("SHOW".equals(cart.getState())) {
                CartItemVO cartItemVO = toCartItemVO(cart);
                cartItemVOS.add(cartItemVO);
                totalAmount = totalAmount + cartItemVO.getPrice();
            }
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
        ReceiverInfoVO receiverInfoVO = orderCheckoutVO.getReceiverInfoVO();
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
        order.setStatus(OrderStatus.PENDING.name());
        order.setPaymentMethod(payment_method);
        order.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));
        orderRepository.save(order);
        Integer orderId = order.getOrderId();

        // 将购物车项状态改为HIDDEN并创建购物车-订单关系
        for (Cart cartItem : cartItems) {
            // 更新购物车项状态为HIDDEN
            cartItem.setState("HIDDEN");
            cartRepository.save(cartItem);

            // 创建购物车-订单关系
            CartsOrdersRelation relation = new CartsOrdersRelation();
            relation.setCartItemId(cartItem.getCartItemId());
            relation.setOrderId(orderId);
            cartsOrdersRelationRepository.save(relation);
        }

        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setUsername(username);
        orderSubmitVO.setPaymentMethod(payment_method);
        orderSubmitVO.setTotalAmount(String.valueOf(totalAmount));
        orderSubmitVO.setCreateTime(String.valueOf(LocalDateTime.now()));
        orderSubmitVO.setStatus(OrderStatus.PENDING.name());
        orderSubmitVO.setOrderId(String.valueOf(orderId));

        return orderSubmitVO;
    }
}
