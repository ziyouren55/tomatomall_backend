package com.example.tomatomall.controller;


import com.example.tomatomall.po.Account;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;
import com.example.tomatomall.vo.shopping.OrderCheckoutVO;
import com.example.tomatomall.vo.shopping.OrderSubmitVO;
import com.example.tomatomall.vo.shopping.UpdateQuantityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController
{
    @Autowired
    CartService cartService;

    @PostMapping()
    public Response<CartItemVO> addCartItem(@RequestBody CartItemVO cartItemVO, @RequestAttribute("userId") Integer userId)
    {
        return Response.buildSuccess(cartService.addProductToCart(cartItemVO, userId));
    }

    @DeleteMapping("/{cartItemId}")
    public Response<String> deleteCartItem(@PathVariable String cartItemId) //删除了参数userid
    {
        return Response.buildSuccess(cartService.deleteProductFromCart(cartItemId));
    }

    @PatchMapping("/{cartItemId}")
    public Response<String> updateCartItemQuantity(@PathVariable String cartItemId, @RequestBody UpdateQuantityVO updateQuantityVO, @RequestAttribute("userId") Integer userId)
    {
        return Response.buildSuccess(cartService.updateCartItemQuantity(cartItemId,updateQuantityVO,userId));
    }

    @GetMapping()
    public Response<CartItemsVO> getCartItemList(@RequestAttribute("userId") Integer userId)
    {
        return Response.buildSuccess(cartService.getProductListFromCart(userId));
    }

    @PostMapping("/checkout")
    public Response<OrderSubmitVO> submitOrder(@RequestBody OrderCheckoutVO orderCheckoutVO,@RequestAttribute("currentUser") Account account){
        return Response.buildSuccess(cartService.submitOrder(orderCheckoutVO,account));
    }

}
