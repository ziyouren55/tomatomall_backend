package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.BookComment;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.BookCommentRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.util.MyBeanUtil;
import com.example.tomatomall.vo.products.BookCommentVO;
import com.example.tomatomall.vo.products.ProductVO;
import com.example.tomatomall.vo.products.StockpileVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService
{
    @Autowired
    ProductRepository productRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Autowired
    BookCommentRepository bookCommentRepository;

    @Override
    public List<ProductVO> getProductList()
    {
        return productRepository.findAll().stream().map(Product::toVO).collect(Collectors.toList());
    }

    @Override
    public ProductVO getProduct(Integer id)
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
        Optional<Product> product = productRepository.findById(productVO.getId());
        if(!product.isPresent())
        {
            throw TomatoMallException.productNotFind();
        }
        BeanUtils.copyProperties(productVO,product.get(), MyBeanUtil.getNullPropertyNames(productVO));
        productRepository.save(product.get());
        return "更新成功";
    }

    @Override
    public ProductVO createProduct(ProductVO productVO)
    {
        Optional<Product> product = productRepository.findByTitle(productVO.getTitle());
        if(product.isPresent())
        {
            throw TomatoMallException.productAlreadyExists();
        }
        Product newProduct = productVO.toPO();
        productRepository.save(newProduct);
        Optional<Stockpile> stockpile = stockpileRepository.findByProductId(productVO.getId());
        if(stockpile.isPresent())
            stockpile.get().setAmount(stockpile.get().getAmount() + 1);
        else
        {
            StockpileVO stockpileVO = new StockpileVO();
            stockpileVO.setId((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
            stockpileVO.setProductId(newProduct.getId());
            stockpileVO.setAmount(1);
            stockpileVO.setFrozen(0);
            stockpileRepository.save(stockpileVO.toPO());
        }


        return newProduct.toVO();
    }

    @Override
    @Transactional
    public String deleteProduct(Integer id)
    {
        Optional<Product> product = productRepository.findById(id);
        if(!product.isPresent())
            throw TomatoMallException.productNotFind();
        productRepository.deleteById(id);
        return "删除成功";
    }

    @Override
    public String updateProductStockpile(Integer productId, Integer amount)
    {
        Optional<Stockpile> stockpile = stockpileRepository.findByProductId(productId);
        if(!stockpile.isPresent())
            throw TomatoMallException.productNotFind();
        stockpile.get().setAmount(amount);
        stockpileRepository.save(stockpile.get());
        return "调整库存成功";
    }

    @Override
    public StockpileVO getProductStockpile(Integer productId)
    {
        Optional<Stockpile> stockpile = stockpileRepository.findByProductId(productId);
        if(stockpile.isPresent())
            return stockpile.get().toVO();
        else
            throw TomatoMallException.productNotFind();
    }

    @Override
    public String addBookComment(Integer productId, BookCommentVO bookCommentVO) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()){
            bookCommentRepository.save(bookCommentVO.toPO());
        } else {
            throw TomatoMallException.productNotFind();
        }
        return "上传成功";
    }

    @Override
    public Set<BookCommentVO> getBookComment(Integer productId) {
        List<BookComment> Bookcomments = bookCommentRepository.findByProductId(productId);
        if (Bookcomments.isEmpty()) {
            throw TomatoMallException.productNotFind();
        }
        return Bookcomments.stream().map(BookComment::toVO).collect(Collectors.toSet());

    }

    @Override
    public String deleteBookComment(Integer id) {
        Optional<BookComment> bookComment = bookCommentRepository.findById(id);
        if (bookComment.isPresent()) {
            bookCommentRepository.delete(bookComment.get());
            return "删除成功";
        } else {
            throw new RuntimeException("未找到该评论");
        }
    }

}
