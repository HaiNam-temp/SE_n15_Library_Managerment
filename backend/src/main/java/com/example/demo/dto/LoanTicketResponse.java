package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LoanTicketResponse {
    private Long loanId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String status;
    private Integer itemCount;
}
