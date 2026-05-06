package com.example.demo.repository;

import com.example.demo.domain.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    boolean existsByEmail(String email);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reader r WHERE r.studentCodeOrCitizenId = :studentCodeOrCitizenId")
    boolean existsByStudentCodeOrCitizenId(@Param("studentCodeOrCitizenId") String studentCodeOrCitizenId);
}
