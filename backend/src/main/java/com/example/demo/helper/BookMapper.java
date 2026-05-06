package com.example.demo.helper;

import com.example.demo.domain.Book;
import com.example.demo.dto.BookRequest;
import com.example.demo.dto.BookResponse;

public final class BookMapper {

    private BookMapper() {
    }

    public static Book toEntity(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublishedYear(request.getPublishedYear());
        book.setPublisher(request.getPublisher());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        return book;
    }

    public static BookResponse toResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setPublishedYear(book.getPublishedYear());
        response.setPublisher(book.getPublisher());
        response.setStatus(book.getStatus());
        response.setImportedDate(book.getImportedDate());
        response.setIsbn(book.getIsbn());
        response.setDescription(book.getDescription());
        if (book.getBookCategory() != null) {
            response.setCategoryId(book.getBookCategory().getId());
            response.setCategoryName(book.getBookCategory().getCategoryName());
        }
        return response;
    }
}
