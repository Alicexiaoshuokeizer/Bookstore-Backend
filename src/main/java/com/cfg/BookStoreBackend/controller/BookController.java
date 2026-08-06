package com.cfg.BookStoreBackend.controller;

import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.model.dto.BookDTO;
import com.cfg.BookStoreBackend.model.dto.BookResponseDTO;
import com.cfg.BookStoreBackend.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

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
    public ResponseEntity<BookResponseDTO> addNewBook(@Valid @RequestBody BookDTO bookDTO) throws DatabaseException {
        return ResponseEntity
                .status(201)
                .body(bookService.addBook(bookDTO));
    }

    // get /api/books
    // returns 200 ok status code and a list of every book in the catalog (empty list if there are none)
    @GetMapping("/api/books")
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        return ResponseEntity
                .ok(bookService.getAllBooks());
    }

    // get /api/books/{id}
    // if success, returns 200 ok status code and the matching book
    // if the id doesn't exist, throws BookNotFoundException, handled by /exception/GlobalExceptionHandler
    // to return a 404 status code and a message stating the book id could not be found
    @GetMapping("/api/books/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity
                .ok(bookService.getBookById(id));
    }
}