package com.example.tomatomall.service;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.vo.shopping.*;

import java.util.List;

public interface CartService
{
    CartItemVO addProductToCart(CartItemVO cartItemVO, Integer userId);

    String deleteProductFromCart(String cartItemId, Integer userId);

    String updateCartItemQuantity(String cartItemId, UpdateQuantityVO updateQuantityVO, Integer userId);

    CartItemsVO getProductListFromCart(Integer userId);

    OrderSubmitVO submitOrder(OrderCheckoutVO orderCheckoutVO, Account account);
}
