package com.example.tomatomall.repository;

import com.example.tomatomall.po.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Integer>
{
    Optional<Account> findByUsername(String username);
    long countByMemberLevelId(Integer memberLevelId);
}
