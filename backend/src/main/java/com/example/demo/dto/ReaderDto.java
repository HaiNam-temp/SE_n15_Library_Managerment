package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReaderDto extends BaseDto {

    private Long id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String email;
    private LocalDate cardCreatedDate;
    private LocalDate cardExpiredDate;
    private String cardStatus;
    private Integer currentBorrowedCount;
    private String phone;
    private String studentCodeOrCitizenId;
    private Long borrowingRuleId;
}
