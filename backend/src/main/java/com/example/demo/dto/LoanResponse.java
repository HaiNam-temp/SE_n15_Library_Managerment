package com.example.demo.dto;

import com.example.demo.enums.LoanStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LoanResponse extends BaseDto {

    private Long loanId;
    private Long readerId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LoanStatus status;
    private List<String> barcodes = new ArrayList<>();
}
