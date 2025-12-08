package com.example.tomatomall.service;

import com.example.tomatomall.vo.bookComment.BookCommentVO;

import org.springframework.data.domain.Page;

public interface BookCommentService {

    String addBookComment(Integer productId, Integer userId, BookCommentVO bookCommentVO);

    Page<BookCommentVO> getBookComment(Integer productId, int page, int size);

    String deleteBookComment(Integer Id, Integer userId);
}
