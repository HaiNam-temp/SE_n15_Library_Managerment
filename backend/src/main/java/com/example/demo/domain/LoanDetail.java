package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "loan_detail")
public class LoanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate actualReturnDate;
    private String returnCondition;

    @ManyToOne
    @JoinColumn(name = "loan_ticket_id")
    private LoanTicket loanTicket;

    @ManyToOne
    @JoinColumn(name = "book_item_id")
    private BookItem bookItem;
}
