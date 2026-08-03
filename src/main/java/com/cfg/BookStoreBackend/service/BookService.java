package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.model.dto.BookDTO;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.model.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// indicate this is a service component for spring
@Service
// log info for debugging and tracking
@Slf4j
// constructor injection via lombok to indicate dependency of any final field variables
@RequiredArgsConstructor
public class BookService {
  // fields
    // create book repository for server to communicate with db
    private final BookRepository bookRepository;

  // methods
    // add new book to books table via bookRepository
    // if success, returns Book class object of the newly added book
    // if fails, returns log error info in console and throw InternalServerError error
    public Book addBook(BookDTO bookDTO) throws DatabaseException {
        try {
            Book newBook = new Book();
            newBook.setTitle(bookDTO.getTitle());
            newBook.setAuthor(bookDTO.getAuthor());
            newBook.setPrice(bookDTO.getPrice());
            newBook.setStock(bookDTO.getStock());
            return bookRepository.save(newBook);
        }
        catch (Exception ex) {
            // log message for internal tracking and debugging
            log.error("Failed to add book: {}", ex.getMessage());
            // throw exception for /exception/GlobalExceptionHandler to handle and send 500 status code response
            throw new DatabaseException("Failed to save new book");
        }
    }

}
