package com.example.tomatomall.repository;

import com.example.tomatomall.po.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findByForumIdAndStatus(Integer forumId, String status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.forumId = ?1 AND p.isSticky = true AND p.status = 'NORMAL' ORDER BY p.createTime DESC")
    List<Post> findStickyPosts(Integer forumId);

    Page<Post> findByUserId(Integer userId, Pageable pageable);

    Page<Post> findByIsEssenceAndStatus(Boolean isEssence, String status, Pageable pageable);
}
