package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "borrow_receipt")
public class BorrowReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String volumeTitle;
    private LocalDate borrowedDate;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    @OneToMany(mappedBy = "borrowReceipt")
    private List<BorrowDetail> borrowDetails = new ArrayList<>();
}