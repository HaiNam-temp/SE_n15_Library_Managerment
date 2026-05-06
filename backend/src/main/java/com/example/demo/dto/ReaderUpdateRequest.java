package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReaderUpdateRequest {

    @NotBlank(message = "fullName is required")
    private String fullName;

    @Email(message = "email must be valid")
    @NotBlank(message = "email is required")
    private String email;

    @NotBlank(message = "studentCodeOrCitizenId is required")
    private String studentCodeOrCitizenId;

    private String gender;
    private String address;
    private String phone;
    private LocalDate dateOfBirth;
    private String cardStatus;
}
