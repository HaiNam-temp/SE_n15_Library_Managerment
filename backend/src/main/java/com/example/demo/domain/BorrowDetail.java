package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "borrow_detail")
public class BorrowDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate returnedDate;

    @ManyToOne
    @JoinColumn(name = "borrow_receipt_id")
    private BorrowReceipt borrowReceipt;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}