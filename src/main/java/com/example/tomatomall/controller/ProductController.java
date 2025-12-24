package com.example.tomatomall.controller;


import com.example.tomatomall.enums.UserRole;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.products.SearchResultVO;
import com.example.tomatomall.vo.PageResultVO;
import com.example.tomatomall.vo.products.StockpileVO;
import com.example.tomatomall.vo.products.ProductVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController
{
    @Resource
    ProductService productService;

    @GetMapping
    public Response<SearchResultVO> getProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        // 公共商品列表（主站）不做基于登录用户的过滤
        return Response.buildSuccess(productService.getProductList(page, pageSize, sortBy, sortOrder));
    }

    /**
     * 商家/管理员的产品管理接口（仅用于管理页）
     * - 商家只能看到自己的商品
     * - 管理员看到所有商品
     */
    @GetMapping("/manage")
    public Response<SearchResultVO> getManageProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestAttribute(value = "userId", required = false) Integer userId,
            @RequestAttribute(value = "userRole", required = false) UserRole userRole) {
        // 仅在 Service 层处理权限与范围，Controller 仅负责路由和异常传递
        if (userRole == null) {
            throw com.example.tomatomall.exception.TomatoMallException.notLogin();
        }
        return Response.buildSuccess(productService.getManageProductList(page, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/{id}")
    public Response<ProductVO> getProduct(@PathVariable Integer id)
    {
        return Response.buildSuccess(productService.getProduct(id));
    }

    @PutMapping()
    public Response<String> updateProduct(@RequestBody ProductVO productVO){
        return Response.buildSuccess(productService.updateProduct(productVO));
    }

    @PostMapping()
    public Response<ProductVO> createProduct(@RequestBody ProductVO productVO){
        return Response.buildSuccess(productService.createProduct(productVO));
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteProduct(@PathVariable("id") Integer id) {
        return Response.buildSuccess(productService.deleteProduct(id));
    }

    @PatchMapping("/stockpile/{productId}")
    public Response<String> updateProductStockpile(@PathVariable("productId") Integer productId, @RequestBody Map<String, Integer> body) {
        Integer amount = body.get("amount");
        return Response.buildSuccess(productService.updateProductStockpile(productId, amount));
    }


    @GetMapping("/stockpile/{productId}")
    public Response<StockpileVO> getProductStockpile(@PathVariable("productId") Integer productId)
    {
        return Response.buildSuccess(productService.getProductStockpile(productId));
    }

    @GetMapping("/stockpile")
    public Response<PageResultVO<com.example.tomatomall.vo.products.StockpileVO>> getAllStockpile(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize)
    {
        return Response.buildSuccess(productService.getAllStockpile(page, pageSize));
    }

    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @param page 页码（从0开始，默认0）
     * @param pageSize 每页数量（默认20）
     * @param sortBy 排序字段（price, salesCount, rate）
     * @param sortOrder 排序方向（asc, desc）
     * @return 搜索结果
     */
    @GetMapping("/search")
    public Response<SearchResultVO> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder)
    {
        return Response.buildSuccess(productService.searchProducts(keyword, page, pageSize, sortBy, sortOrder));
    }

    /**
     * 公开接口：根据店铺ID分页获取该店铺的商品（用于店铺或商家公开页展示）
     */
    @GetMapping("/store/{storeId}")
    public Response<SearchResultVO> getProductsByStore(
            @PathVariable Integer storeId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Response.buildSuccess(productService.getProductsByStore(storeId, page, pageSize));
    }

}
