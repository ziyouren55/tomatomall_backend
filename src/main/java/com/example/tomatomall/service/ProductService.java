package com.example.tomatomall.service;

import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.vo.products.ProductVO;
import com.example.tomatomall.vo.products.SearchResultVO;
import com.example.tomatomall.vo.products.StockpileVO;

import java.util.List;

public interface ProductService
{
    List<ProductVO> getProductList();

    ProductVO getProduct(Integer id);

    String updateProduct(ProductVO productVO);

    ProductVO createProduct(ProductVO productVO);

    String deleteProduct(Integer id);

    String updateProductStockpile(Integer productId, Integer amount);

    StockpileVO getProductStockpile(Integer productId);

    List<StockpileVO> getAllStockpile();

    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param pageSize 每页数量
     * @param sortBy 排序字段（price, salesCount, rate）
     * @param sortOrder 排序方向（asc, desc）
     * @return 搜索结果
     */
    SearchResultVO searchProducts(String keyword, Integer page, Integer pageSize, String sortBy, String sortOrder);
}
