package com.example.tomatomall.service;

import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;

public interface CartService
{
    CartItemVO addProductToCart(CartItemVO cartItemVO);

    String deleteProductFromCart(String cartItemId);

    String updateCartItemQuantity(CartItemVO cartItemVO);

    CartItemsVO getProductListFromCart();
}
