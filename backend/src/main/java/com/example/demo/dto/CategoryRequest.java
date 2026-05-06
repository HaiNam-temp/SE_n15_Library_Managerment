package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 255, message = "Tên danh mục tối đa 255 ký tự")
    private String categoryName;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;
}
