package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.StockpileVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService
{
    @Override
    public List<ProductVO> getProductList()
    {
        return List.of();
    }

    @Override
    public ProductVO getProduct(String id)
    {
        return null;
    }

    @Override
    public String updateProduct(ProductVO productVO)
    {
        return "";
    }

    @Override
    public ProductVO createProduct(ProductVO productVO)
    {
        return null;
    }

    @Override
    public String deleteProduct(String id)
    {
        return "";
    }

    @Override
    public String updateStockpile(String productId, Integer amount)
    {
        return "";
    }

    @Override
    public StockpileVO getProductStockpile(String productId)
    {
        return null;
    }
}
