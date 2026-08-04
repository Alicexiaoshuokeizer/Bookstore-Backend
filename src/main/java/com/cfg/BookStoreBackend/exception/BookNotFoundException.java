package com.cfg.BookStoreBackend.exception;

// Custom exception thrown when a requested book id can't be found in the db
public class BookNotFoundException extends RuntimeException {

    // Constructor creates exception with a message, including missing book id.
    public BookNotFoundException(Long id) {
        super("Book not found with id: " + id);
    }
}