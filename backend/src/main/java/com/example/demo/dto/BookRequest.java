package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequest {

    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 255, message = "Tên sách tối đa 255 ký tự")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    @Size(max = 255, message = "Tên tác giả tối đa 255 ký tự")
    private String author;

    private Integer publishedYear;

    @Size(max = 255, message = "Nhà xuất bản tối đa 255 ký tự")
    private String publisher;

    @NotBlank(message = "ISBN không được để trống")
    @Size(max = 100, message = "ISBN tối đa 100 ký tự")
    private String isbn;

    private String description;

//    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
}
