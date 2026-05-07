package com.example.demo.repository;

import com.example.demo.domain.BookItem;
import com.example.demo.domain.LoanDetail;
import com.example.demo.domain.LoanTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetail, Long>, BaseRepository {

    List<LoanDetail> findByLoanTicket(LoanTicket loanTicket);

    Optional<LoanDetail> findByBookItemAndActualReturnDateIsNull(BookItem bookItem);
}
