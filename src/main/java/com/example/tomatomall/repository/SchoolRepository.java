package com.example.tomatomall.repository;

import com.example.tomatomall.po.School;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// no additional imports required

@Repository
public interface SchoolRepository extends JpaRepository<School, String> {
    Page<School> findByCityCodeOrderByName(String cityCode, Pageable pageable);
    Page<School> findByCityCodeAndNameContainingIgnoreCase(String cityCode, String name, Pageable pageable);
    Page<School> findByNameContainingIgnoreCase(String name, Pageable pageable);
}


