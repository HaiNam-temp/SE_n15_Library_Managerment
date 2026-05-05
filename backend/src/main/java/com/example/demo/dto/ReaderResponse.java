package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReaderResponse {
    private Long id;
    private String fullName;
    private String email;
    private String studentCodeOrCitizenId;
    private String phone;
    private LocalDate dateOfBirth;
    private String accountRole;
    private String accountStatus;
}
