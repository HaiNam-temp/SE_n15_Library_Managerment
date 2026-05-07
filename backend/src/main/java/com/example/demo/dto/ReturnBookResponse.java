package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReturnBookResponse extends BaseDto {

    private String message;
    private BigDecimal fineAmount;
    private String fineReason;
}
