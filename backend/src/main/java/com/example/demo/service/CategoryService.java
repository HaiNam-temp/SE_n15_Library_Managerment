package com.example.demo.service;

import com.example.demo.dto.CategoryDetailResponse;
import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.dto.PageResponse;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    PageResponse<CategoryResponse> getCategories(int pageIndex, int pageSize);

    CategoryDetailResponse getCategoryDetail(Long categoryId);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest request);

    void deleteCategory(Long categoryId);
}
