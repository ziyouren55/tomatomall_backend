package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.BookComment;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.BookCommentRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.BookCommentService;
import com.example.tomatomall.vo.bookComment.BookCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookCommentServiceImpl implements BookCommentService {
    @Autowired
    BookCommentRepository bookCommentRepository;

    @Autowired
    ProductRepository productRepository;

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
