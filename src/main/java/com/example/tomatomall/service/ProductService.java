package com.example.tomatomall.service;

import com.example.tomatomall.vo.PageResultVO;
import com.example.tomatomall.vo.products.ProductVO;
import com.example.tomatomall.vo.products.SearchResultVO;
import com.example.tomatomall.vo.products.StockpileVO;

public interface ProductService
{
    /**
     * 获取商品列表（分页）
     * @param page 页码（从0开始）
     * @param pageSize 每页数量
     * @param sortBy 排序字段（price, salesCount, rate）
     * @param sortOrder 排序方向（asc, desc）
     * @return 分页结果
     */
    SearchResultVO getProductList(Integer page, Integer pageSize, String sortBy, String sortOrder);

    ProductVO getProduct(Integer id);

    String updateProduct(ProductVO productVO);

    ProductVO createProduct(ProductVO productVO);

    String deleteProduct(Integer id);

    String updateProductStockpile(Integer productId, Integer amount);

    StockpileVO getProductStockpile(Integer productId);

    /**
     * 根据店铺ID分页获取该店铺的商品（公开接口可调用）
     */
    SearchResultVO getProductsByStore(Integer storeId, Integer page, Integer pageSize);

    /**
     * 管理端获取商品列表（商家管理页 / 管理员管理页）
     * Service 层内部会从 UserContext 获取当前用户并决定返回范围
     */
    SearchResultVO getManageProductList(Integer page, Integer pageSize, String sortBy, String sortOrder);

    /**
     * 获取所有库存（分页）
     * @param page 页码（从0开始）
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResultVO<StockpileVO> getAllStockpile(Integer page, Integer pageSize);

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
