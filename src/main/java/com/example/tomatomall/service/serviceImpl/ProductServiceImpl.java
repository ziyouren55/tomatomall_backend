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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Override
    public List<ProductVO> getProductList() {
        return productRepository.findAll().stream().map(Product::toVO).collect(Collectors.toList());
    }

    @Override
    public ProductVO getProduct(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent())
            return product.get().toVO();
        else
            throw TomatoMallException.productNotFind();
    }

    @Override
    public String updateProduct(ProductVO productVO) {
        Optional<Product> product = productRepository.findById(productVO.getId());
        if (!product.isPresent()) {
            throw TomatoMallException.productNotFind();
        }
        BeanUtils.copyProperties(productVO, product.get(), MyBeanUtil.getNullPropertyNames(productVO));
        productRepository.save(product.get());
        return "更新成功";
    }

    @Override
    @Transactional
    public ProductVO createProduct(ProductVO productVO) {
        Optional<Product> product = productRepository.findByTitle(productVO.getTitle());
        if (product.isPresent()) {
            throw TomatoMallException.productAlreadyExists();
        }
        Product newProduct = productVO.toPO();
        productRepository.save(newProduct);
        
        // 修复：使用保存后生成的newProduct.getId()，而不是productVO.getId()
        Optional<Stockpile> stockpile = stockpileRepository.findByProduct_Id(newProduct.getId());
        if (stockpile.isPresent()) {
            // 如果库存已存在，增加库存
            stockpile.get().setAmount(stockpile.get().getAmount() + 1);
            stockpileRepository.save(stockpile.get());
        } else {
            // 创建新的库存记录，使用JPA关联关系
            Stockpile newStockpile = new Stockpile();
            newStockpile.setProduct(newProduct);
            newStockpile.setAmount(1); // 默认库存为1，可以根据需要调整
            newStockpile.setFrozen(0);
            stockpileRepository.save(newStockpile);
        }

        return newProduct.toVO();
    }

    @Override
    @Transactional
    public String deleteProduct(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent())
            throw TomatoMallException.productNotFind();
        
        // 由于使用了级联删除（orphanRemoval = true），删除商品时会自动删除关联的库存记录
        // 但为了确保数据一致性，我们也可以显式删除
        Optional<Stockpile> stockpile = stockpileRepository.findByProduct_Id(id);
        if (stockpile.isPresent()) {
            stockpileRepository.delete(stockpile.get());
        }
        
        productRepository.deleteById(id);
        return "删除成功";
    }

    @Override
    @Transactional
    public String updateProductStockpile(Integer productId, Integer amount) {
        Optional<Stockpile> stockpile = stockpileRepository.findByProduct_Id(productId);
        if (!stockpile.isPresent()) {
            // 如果库存不存在，创建默认库存记录
            Optional<Product> product = productRepository.findById(productId);
            if (!product.isPresent()) {
                throw TomatoMallException.productNotFind();
            }
            Stockpile newStockpile = new Stockpile();
            newStockpile.setProduct(product.get());
            newStockpile.setAmount(amount);
            newStockpile.setFrozen(0);
            stockpileRepository.save(newStockpile);
        } else {
            stockpile.get().setAmount(amount);
            stockpileRepository.save(stockpile.get());
        }
        return "调整库存成功";
    }

    @Override
    public StockpileVO getProductStockpile(Integer productId) {
        Optional<Stockpile> stockpile = stockpileRepository.findByProduct_Id(productId);
        if (stockpile.isPresent()) {
            return stockpile.get().toVO();
        } else {
            // 如果库存不存在，返回默认库存而不是抛出异常
            StockpileVO defaultStockpile = new StockpileVO();
            defaultStockpile.setProductId(productId);
            defaultStockpile.setAmount(0);
            defaultStockpile.setFrozen(0);
            // 尝试获取商品名称
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                defaultStockpile.setProductName(product.get().getTitle());
            }
            return defaultStockpile;
        }
    }

    @Override
    public List<StockpileVO> getAllStockpile() {
        return stockpileRepository.findAll().stream().map(Stockpile::toVO).collect(Collectors.toList());
    }
}
