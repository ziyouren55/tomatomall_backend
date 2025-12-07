package com.example.tomatomall.repository;

import com.example.tomatomall.po.Stockpile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockpileRepository extends JpaRepository<Stockpile,Integer>
{
    // 通过关联的Product实体的ID查找库存
    Optional<Stockpile> findByProduct_Id(Integer productId);

    // 向后兼容的方法（已废弃，建议使用findByProduct_Id）
    @Deprecated
    default Optional<Stockpile> findByProductId(Integer productId) {
        return findByProduct_Id(productId);
    }

}
