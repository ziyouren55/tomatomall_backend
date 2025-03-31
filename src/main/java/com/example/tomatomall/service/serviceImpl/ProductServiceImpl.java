package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.util.MyBeanUtil;
import com.example.tomatomall.vo.products.ProductVO;
import com.example.tomatomall.vo.products.StockpileVO;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    StockpileRepository stockpileRepository;


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
        Optional<Product> product = productRepository.findById(String.valueOf(productVO.getId()));
        if(!product.isPresent())
        {
            throw TomatoMallException.productNotFind();
        }
        BeanUtils.copyProperties(productVO,product, MyBeanUtil.getNullPropertyNames(productVO));
        return "更新成功";
    }

    @Override
    public ProductVO createProduct(ProductVO productVO)
    {
        Product product = productRepository.findByTitle(productVO.getTitle());
        if(product != null)
        {
            throw TomatoMallException.productAlreadyExists();
        }
        Product newProduct = productVO.toPO();
        productRepository.save(newProduct);

        return newProduct.toVO();
    }

    @Override
    public String deleteProduct(String id)
    {
        Optional<Product> product = productRepository.findById(id);
        if(!product.isPresent())
            throw TomatoMallException.productNotFind();
        productRepository.deleteById(id);
        return "删除成功";
    }

    @Override
    public String updateProductStockpile(String productId, Integer amount)
    {
        Stockpile stockpile = stockpileRepository.findByProductId(productId);
        if(stockpile == null)
            throw TomatoMallException.productNotFind();
        stockpile.setAmount(amount);
        return "调整库存成功";
    }

    @Override
    public StockpileVO getProductStockpile(String productId)
    {
        Stockpile stockpile = stockpileRepository.findByProductId(productId);
        if(stockpile != null)
            return stockpile.toVO();
        else
            throw TomatoMallException.productNotFind();
    }
}
