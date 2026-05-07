package com.example.demo.repository;

import com.example.demo.domain.BorrowingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowingRuleRepository extends JpaRepository<BorrowingRule, Long>, BaseRepository {

    Optional<BorrowingRule> findFirstByOrderByRuleIdAsc();
}
