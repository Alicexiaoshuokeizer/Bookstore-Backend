package com.cfg.BookStoreBackend.controller;


import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.model.dto.BookDTO;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
    private final BookService bookService;

    // Constructor to set up the book service for all BookController endpoints to use
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // post /api/books
    // if success, returns 201 created status code and book entity class including the book id
    // if fails, throws DatabaseException, exception is handled by /exception/GlobalExceptionHandler
    // to return a 500 status code and general error message
    @PostMapping("/api/books")
    public ResponseEntity<Book> addNewBook(@Valid @RequestBody BookDTO bookDTO) throws DatabaseException {
        return ResponseEntity
                .status(201)
                .body(bookService.addBook(bookDTO));
    }

}
