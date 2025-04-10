package com.example.tomatomall.service;

import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;
import com.example.tomatomall.vo.shopping.OrderSubmitVO;
import com.example.tomatomall.vo.shopping.UpdateQuantityVO;

import java.util.List;

public interface CartService
{
    CartItemVO addProductToCart(CartItemVO cartItemVO, Integer userId);

    String deleteProductFromCart(String cartItemId);

    String updateCartItemQuantity(String cartItemId, UpdateQuantityVO updateQuantityVO, Integer userId);

    CartItemsVO getProductListFromCart(Integer userId);

    OrderSubmitVO submitOrder(List<String> cartItemIds, Object shipping_address, String payment_method);
}
