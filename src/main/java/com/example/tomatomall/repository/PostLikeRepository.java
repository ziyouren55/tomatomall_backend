package com.example.tomatomall.repository;

import com.example.tomatomall.po.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    Optional<PostLike> findByPostIdAndUserId(Integer postId, Integer userId);
    boolean existsByPostIdAndUserId(Integer postId, Integer userId);
}

