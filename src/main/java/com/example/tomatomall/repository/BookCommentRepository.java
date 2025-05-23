package com.example.tomatomall.repository;

import com.example.tomatomall.po.BookComment;
import com.example.tomatomall.po.Stockpile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookCommentRepository extends JpaRepository<BookComment,Integer> {
    List<BookComment> findByProductId(Integer productId);

}
