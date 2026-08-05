package com.cfg.BookStoreBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Outputs an HTTP 404 status automatically
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
