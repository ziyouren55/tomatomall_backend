package com.example.tomatomall.repository;

import com.example.tomatomall.po.CartsOrdersRelation;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartsOrdersRelationRepository extends JpaRepository<CartsOrdersRelation, Integer> {
    @Query("SELECT c.cartItemId FROM CartsOrdersRelation c WHERE c.orderId = :orderId")
    List<Integer> findCartItemIdsByOrderId(@Param("orderId") Integer orderId);

    List<CartsOrdersRelation> findByOrderId(Integer orderId);
}

