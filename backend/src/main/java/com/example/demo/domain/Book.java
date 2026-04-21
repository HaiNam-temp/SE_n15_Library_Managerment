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
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private Integer publishedYear;
    private String publisher;
    private String status;
    private LocalDate importedDate;

    @ManyToOne
    @JoinColumn(name = "book_category_id")
    private BookCategory bookCategory;

    @OneToMany(mappedBy = "book")
    private List<BorrowDetail> borrowDetails = new ArrayList<>();
}