package com.example.demo.service;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.BookCategory;
import com.example.demo.dto.CategoryDetailResponse;
import com.example.demo.dto.CategoryRequest;
import com.example.demo.dto.CategoryResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.enums.CategoryStatus;
import com.example.demo.exceptions.ResourceAlreadyExistsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.helper.CategoryMapper;
import com.example.demo.repository.ActivityLogRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("start createCategory - request={}", request);

        if (categoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
            log.error("createCategory error - duplicate categoryName={}", request.getCategoryName());
            throw new ResourceAlreadyExistsException("Tên danh mục đã tồn tại.");
        }

        BookCategory category = CategoryMapper.toEntity(request);
        category.setStatus(CategoryStatus.ACTIVE);
        BookCategory savedCategory = categoryRepository.save(category);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction("Create Category");
        activityLog.setActionTime(LocalDateTime.now());
        activityLog.setDescription(String.format("Created category '%s'", savedCategory.getCategoryName()));
        activityLogRepository.save(activityLog);
        CategoryResponse response = CategoryMapper.toResponse(savedCategory);
        log.info("end createCategory - categoryId={}", response.getId());
        return response;
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        log.info("start updateCategory - categoryId={}, request={}", categoryId, request);

        BookCategory category = categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục sách không tồn tại."));

        if (categoryRepository.existsByCategoryNameIgnoreCaseAndIdNot(request.getCategoryName(), categoryId)) {
            log.error("updateCategory error - duplicate categoryName={} for categoryId={}", request.getCategoryName(),
                    categoryId);
            throw new ResourceAlreadyExistsException("Tên danh mục đã tồn tại.");
        }

        category.setCategoryName(request.getCategoryName().trim());
        category.setDescription(request.getDescription());
        BookCategory updatedCategory = categoryRepository.save(category);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction("Update Category");
        activityLog.setActionTime(LocalDateTime.now());
        activityLog.setDescription(String.format("Updated category '%s'", updatedCategory.getCategoryName()));
        activityLogRepository.save(activityLog);

        CategoryResponse response = CategoryMapper.toResponse(updatedCategory);
        log.info("end updateCategory - categoryId={}", response.getId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getCategories(int pageIndex, int pageSize) {
        log.info("start getCategories - pageIndex={}, pageSize={}", pageIndex, pageSize);

        if (pageIndex < 0 || pageSize <= 0) {
            log.error("getCategories invalid pagination - pageIndex={}, pageSize={}", pageIndex, pageSize);
            throw new IllegalArgumentException("pageIndex phải lớn hơn hoặc bằng 0 và pageSize phải lớn hơn 0");
        }

        PageRequest pageable = PageRequest.of(pageIndex, pageSize);
        Page<BookCategory> categoryPage = categoryRepository.findAllByStatus(CategoryStatus.ACTIVE, pageable);
        long totalCount = categoryRepository.countByStatus(CategoryStatus.ACTIVE);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        PageResponse<CategoryResponse> response = new PageResponse<>(
                categoryPage.map(CategoryMapper::toResponse).toList(),
                pageIndex,
                pageSize,
                totalCount,
                totalPages);

        log.info("end getCategories - totalCount={}, totalPages={}", totalCount, totalPages);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDetailResponse getCategoryDetail(Long categoryId) {
        log.info("start getCategoryDetail - categoryId={}", categoryId);

        BookCategory category = categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục sách không tồn tại."));

        long bookCount = bookRepository.countByBookCategory(category);
        CategoryDetailResponse response = CategoryMapper.toDetailResponse(category, bookCount);

        log.info("end getCategoryDetail - categoryId={}, bookCount={}", categoryId, bookCount);
        return response;
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("start deleteCategory - categoryId={}", categoryId);

        BookCategory category = categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục sách không tồn tại."));

        // Ràng buộc 1: Kiểm tra số sách thuộc danh mục
        long bookCount = bookRepository.countByBookCategory(category);
        if (bookCount > 0) {
            log.error("deleteCategory error - category has books, categoryId={}, bookCount={}", categoryId, bookCount);
            throw new IllegalStateException("Danh mục đang có sách, không thể xóa.");
        }

        // Ràng buộc 2: Kiểm tra danh mục mặc định
        if (Boolean.TRUE.equals(category.getIsDefault())) {
            log.error("deleteCategory error - cannot delete default category, categoryId={}", categoryId);
            throw new IllegalStateException("Không thể xóa danh mục mặc định.");
        }

        // Soft delete: set status to INACTIVE
        category.setStatus(CategoryStatus.INACTIVE);
        categoryRepository.save(category);

        // Ghi activity log
        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction("Delete Category");
        activityLog.setActionTime(LocalDateTime.now());
        activityLog.setDescription(String.format("Deleted category '%s'", category.getCategoryName()));
        activityLogRepository.save(activityLog);

        log.info("end deleteCategory - categoryId={}", categoryId);
    }
}
