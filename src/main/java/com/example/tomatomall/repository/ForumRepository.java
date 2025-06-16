package com.example.tomatomall.repository;

import com.example.tomatomall.po.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Integer> {
    Optional<Forum> findByBookId(Integer bookId);
    List<Forum> findByStatus(String status);
    // 新增：查询所有已有论坛的书籍ID
    @Query("SELECT f.bookId FROM Forum f")
    List<Integer> findAllBookIds();
}
