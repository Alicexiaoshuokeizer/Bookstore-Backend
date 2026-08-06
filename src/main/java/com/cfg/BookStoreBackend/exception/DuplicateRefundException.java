package com.cfg.BookStoreBackend.exception;

public class DuplicateRefundException extends RuntimeException {
    public DuplicateRefundException(String message) {
        super(message);
    }
}
