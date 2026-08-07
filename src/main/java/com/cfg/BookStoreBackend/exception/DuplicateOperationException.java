package com.cfg.BookStoreBackend.exception;

public class DuplicateOperationException extends RuntimeException {
    public DuplicateOperationException(String message) {
        super(message);
    }
}
