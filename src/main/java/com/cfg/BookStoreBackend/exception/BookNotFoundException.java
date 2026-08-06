package com.cfg.BookStoreBackend.exception;

public class BookNotFoundException extends RuntimeException {

    // used by BookService when a requested book id doesn't exist
    // caught by GlobalExceptionHandler to return a 404 instead of a 500
    public BookNotFoundException(Long id) {
        super("Book not found with id: " + id);
    }
}