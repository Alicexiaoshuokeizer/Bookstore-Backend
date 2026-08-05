package com.cfg.BookStoreBackend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

// indicate this is a component that handler api endpoint exception to spring
@RestControllerAdvice
public class GlobalExceptionHandler {

    // catch DatabaseException that occurs in endpoints
    // response sends 500 internal server error status code
    // response also sends a general database error message
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<String> handleDatabaseException(DatabaseException e) {
        return ResponseEntity
                .internalServerError()
                .body("An error occurred while accessing the database");
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFoundException(BookNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
    // catch NotFoundException that occurs in endpoints
    // response sends 404 not found status code
    // response also sends an endpoint customized not found message
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    // catch OutOfStockException that occurs when book is out of stock
    // response sends 409 Conflict client error indicates a request
    // conflict with the current state of the target resource
    // response sends an endpoint customized not out of stock message
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<String> handleOutOfStockException(OutOfStockException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }



}
