package com.example.demo.helper;

import com.example.demo.domain.BookCategory;
import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;

public final class CategoryMapper {

    private CategoryMapper() {
        // Utility class
    }

    public static BookCategory toEntity(CategoryRequest request) {
        BookCategory category = new BookCategory();
        category.setCategoryName(request.getCategoryName().trim());
        category.setDescription(request.getDescription());
        return category;
    }

    public static CategoryResponse toResponse(BookCategory category) {
        return new CategoryResponse(
                category.getId(),
                category.getCategoryName(),
                category.getDescription(),
                category.getStatus());
    }

    public static com.example.demo.dto.CategoryDetailResponse toDetailResponse(BookCategory category, long bookCount) {
        return new com.example.demo.dto.CategoryDetailResponse(
                category.getId(),
                category.getCategoryName(),
                category.getDescription(),
                category.getStatus(),
                category.getIsDefault() != null ? category.getIsDefault() : false,
                bookCount,
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}
