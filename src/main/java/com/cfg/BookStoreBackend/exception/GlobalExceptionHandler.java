package com.cfg.BookStoreBackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// indicate this is a component that handler api endpoint exception to spring
@RestControllerAdvice
@Slf4j
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

    // catch NotFoundException that occurs in endpoints
    // response sends 404 not found status code
    // response also sends an endpoint customized not found message
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(404)
                .body(e.getMessage());
    }

    // catch OutOfStockException that occurs when book is out of stock
    // response sends 409 Conflict client error indicates a request
    // conflict with the current state of the target resource
    // response sends an endpoint customized not out of stock message
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<String> handleOutOfStockException(OutOfStockException e) {
        return ResponseEntity
                .status(409)
                .body(e.getMessage());
    }

    // catch DuplicateRefundException that occurs when a purchase is already refunded/return book
    // response sends 409 Conflict client error indicates a request
    // conflict with the current state of the target resource
    // response sends an endpoint customized already refunded/returned message
    @ExceptionHandler(DuplicateOperationException.class)
    public ResponseEntity<String> handleDuplicateOperationException(DuplicateOperationException e) {
        return ResponseEntity
                .status(409)
                .body(e.getMessage());
    }

    // catch MethodArgumentTypeMismatchException that occurs when method arguments have mismatched type
    // e.g. argument (id: type Long ) in controller, input id value > Long.Max,
    // program fails to parse id to Long, and throw MethodArgumentTypeMismatchException
    // response sends 400 bad request status and error message indicate the id is in invalid format
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();
        return ResponseEntity
                .status(400)
                .body(String.format("Invalid data format: %s", parameterName));
    }

//    =============Final Defense============================================
//    =============This Exception handler must be the last handler in this class===========================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handlerUnexpectedRuntimeException(RuntimeException ex, HttpServletRequest request) {
        // log error at server side to help debugging
        // show request url (endpoint), show error message, show error traceback details
        log.error("Unexpected error at: [{}] {}", request.getRequestURL(), ex.getMessage(), ex);
        return ResponseEntity
                .status(500)
                .body("An unexpected error occurs, please try again later");
    }
}
