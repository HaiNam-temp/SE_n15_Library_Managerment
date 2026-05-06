package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReaderDetailResponse {
    private Long id;
    private String fullName;
    private String email;
    private String studentCodeOrCitizenId;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private LocalDate cardCreatedDate;
    private LocalDate cardExpiredDate;
    private String cardStatus;
    private Integer currentBorrowedCount;
    private String accountRole;
    private String accountStatus;
    private List<LoanTicketResponse> recentLoans;
}
