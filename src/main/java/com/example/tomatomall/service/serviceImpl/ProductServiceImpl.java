package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.StockpileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService
{
    @Autowired
    ProductRepository productRepository;


    @Override
    public List<ProductVO> getProductList()
    {
        return productRepository.findAll().stream().map(Product::toVO).collect(Collectors.toList());
    }

    @Override
    public ProductVO getProduct(String id)
    {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent())
            return product.get().toVO();
        else
            throw TomatoMallException.productNotFind();
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
    public String updateProductStockpile(String productId, Integer amount)
    {
        return "";
    }

    @Override
    public StockpileVO getProductStockpile(String productId)
    {
        return null;
    }
}
