package com.example.tomatomall.controller;


import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController
{
    @Autowired
    CartService cartService;

    @PostMapping()
    public Response addCartItem(@RequestBody CartItemVO cartItemVO)
    {
        return Response.buildSuccess(cartService.addProductToCart(cartItemVO));
    }

    @DeleteMapping("/{cartItemId}")
    public Response deleteCartItem(@PathVariable String cartItemId)
    {
        return Response.buildSuccess(cartService.deleteProductFromCart(cartItemId));
    }
}
