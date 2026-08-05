package com.cfg.BookStoreBackend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// indicate this is a component that handler api endpoint exception to spring
@RestControllerAdvice
public class GlobalExceptionHandler {

    // catch DatabaseException that occurs in endpoints
    // response sends 500 internal server error status code
    // response also sends a general database error message
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<String> handleDatabaseException() {
        return ResponseEntity
                .internalServerError()
                .body("An error occurred while accessing the database");
    }


}
