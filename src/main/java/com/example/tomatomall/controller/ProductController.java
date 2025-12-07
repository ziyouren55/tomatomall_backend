package com.example.tomatomall.controller;


import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.products.ProductVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController
{
    StockpileRepository stockpileRepository;

    @Resource
    ProductService productService;

    @GetMapping
    public Response getProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        return Response.buildSuccess(productService.getProductList(page, pageSize, sortBy, sortOrder));
    }

    @GetMapping("/{id}")
    public Response getProduct(@PathVariable Integer id)
    {
        return Response.buildSuccess(productService.getProduct(id));
    }

    @PutMapping()
    public Response updateProduct(@RequestBody ProductVO productVO){
        return Response.buildSuccess(productService.updateProduct(productVO));
    }

    @PostMapping()
    public Response createProduct(@RequestBody ProductVO productVO){
        return Response.buildSuccess(productService.createProduct(productVO));
    }

    @DeleteMapping("/{id}")
    public Response deleteProduct(@PathVariable("id") Integer id) {
        return Response.buildSuccess(productService.deleteProduct(id));
    }

    @PatchMapping("/stockpile/{productId}")
    public Response updateProductStockpile(@PathVariable("productId") Integer productId, @RequestBody Map<String, Integer> body) {
        Integer amount = body.get("amount");
        return Response.buildSuccess(productService.updateProductStockpile(productId, amount));
    }


    @GetMapping("/stockpile/{productId}")
    public Response getProductStockpile(@PathVariable("productId") Integer productId)
    {
        return Response.buildSuccess(productService.getProductStockpile(productId));
    }

    @GetMapping("/stockpile")
    public Response getAllStockpile(
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
    public Response searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder)
    {
        return Response.buildSuccess(productService.searchProducts(keyword, page, pageSize, sortBy, sortOrder));
    }

}
