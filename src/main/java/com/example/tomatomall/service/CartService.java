package com.example.tomatomall.service;

import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;

public interface CartService
{
    CartItemVO addProductToCart(CartItemVO cartItemVO, Integer userId);

    String deleteProductFromCart(String cartItemId, Integer userId);

    String updateCartItemQuantity(String cartItemId,CartItemVO cartItemVO,Integer userId);

    CartItemsVO getProductListFromCart(Integer userId);
}
