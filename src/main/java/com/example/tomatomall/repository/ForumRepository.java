package com.example.tomatomall.repository;

import com.example.tomatomall.po.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Integer> {
    Optional<Forum> findByBookId(Integer bookId);
    List<Forum> findByStatus(String status);
    Page<Forum> findByStatus(String status, Pageable pageable);
    Page<Forum> findByStatusAndPostCountGreaterThan(String status, Integer postCount, Pageable pageable);
    Page<Forum> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Forum> findByNameContainingIgnoreCaseAndStatus(String keyword, String status, Pageable pageable);
    // 新增：查询所有已有论坛的书籍ID
    @Query("SELECT f.bookId FROM Forum f")
    List<Integer> findAllBookIds();
}
