package com.example.demo.repository;

import com.example.demo.domain.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long>, BaseRepository {
}
