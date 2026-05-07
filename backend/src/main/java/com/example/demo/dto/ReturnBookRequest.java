package com.example.demo.dto;

import com.example.demo.enums.ReturnCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnBookRequest extends BaseDto {

    private String barcode;
    private ReturnCondition condition;
}
