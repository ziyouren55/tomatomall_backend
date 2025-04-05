package com.example.tomatomall.repository;

import com.example.tomatomall.po.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Integer>
{
    Cart findByCartItemId(Integer cartItemId);
    Optional<Cart> findByUserIdAndProductId(Integer userId, Integer productId);
}
