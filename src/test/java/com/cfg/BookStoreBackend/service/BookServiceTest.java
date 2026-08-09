package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.exception.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
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

    // PUT test, update book should throw book not found exception for when book doesn't exist.
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

    // DELETE test, delete book should succeed when the book exists in the database.
    @Test
    void deleteBookShouldSucceedWhenBookExists() throws DatabaseException {
        // Arrange: Tell the mock repository to report that the book ID exists
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        // Act & Assert: Execute the service call and verify it completes without errors
        assertDoesNotThrow(() -> bookService.deleteBook(1L));

        // Verify that the repository interactions happened exactly once
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }

    // DELETE test, delete book should throw book not found exception when the ID is missing.
    @Test
    void deleteBookShouldThrowNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange: Force the repository to state the ID is missing from MySQL
        when(bookRepository.existsById(999L)).thenReturn(false);

        // Act & Assert: Verify it bubbles up your custom 404 tracking exception
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookService.deleteBook(999L)
        );

        assertEquals("Book not found with id: 999", exception.getMessage());

        // Verify existence was checked but the deletion execution was safely skipped
        verify(bookRepository, times(1)).existsById(999L);
        verify(bookRepository, never()).deleteById(anyLong());
    }

    // POST test: add book should return BookResponseDTO when everything is valid
    @Test
    void addBookShouldReturnCorrectBookResponseDTOWhenValid() {
        // Assign
        // create requestDTO as argument for addBook()
        BookDTO request = new BookDTO();
        request.setTitle("Title");
        request.setAuthor("Author");
        request.setPrice(12.0);
        request.setStock(100);

        // create a book entity as the entity the repo return after saving newBook
        Book saved = new Book();
        saved.setId(10L);
        saved.setTitle("Title");
        saved.setAuthor("Author");
        saved.setPrice(12.0);
        saved.setStock(100);

        // mock the repo
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        // Act
        // call the service method to be tested: addBook()
        BookResponseDTO response = bookService.addBook(request);

        // Assert
        assertEquals(10L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Author", response.getAuthor());
        assertEquals(12.0, response.getPrice());
        assertEquals(100, response.getStock());

        // Verify that the repo methods being called
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    // POST test: add book should throw DataBaseException when database failed to save new book
    @Test
    void addBookShouldThrowDataBaseExceptionWhenSaveFails() {
        // Assign
        // create requestDTO as argument for addBook()
        BookDTO request = new BookDTO();
        request.setTitle("Title");
        request.setAuthor("Author");
        request.setPrice(12.0);
        request.setStock(100);

        when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("db error"));

        // Assert
        assertThrows(DatabaseException.class, () -> bookService.addBook(request));

        // Verify
        verify(bookRepository, times(1)).save(any(Book.class));
    }

}