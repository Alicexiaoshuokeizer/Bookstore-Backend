package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.BookNotFoundException;
import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.model.dto.BookDTO;
import com.cfg.BookStoreBackend.model.dto.BookResponseDTO;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.model.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    // if success, returns BookResponseDTO of the newly added book (never the raw entity)
    // if fails, returns log error info in console and throw InternalServerError error
    public BookResponseDTO addBook(BookDTO bookDTO) throws DatabaseException {
        try {
            Book newBook = new Book();
            //Input sanitation: trim extra space before and behind the string
            newBook.setTitle(bookDTO.getTitle().trim());
            newBook.setAuthor(bookDTO.getAuthor().trim());
            newBook.setPrice(bookDTO.getPrice());
            newBook.setStock(bookDTO.getStock());
            Book savedBook = bookRepository.save(newBook);
            return BookResponseDTO.fromEntity(savedBook);
        }
        catch (Exception ex) {
            // log message for internal tracking and debugging
            log.error("Failed to add book: {}", ex.getMessage());
            // throw exception for /exception/GlobalExceptionHandler to handle and send 500 status code response
            throw new DatabaseException("Failed to save new book");
        }
    }

    // get every book in the books table
    // no "not found" case here an empty catalog is valid, so this returns an empty list rather than an error
    public List<BookResponseDTO> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll().stream()
                .map(BookResponseDTO::fromEntity)
                .toList();
    }

    // get a single book by id
    // if the id doesn't exist, throw BookNotFoundException for /exception/GlobalExceptionHandler to catch
    // and turn into a 404, rather than letting a null through to the controller
    public BookResponseDTO getBookById(Long id) {
        log.info("Fetching book with id {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return BookResponseDTO.fromEntity(book);
    }

}