package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BorrowStatusDto extends BaseDto {

    private Long readerId;
    private String fullName;
    private Integer currentBorrowedCount;
    private Integer remainingBorrowCount;
    private Integer maxBorrowDays;
    private List<BorrowedBookDto> borrowedBooks = new ArrayList<>();
}
