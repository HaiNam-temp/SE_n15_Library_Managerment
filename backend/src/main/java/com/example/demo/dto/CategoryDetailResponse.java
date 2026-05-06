package com.example.demo.dto;

import com.example.demo.enums.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CategoryDetailResponse {

    private Long id;
    private String categoryName;
    private String description;
    private CategoryStatus status;
    private boolean isDefault;
    private long bookCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
