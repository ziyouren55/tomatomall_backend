package com.example.tomatomall.repository;

import com.example.tomatomall.po.Advertisements;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AdvertisementRepository extends JpaRepository<Advertisements,Integer> {
//    Optional<Advertisements> findById(Integer id);
}
