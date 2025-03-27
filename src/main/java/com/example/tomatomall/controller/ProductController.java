package com.example.tomatomall.controller;


import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/products")
public class ProductController
{
    @Resource
    ProductService productService;

    @GetMapping
    public Response getProducts(){
        return Response.buildSuccess(productService.getProductList());
    }

    @GetMapping
    public Response getProduct(@RequestParam("id") String id)
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

    @DeleteMapping()
    public Response deleteProduct(@RequestParam("id") String id){
        return Response.buildSuccess(productService.deleteProduct(id));
    }

    @PatchMapping("/stockpile")
    public Response updateProductStockpile(@RequestParam("productId") String productId, @RequestParam("amount") Integer amount)
    {
        return Response.buildSuccess(productService.updateProductStockpile(productId,amount));
    }

    @GetMapping("/stockpile")
    public Response getProductStockpile(@RequestParam("productId") String productId)
    {
        return Response.buildSuccess(productService.getProductStockpile(productId));
    }
}
