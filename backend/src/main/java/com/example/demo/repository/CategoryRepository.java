package com.example.demo.repository;

import com.example.demo.domain.BookCategory;
import com.example.demo.enums.CategoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<BookCategory, Long> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    boolean existsByCategoryNameIgnoreCaseAndIdNot(String categoryName, Long id);

    Optional<BookCategory> findByCategoryNameIgnoreCase(String categoryName);

    Optional<BookCategory> findByIdAndStatus(Long id, CategoryStatus status);

    Optional<BookCategory> findByIsDefaultTrueAndStatus(CategoryStatus status);

    Page<BookCategory> findAllByStatus(CategoryStatus status, Pageable pageable);

    long countByStatus(CategoryStatus status);
}
