package com.example.demo.repository;

import com.example.demo.domain.LoanTicket;
import com.example.demo.domain.Reader;
import com.example.demo.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanTicketRepository extends JpaRepository<LoanTicket, Long>, BaseRepository {

    List<LoanTicket> findByReaderAndStatus(Reader reader, LoanStatus status);
}
