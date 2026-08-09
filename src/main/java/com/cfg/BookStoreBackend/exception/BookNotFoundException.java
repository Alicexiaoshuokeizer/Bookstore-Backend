package com.cfg.BookStoreBackend.exception;

// extends NotFoundException so it's automatically caught by the shared
// handleNotFoundException handler in GlobalExceptionHandler - no separate
// handler needed just for books
public class BookNotFoundException extends NotFoundException {
    public BookNotFoundException(Long id) {
        super("Book not found with id: " + id);
    }
}