package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BorrowedBookDto extends BaseDto {

    private String barcode;
    private String title;
    private LocalDate dueDate;
}
