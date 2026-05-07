package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateLoanRequest extends BaseDto {

    private Long readerId;
    private Long librarianId;
    private List<String> barcodes;
}
