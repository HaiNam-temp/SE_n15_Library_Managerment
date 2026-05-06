package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private Integer publishedYear;
    private String publisher;
    private String status;
    private LocalDate importedDate;
    private String isbn;
    private String description;
    private String categoryName;
    private Long categoryId;
}
