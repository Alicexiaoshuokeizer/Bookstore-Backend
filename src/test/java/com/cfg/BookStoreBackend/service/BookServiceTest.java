package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.NotFoundException;
import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.model.dto.BookDTO;
import com.cfg.BookStoreBackend.model.dto.BookResponseDTO;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.model.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    // Creates a mock BookRepository so the test doesn't use a real DB.
    @Mock
    private BookRepository bookRepository;

    // Creates BookService and injects the mocked repo into it.
    @InjectMocks
    private BookService bookService;

    // PUT test, update book should update an existing book.
    @Test
    void updateBookShouldUpdateExistingBook() throws DatabaseException {
        // Creates a book that the mock repo will return
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old title");
        existingBook.setAuthor("Old author");
        existingBook.setPrice(10.00);
        existingBook.setStock(2);

        // PUT endpoint updates the book details
        BookDTO updateRequest = new BookDTO();
        updateRequest.setTitle("New title");
        updateRequest.setAuthor("New author");
        updateRequest.setPrice(15.00);
        updateRequest.setStock(5);

        // This is what the repo should return
        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(existingBook))
                .thenReturn(existingBook);

        // Call the method that's being tested
        BookResponseDTO result = bookService.updateBook(1L, updateRequest);

        // Check the returned values
        assertEquals(1L, result.getId());
        assertEquals("New title", result.getTitle());
        assertEquals("New author", result.getAuthor());
        assertEquals(15.00, result.getPrice());
        assertEquals(5, result.getStock());

        // Verify that the repo methods were called
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(existingBook);
    }

    // PUT test, update book should throw not found exception for when book doesn't exist.
    @Test
    void updateBookShouldThrowNotFoundExceptionWhenBookDoesNotExist() {
        // Repo returns no book for this ID
        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        BookDTO updateRequest = new BookDTO();
        updateRequest.setTitle("New title");
        updateRequest.setAuthor("New author");
        updateRequest.setPrice(15.00);
        updateRequest.setStock(5);

        // Checks the correct exception is thrown
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookService.updateBook(999L, updateRequest)
        );

        assertEquals(
                "Book not found with id: 999",
                exception.getMessage()
        );

        // Makes sure missing books are not saved
        verify(bookRepository).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    // PUT test, update book should throw db exception when the save fails.
    @Test
    void updateBookShouldThrowDatabaseExceptionWhenSaveFails() {
        // Creates a book
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old title");
        existingBook.setAuthor("Old author");
        existingBook.setPrice(10.00);
        existingBook.setStock(2);

        // PUT endpoint updates the book details
        BookDTO updateRequest = new BookDTO();
        updateRequest.setTitle("New title");
        updateRequest.setAuthor("New author");
        updateRequest.setPrice(15.00);
        updateRequest.setStock(5);

        // This is what the repo should return
        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(existingBook))
                .thenThrow(new RuntimeException("Database unavailable"));

        // Act and assert
        DatabaseException exception = assertThrows(
                DatabaseException.class,
                () -> bookService.updateBook(1L, updateRequest)
        );

        assertEquals("Failed to update book", exception.getMessage());

        verify(bookRepository).findById(1L);
        verify(bookRepository).save(existingBook);
    }
}