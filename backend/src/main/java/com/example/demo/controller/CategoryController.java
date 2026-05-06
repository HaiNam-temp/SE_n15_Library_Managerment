package com.example.demo.controller;

import com.example.demo.dto.CategoryDetailResponse;
import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("start createCategory - request={}", request);
        CategoryResponse response = categoryService.createCategory(request);
        log.info("end createCategory - categoryId={}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> getCategories(
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "20") int pageSize) {
        log.info("start getCategories - pageIndex={}, pageSize={}", pageIndex, pageSize);
        PageResponse<CategoryResponse> response = categoryService.getCategories(pageIndex, pageSize);
        log.info("end getCategories - pageIndex={}, pageSize={}, totalCount={}", pageIndex, pageSize,
                response.getTotalCount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDetailResponse> getCategoryDetail(@PathVariable Long categoryId) {
        log.info("start getCategoryDetail - categoryId={}", categoryId);
        CategoryDetailResponse response = categoryService.getCategoryDetail(categoryId);
        log.info("end getCategoryDetail - categoryId={}", categoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        log.info("start updateCategory - categoryId={}, request={}", categoryId, request);
        CategoryResponse response = categoryService.updateCategory(categoryId, request);
        log.info("end updateCategory - categoryId={}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        log.info("start deleteCategory - categoryId={}", categoryId);
        categoryService.deleteCategory(categoryId);
        log.info("end deleteCategory - categoryId={}", categoryId);
        return ResponseEntity.noContent().build();
    }
}
