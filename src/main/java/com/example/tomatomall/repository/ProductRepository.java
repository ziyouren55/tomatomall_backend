package com.example.tomatomall.repository;

import com.example.tomatomall.po.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer>
{
    Optional<Product> findByTitle(String title);

    List<Product> findBySalesCountGreaterThanAndIdNotIn(Integer salesThreshold, List<Integer> existingBookIds);

    /**
     * 搜索商品：根据关键词在标题、描述、详情中搜索
     * 使用模糊匹配，支持部分匹配
     */
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.detail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findProductsByKeyword(@Param("keyword") String keyword);
}
