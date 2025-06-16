package com.example.tomatomall.repository;

import com.example.tomatomall.po.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    Page<Reply> findByPostIdAndStatus(Integer postId, String status, Pageable pageable);
    List<Reply> findByPostIdAndParentIdAndStatus(Integer postId, Integer parentId, String status);
    Page<Reply> findByUserId(Integer userId, Pageable pageable);
}
