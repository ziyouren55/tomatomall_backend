package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;
import com.example.tomatomall.vo.shopping.UpdateQuantityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

}
