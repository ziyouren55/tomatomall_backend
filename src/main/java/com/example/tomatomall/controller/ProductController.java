package com.example.tomatomall.controller;


import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.products.ProductVO;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.products.StockpileVO;
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
    public Response getProducts(){
        return Response.buildSuccess(productService.getProductList());
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
}
