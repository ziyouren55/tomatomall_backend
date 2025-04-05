package com.example.tomatomall.controller;


import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController
{
    @Autowired
    CartService cartService;

    @PostMapping()
    public Response addCartItem(@RequestBody CartItemVO cartItemVO, @RequestAttribute("userId") Integer userId)
    {
        return Response.buildSuccess(cartService.addProductToCart(cartItemVO, userId));
    }

    @DeleteMapping("/{cartItemId}")
    public Response deleteCartItem(@PathVariable String cartItemId, @RequestAttribute("userId") Integer userId)
    {
        return Response.buildSuccess(cartService.deleteProductFromCart(cartItemId, userId));
    }

    @PatchMapping("/{cartItemId}")
    public Response updateCartItemQuantity(@PathVariable String cartItemId,@RequestBody CartItemVO cartItemVO,@RequestAttribute("userId") Integer userId)
    {
        return Response.buildSuccess(cartService.updateCartItemQuantity(cartItemId,cartItemVO,userId));
    }

    @GetMapping()
    public Response getCartItemList(@RequestAttribute("userId") Integer userId)
    {
        return Response.buildSuccess(cartService.getProductListFromCart(userId));
    }

}
