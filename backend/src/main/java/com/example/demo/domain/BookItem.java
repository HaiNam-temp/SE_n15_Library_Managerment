package com.example.demo.domain;

import com.example.demo.enums.BookItemStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "book_item")
public class BookItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String barcode;

    @Enumerated(EnumType.STRING)
    private BookItemStatus status;

    private String location;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
