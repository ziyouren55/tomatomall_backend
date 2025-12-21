package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.UserRole;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.BookComment;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.BookCommentRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.BookCommentService;
import com.example.tomatomall.vo.bookComment.BookCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class BookCommentServiceImpl implements BookCommentService {
    @Autowired
    BookCommentRepository bookCommentRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    public String addBookComment(Integer productId, Integer userId, BookCommentVO bookCommentVO) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()){
            BookComment toSave = bookCommentVO.toPO();
            // 确保路径参数的 productId 写入实体，防止前端未传导致为空
            toSave.setProductId(productId);
            if (userId != null) {
                toSave.setUserId(userId);
                // 设置姓名为账号姓名或用户名，避免前端收集
                accountRepository.findById(userId).ifPresent(acc -> {
                    String displayName = acc.getName() != null && !acc.getName().isEmpty()
                        ? acc.getName()
                        : acc.getUsername();
                    toSave.setName(displayName != null ? displayName : "匿名用户");
                });
            } else {
                toSave.setName("匿名用户");
            }
            toSave.setCreateTime(new Date());
            bookCommentRepository.save(toSave);
        } else {
            throw TomatoMallException.productNotFind();
        }
        return "上传成功";
    }

    @Override
    public Page<BookCommentVO> getBookComment(Integer productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookComment> bookComments = bookCommentRepository.findByProductId(productId, pageable);
        return bookComments.map(BookComment::toVO);
    }

    @Override
    public String deleteBookComment(Integer id, Integer userId) {
        Optional<BookComment> bookComment = bookCommentRepository.findById(id);
        if (bookComment.isPresent()) {
            if (userId != null) {
                BookComment comment = bookComment.get();
                if (!userId.equals(comment.getUserId())) {
                    // 允许管理员删除
                    Optional<Account> account = accountRepository.findById(userId);
                    if (!account.isPresent() || account.get().getRole() == null || account.get().getRole() == UserRole.CUSTOMER) {
                        throw new TomatoMallException("无权限删除该评论");
                    }
                }
            }
            bookCommentRepository.delete(bookComment.get());
            return "删除成功";
        } else {
            throw new RuntimeException("未找到该评论");
        }
    }
}
