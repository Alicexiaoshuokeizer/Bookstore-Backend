package com.cfg.BookStoreBackend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime; // 👈 Added missing import
import java.util.HashMap;       // 👈 Added missing import
import java.util.Map;           // 👈 Added missing import

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

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBookNotFoundException(BookNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());

        return ResponseEntity
                .status(404)
                .body(body);
    }
}
