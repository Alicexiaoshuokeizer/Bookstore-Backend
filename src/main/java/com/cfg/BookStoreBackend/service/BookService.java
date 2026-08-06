package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.exception.NotFoundException;
import com.cfg.BookStoreBackend.model.dto.BookDTO;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.model.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.cfg.BookStoreBackend.model.dto.BookResponseDTO;

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
    public BookResponseDTO addBook(BookDTO bookDTO) throws DatabaseException {
        try {
            Book newBook = new Book();
            //Input sanitation: trim extra space before and behind the string
            newBook.setTitle(bookDTO.getTitle().trim());
            newBook.setAuthor(bookDTO.getAuthor().trim());
            newBook.setPrice(bookDTO.getPrice());
            newBook.setStock(bookDTO.getStock());

            // save new book
            Book saved = bookRepository.save(newBook);
            return new BookResponseDTO(
                    saved.getId(),
                    saved.getTitle(),
                    saved.getAuthor(),
                    saved.getPrice(),
                    saved.getStock()
            );
        }
        catch (Exception ex) {
            // log message for internal tracking and debugging
            log.error("Failed to add book: {}", ex.getMessage());
            // throw exception for /exception/GlobalExceptionHandler to handle and send 500 status code response
            throw new DatabaseException("Failed to save new book");
        }
    }

    // Updates existing book via bookRepository.
    // If successful, finds, updates and saves the changes and returns the updated book object.
    // If book doesn't exist, throws BookNotFoundException.
    // If another error occurs, logs error and throws DatabaseException.
    public BookResponseDTO updateBook(Long id, BookDTO bookDTO) throws DatabaseException {
        try {
            // Retrieves existing book from db by id.
            Book existingBook = bookRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Book not found with id: " + id));

            // Updates existing book with new values.
            existingBook.setTitle(bookDTO.getTitle().trim());
            existingBook.setAuthor(bookDTO.getAuthor().trim());
            existingBook.setPrice(bookDTO.getPrice());
            existingBook.setStock(bookDTO.getStock());

            // Saves updated book.
            Book updatedBook = bookRepository.save(existingBook);

            return new BookResponseDTO(
                    updatedBook.getId(),
                    updatedBook.getTitle(),
                    updatedBook.getAuthor(),
                    updatedBook.getPrice(),
                    updatedBook.getStock()
            );
        }
        catch (NotFoundException ex) {
            // Log for missing book
            log.warn("Book update failed: {}", ex.getMessage());
            throw ex;
        }
        catch (Exception ex) {
            // Throws exception for /exception/GlobalExceptionHandler
            // to handle and send 500 status code response.
            log.error("Failed to update book {}: {}", id, ex.getMessage());
            throw new DatabaseException("Failed to update book");
        }
    }

    // Deletes an existing book via bookRepository by its ID.
    // If the book doesn't exist, throws BookNotFoundException.
    // If a database access error occurs, logs the error and throws DatabaseException.
    public void deleteBook(Long id) throws DatabaseException {
        // Look for the book first. If missing, throw our custom exception to trigger a 404.
        if (!bookRepository.existsById(id)) {
            log.warn("Book deletion failed: Book not found with id: {}", id);
            throw new NotFoundException("Book not found with id: " + id);
        }

        try {
            bookRepository.deleteById(id);
            log.info("Successfully deleted book with id: {}", id);
        } catch (Exception ex) {
            log.error("Failed to delete book {}: {}", id, ex.getMessage());
            throw new DatabaseException("Failed to delete book");
        }

    }
}


