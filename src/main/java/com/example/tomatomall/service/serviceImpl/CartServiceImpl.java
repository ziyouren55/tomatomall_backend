package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;
import com.example.tomatomall.vo.shopping.OrderSubmitVO;
import com.example.tomatomall.vo.shopping.UpdateQuantityVO;
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
public class CartServiceImpl implements CartService
{

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

    @Override
    public CartItemVO addProductToCart(CartItemVO cartItemVO, Integer userId)
    {
        Integer productId = Integer.valueOf(cartItemVO.getProductId());

        Optional<Product> product = productRepository.findById(productId);
        if(!product.isPresent())
            throw TomatoMallException.productNotFind();

        Optional<Cart> cartCheck = cartRepository.findByUserIdAndProductId(userId, productId);
        if(cartCheck.isPresent())
            throw TomatoMallException.cartItemAlreadyExists();

        //检查库存
        Optional<Stockpile> stockpile = stockpileRepository.findByProductId(productId);
        if (!stockpile.isPresent())
            throw TomatoMallException.stockpileNotFind();
        if(stockpile.get().getAmount() < cartItemVO.getQuantity())
            throw TomatoMallException.cartItemQuantityOutOfStock();

        Cart newCart = new Cart();
        newCart.setUserId(userId);
        newCart.setProductId(Integer.valueOf(cartItemVO.getProductId()));
        newCart.setQuantity(cartItemVO.getQuantity());
        cartRepository.save(newCart);

        Optional<Cart> cart = cartRepository.findByUserIdAndProductId(userId, productId);
        if(!cart.isPresent())
            throw TomatoMallException.cartItemNotFind();

        CartItemVO newCartItemVO = new CartItemVO();
        BeanUtils.copyProperties(product.get(),newCartItemVO);
        newCartItemVO.setProductId(String.valueOf(productId));
        newCartItemVO.setCartItemId(String.valueOf(cart.get().getCartItemId()));
        newCartItemVO.setQuantity(cartItemVO.getQuantity());

        return newCartItemVO;
    }

    @Override
    public String deleteProductFromCart(String cartItemId, Integer userId)
    {
        Optional<Cart> cart = cartRepository.findByCartItemId(Integer.valueOf(cartItemId));
        if(!cart.isPresent())
            throw TomatoMallException.cartItemNotFind();
        cartRepository.deleteById(Integer.valueOf(cartItemId));
        return "删除成功";
    }

    @Override
    public String updateCartItemQuantity(String cartItemId, UpdateQuantityVO updateQuantityVO, Integer userId)
    {
        Optional<Cart> cart = cartRepository.findByCartItemId(Integer.valueOf(cartItemId));
        if(!cart.isPresent())
            throw TomatoMallException.cartItemNotFind();

        //检查库存
        Integer productId = cart.get().getProductId();
        Optional<Stockpile> stockpile = stockpileRepository.findByProductId(productId);
        if(!stockpile.isPresent())
            throw TomatoMallException.stockpileNotFind();
        if (stockpile.get().getAmount() < updateQuantityVO.getQuantity())
            throw TomatoMallException.cartItemQuantityOutOfStock();

        cart.get().setQuantity(updateQuantityVO.getQuantity());
        cartRepository.save(cart.get());
        return "修改数量成功";
    }

    @Override
    public CartItemsVO getProductListFromCart(Integer userId)
    {
        List<Cart> cartList = cartRepository.findAll();
        CartItemsVO cartItemsVO = new CartItemsVO();
        List<CartItemVO> cartItemVOS = new ArrayList<>();
        double totalAmount = 0.0;
        for (Cart cart : cartList)
        {
            CartItemVO cartItemVO = toCartItemVO(cart);
            cartItemVOS.add(cartItemVO);
            totalAmount = totalAmount + cartItemVO.getPrice();
        }

        cartItemsVO.setCartItems(cartItemVOS);
        cartItemsVO.setTotal(cartList.size());
        cartItemsVO.setTotalAmount(totalAmount);

        return cartItemsVO;
    }

    private CartItemVO toCartItemVO(Cart cart)
    {
        CartItemVO cartItemVO = new CartItemVO();

        Integer productId = cart.getProductId();
        Optional<Product> product = productRepository.findById(productId);
        if(!product.isPresent())
            throw TomatoMallException.productNotFind();

        BeanUtils.copyProperties(product.get(),cartItemVO);
        cartItemVO.setProductId(String.valueOf(productId));
        cartItemVO.setCartItemId(String.valueOf(cart.getCartItemId()));
        cartItemVO.setQuantity(cart.getQuantity());

        return cartItemVO;
    }

    public OrderSubmitVO submitOrder(List<String> cartItemIds, Object shipping_address, String payment_method) {
        List<Cart> cartItems = new ArrayList<>();
        double totalAmount = 0;
        Integer cartItemId_int = Integer.valueOf(cartItemIds.get(0));
        for (String cartItemId : cartItemIds) {
            cartItemId_int = Integer.valueOf(cartItemId);
            Optional<Cart> newCart = cartRepository.findByCartItemId(cartItemId_int);
            if (newCart.isPresent()) {
                cartItems.add(newCart.get());
                Optional<Stockpile> stockpile = stockpileRepository.findByProductId(newCart.get().getProductId());
                if (stockpile.isPresent()) {
                    if (stockpile.get().getAmount() < newCart.get().getQuantity()) {
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
        Optional<Cart> newCart = cartRepository.findByCartItemId(cartItemId_int);

        Order order = new Order();
        order.setUserId(newCart.get().getUserId());
        order.setStatus("PENDING");
        order.setPaymentMethod(payment_method);
        order.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        order.setTotalAmount(BigDecimal.valueOf(totalAmount));
        orderRepository.save(order);
        Integer orderId = order.getOrderId();

        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setUsername(String.valueOf(accountRepository.findById(newCart.get().getUserId())));
        orderSubmitVO.setPaymentMethod(payment_method);
        orderSubmitVO.setTotalAmount(String.valueOf(totalAmount));
        orderSubmitVO.setCreateTime(String.valueOf(LocalDateTime.now()));
        orderSubmitVO.setStatus("PENDING");
        orderSubmitVO.setOrderId(String.valueOf(orderId));

        return orderSubmitVO;
    }
}
