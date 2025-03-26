package com.example.tomatomall.service;

import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.StockpileVO;

import java.util.List;

public interface ProductService
{
    List<ProductVO> getProductList();

    ProductVO getProduct(String id);

    String updateProduct(ProductVO productVO);

    ProductVO createProduct(ProductVO productVO);

    String deleteProduct(String id);

    String updateStockpile(String productId, Integer amount);

    StockpileVO getProductStockpile(String productId);

}
