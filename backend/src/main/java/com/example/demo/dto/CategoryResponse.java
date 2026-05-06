package com.example.demo.dto;

import com.example.demo.enums.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String categoryName;
    private String description;
    private CategoryStatus status;
}
