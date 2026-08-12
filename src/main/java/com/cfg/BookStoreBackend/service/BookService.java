package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.exception.DuplicateOperationException;
import com.cfg.BookStoreBackend.exception.NotFoundException;
import com.cfg.BookStoreBackend.model.dto.BookDTO;
import com.cfg.BookStoreBackend.model.dto.BookResponseDTO;
import com.cfg.BookStoreBackend.model.dto.ReturnBookRequestDTO;
import com.cfg.BookStoreBackend.model.dto.ReturnBookResponseDTO;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.model.entity.Purchase;
import com.cfg.BookStoreBackend.model.repository.BookRepository;
import com.cfg.BookStoreBackend.model.repository.PurchaseRepository;
import com.cfg.BookStoreBackend.util.PurchaseStatus;
import jakarta.transaction.Transactional;
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
    // create book and purchase repository for server to communicate with db
    private final BookRepository bookRepository;
    private final PurchaseRepository purchaseRepository;

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

            // save new book
            Book savedBook = bookRepository.save(newBook);
            log.info("Successfully saved a new Book. Book ID={}, Book Title={}", savedBook.getId(),savedBook.getTitle());
            return BookResponseDTO.toResponseDTO(savedBook);
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
                .map(BookResponseDTO::toResponseDTO)
                .toList();
    }

    // get a single book by id
    // if the id doesn't exist, throw NotFoundException for /exception/GlobalExceptionHandler to catch
    // and turn into a 404, rather than letting a null through to the controller
    public BookResponseDTO getBookById(Long id) {
        log.info("Fetching book with id {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with id: " + id));
        return BookResponseDTO.toResponseDTO(book);
    }

    // Updates existing book via bookRepository.
    // If successful, finds, updates and saves the changes and returns the updated book object.
    // If book doesn't exist, throws NotFoundException.
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

            return BookResponseDTO.toResponseDTO(updatedBook);
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
    // If the book doesn't exist, throws NotFoundException.
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

    // Transactional process, if an exception occurs, the whole process rollbacks
    // Updates existing book stock via a provided purchase id, when dealing with return book from purchase
    // If successful, returns ReturnBookResponseDTO as confirmation
    // If purchase id/ book id does not exist, throws NotFoundException
    // If another error occurs, logs error and throws DatabaseException
    @Transactional
    public ReturnBookResponseDTO returnBook(ReturnBookRequestDTO dto)
            throws NotFoundException, DuplicateOperationException, DatabaseException
    {
        // find purchase based on the provided purchase id
        Long purchaseId = dto.getPurchaseId();
        Purchase purchase = purchaseRepository
                .findById(purchaseId)
                .orElseThrow(() -> {
                    log.warn("ReturnBook--purchase not found with id: {}", purchaseId);
                    return new NotFoundException("ReturnBook--purchase not found with id: " + purchaseId);
                });

        // check if purchase has already been processed to restock
        if (purchase.getStatus() == PurchaseStatus.RETURN) {
            log.warn("Return book rejected: Purchase id {} is already restocked", purchaseId);
            throw new DuplicateOperationException("Books of purchase id: " + purchaseId + " has already been restocked");
        }

        // find book based on book id in purchase
        Long bookId = purchase.getBook().getId();
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> {
                    log.warn("ReturnBook--book not found with id: {}", purchaseId);
                    return new NotFoundException("ReturnBook--book not found with id: " + bookId);
                });

        try {
            // update and save book stock
            book.setStock(book.getStock() + purchase.getQuantity());
            bookRepository.save(book);

            // update and save purchase status
            purchase.setStatus(PurchaseStatus.RETURN);
            Purchase updatePurchase = purchaseRepository.save(purchase);

            log.info("Successfully processed restock for purchase ID {}, book ID {}", purchaseId, bookId);
            return ReturnBookResponseDTO.toResponseDTO(updatePurchase);
        }
        catch (Exception e) {
            log.error("Failed to process return book with purchase id: {}", purchaseId);
            throw new DatabaseException("Failed to process returning purchased book");
        }
    }
}