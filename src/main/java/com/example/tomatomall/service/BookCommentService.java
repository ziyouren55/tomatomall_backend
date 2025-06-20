package com.example.tomatomall.service;

import com.example.tomatomall.vo.products.BookCommentVO;

import java.util.Set;

public interface BookCommentService {

    String addBookComment(Integer productId, BookCommentVO bookCommentVO);

    Set<BookCommentVO> getBookComment(Integer productId);

    String deleteBookComment(Integer Id);
}
