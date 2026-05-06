package com.example.demo.service;

import com.example.demo.dto.BookRequest;
import com.example.demo.dto.BookResponse;
import com.example.demo.dto.PageResponse;

public interface BookService {

    BookResponse createBook(BookRequest request);

    BookResponse updateBook(Long bookId, BookRequest request);

    void deleteBook(Long bookId);

    PageResponse<BookResponse> getBooks(int pageIndex, int pageSize);

    BookResponse getBookDetail(Long bookId);
}
