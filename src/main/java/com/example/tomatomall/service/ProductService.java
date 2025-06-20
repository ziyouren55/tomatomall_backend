package com.example.tomatomall.service;

import com.example.tomatomall.vo.products.BookCommentVO;
import com.example.tomatomall.vo.products.ProductVO;
import com.example.tomatomall.vo.products.StockpileVO;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Set;

public interface ProductService
{
    List<ProductVO> getProductList();

    ProductVO getProduct(Integer id);

    String updateProduct(ProductVO productVO);

    ProductVO createProduct(ProductVO productVO);

    String deleteProduct(Integer id);

    String updateProductStockpile(Integer productId, Integer amount);

    StockpileVO getProductStockpile(Integer productId);

}
