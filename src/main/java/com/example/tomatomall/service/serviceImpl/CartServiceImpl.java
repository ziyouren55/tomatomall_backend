package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

        Cart newCart = new Cart();
        newCart.setUserId(userId);
        newCart.setProductId(Integer.valueOf(cartItemVO.getProductId()));
        newCart.setQuantity(cartItemVO.getQuantity());
        cartRepository.save(newCart);

        Optional<Cart> cart = cartRepository.findByUserIdAndProductId(userId, productId);
        if(!cart.isPresent())
            throw TomatoMallException.cartItemNotFind();

        CartItemVO newCartItemVO = new CartItemVO();
        newCartItemVO.setProductId(String.valueOf(productId));
        newCartItemVO.setCartItemId(String.valueOf(cart.get().getCartItemId()));
        newCartItemVO.setQuantity(cartItemVO.getQuantity());
        newCartItemVO.setCover(product.get().getCover());
        newCartItemVO.setDetail(product.get().getDetail());
        newCartItemVO.setPrice(product.get().getPrice());
        newCartItemVO.setTitle(product.get().getTitle());
        newCartItemVO.setDescription(product.get().getDescription());

        return newCartItemVO;
    }

    @Override
    public String deleteProductFromCart(String cartItemId, Integer userId)
    {
        return "";
    }

    @Override
    public String updateCartItemQuantity(String cartItemId,CartItemVO cartItemVO, Integer userId)
    {
        return "";
    }

    @Override
    public CartItemsVO getProductListFromCart(Integer userId)
    {
        return null;
    }

}
