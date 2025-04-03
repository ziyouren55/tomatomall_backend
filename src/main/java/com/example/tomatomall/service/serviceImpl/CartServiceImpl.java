package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;

public class CartServiceImpl implements CartService
{
    @Override
    public CartItemVO addProductToCart(CartItemVO cartItemVO)
    {
        return null;
    }

    @Override
    public String deleteProductFromCart(String cartItemId)
    {
        return "";
    }

    @Override
    public String updateCartItemQuantity(CartItemVO cartItemVO)
    {
        return "";
    }

    @Override
    public CartItemsVO getProductListFromCart()
    {
        return null;
    }
}
