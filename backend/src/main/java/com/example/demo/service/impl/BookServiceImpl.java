package com.example.demo.service.impl;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.Book;
import com.example.demo.domain.BookCategory;
import com.example.demo.dto.BookRequest;
import com.example.demo.dto.BookResponse;
import com.example.demo.dto.PageResponse;
import com.example.demo.enums.CategoryStatus;
import com.example.demo.exceptions.ResourceAlreadyExistsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.helper.BookMapper;
import com.example.demo.repository.ActivityLogRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    @Transactional
    public BookResponse createBook(BookRequest request) {
        log.info("start createBook - request={}", request);

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            log.error("createBook error - duplicate isbn={}", request.getIsbn());
            throw new ResourceAlreadyExistsException("ISBN đã tồn tại.");
        }

//        BookCategory category = categoryRepository.findByIdAndStatus(request.getCategoryId(), CategoryStatus.ACTIVE)
//                .orElseThrow(() -> new ResourceNotFoundException("Danh mục sách không tồn tại hoặc không hoạt động."));

        BookCategory category;

        // Nếu người dùng có chọn một danh mục cụ thể
        if (request.getCategoryId() != null && request.getCategoryId() > 0) {
            category = categoryRepository.findByIdAndStatus(request.getCategoryId(), CategoryStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Danh mục sách không tồn tại hoặc không hoạt động."));
        }
        // Nếu người dùng KHÔNG chọn (null), lấy danh mục Placeholder (isDefault = true)
        else {
            category = categoryRepository.findByIsDefaultTrueAndStatus(CategoryStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Hệ thống chưa cài đặt danh mục mặc định (Placeholder)."));
        }

        Book book = BookMapper.toEntity(request);
        book.setBookCategory(category);
        book.setImportedDate(LocalDate.now());
        book.setStatus("AVAILABLE");

        Book savedBook = bookRepository.save(book);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction("Create Book");
        activityLog.setActionTime(LocalDateTime.now());
        activityLog.setDescription(String.format("Created book '%s' (ISBN: %s)", savedBook.getTitle(), savedBook.getIsbn()));
        activityLogRepository.save(activityLog);

        BookResponse response = BookMapper.toResponse(savedBook);
        log.info("end createBook - bookId={}", response.getId());
        return response;
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long bookId, BookRequest request) {
        log.info("start updateBook - bookId={}, request={}", bookId, request);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Sách không tồn tại."));

        if (bookRepository.existsByIsbnAndIdNot(request.getIsbn(), bookId)) {
            log.error("updateBook error - duplicate isbn={} for bookId={}", request.getIsbn(), bookId);
            throw new ResourceAlreadyExistsException("ISBN đã tồn tại.");
        }

        BookCategory category = categoryRepository.findByIdAndStatus(request.getCategoryId(), CategoryStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục sách không tồn tại hoặc không hoạt động."));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublishedYear(request.getPublishedYear());
        book.setPublisher(request.getPublisher());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setBookCategory(category);

        Book updatedBook = bookRepository.save(book);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction("Update Book");
        activityLog.setActionTime(LocalDateTime.now());
        activityLog.setDescription(String.format("Updated book '%s' (ISBN: %s)", updatedBook.getTitle(), updatedBook.getIsbn()));
        activityLogRepository.save(activityLog);

        BookResponse response = BookMapper.toResponse(updatedBook);
        log.info("end updateBook - bookId={}", response.getId());
        return response;
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId) {
        log.info("start deleteBook - bookId={}", bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Sách không tồn tại."));

        boolean isBorrowed = book.getBorrowDetails().stream()
                .anyMatch(detail -> detail.getReturnedDate() == null);

        if (isBorrowed) {
            log.error("deleteBook error - book is borrowed, bookId={}", bookId);
            throw new IllegalStateException("Sách đang được mượn, không thể xóa.");
        }

//        bookRepository.delete(book);
        book.setStatus("DELETED");
        bookRepository.save(book);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction("Delete Book");
        activityLog.setActionTime(LocalDateTime.now());
        activityLog.setDescription(String.format("Deleted book '%s' (ISBN: %s)", book.getTitle(), book.getIsbn()));
        activityLogRepository.save(activityLog);

        log.info("end deleteBook - bookId={}", bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getBooks(int pageIndex, int pageSize) {
        log.info("start getBooks - pageIndex={}, pageSize={}", pageIndex, pageSize);
        PageRequest pageable = PageRequest.of(pageIndex, pageSize);
        Page<Book> bookPage = bookRepository.findAll(pageable);
        
        return new PageResponse<>(
                bookPage.map(BookMapper::toResponse).toList(),
                pageIndex,
                pageSize,
                bookPage.getTotalElements(),
                bookPage.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookDetail(Long bookId) {
        log.info("start getBookDetail - bookId={}", bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Sách không tồn tại."));
        return BookMapper.toResponse(book);
    }
}
