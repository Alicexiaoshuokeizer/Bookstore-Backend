package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.exception.DuplicateOperationException;
import com.cfg.BookStoreBackend.exception.NotFoundException;
import com.cfg.BookStoreBackend.model.dto.BookDTO;
import com.cfg.BookStoreBackend.model.dto.BookResponseDTO;
import com.cfg.BookStoreBackend.model.dto.ReturnBookRequestDTO;
import com.cfg.BookStoreBackend.model.dto.ReturnBookResponseDTO;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.model.entity.Customer;
import com.cfg.BookStoreBackend.model.entity.Purchase;
import com.cfg.BookStoreBackend.model.repository.BookRepository;
import com.cfg.BookStoreBackend.model.repository.PurchaseRepository;
import com.cfg.BookStoreBackend.util.PurchaseStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @Mock
    private PurchaseRepository purchaseRepository;

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


    // POST test
    // when valid return book should restock book and change purchase status to PurchaseStatus.RETURN
    @Test
    void returnBookShouldRestockBookAndChangePurchaseStatusWhenValid() {
        // Assign
        // create returnBook argument
        ReturnBookRequestDTO request = new ReturnBookRequestDTO();
        request.setPurchaseId(1L);

        // create book entity the repo returns when findById
        Book foundBook = new Book();
        foundBook.setId(10L);
        foundBook.setStock(95);

        // create purchase entity the repo returns when findById
        Purchase foundPurchase = new Purchase();
        foundPurchase.setId(1L);
        foundPurchase.setQuantity(5);
        foundPurchase.setBook(foundBook);
        foundPurchase.setStatus(PurchaseStatus.CONFIRMED);

        // create book entity the repo returns when save() is called
        Book updatedBook = new Book();
        updatedBook.setId(10L);
        updatedBook.setStock(100);
        updatedBook.setTitle("Title");
        updatedBook.setAuthor("Author");

        // create customer entity used in the returned purchase entity after save() via purchaseRepository
        Customer customer = new Customer();
        customer.setId(100L);

        // create purchase entity the repo returns when save() is called
        Purchase updatedPurchase = new Purchase();
        updatedPurchase.setId(1L);
        updatedPurchase.setQuantity(5);
        updatedPurchase.setCustomer(customer);
        updatedPurchase.setBook(foundBook);
        updatedPurchase.setStatus(PurchaseStatus.RETURN);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(foundPurchase));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(updatedPurchase);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(foundBook));

        // Act
        ReturnBookResponseDTO responseDTO = bookService.returnBook(request);

        // Assert & Verify
        // capture the arguments that are passed to repo methods
        // to verify whether business logic is implemented to the argument successfully/correctly
        ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
        verify(purchaseRepository, times(1)).save(purchaseCaptor.capture());
        verify(bookRepository, times(1)).findById(10L);
        verify(purchaseRepository, times(1)).findById(1L);

        Purchase purchaseToSave = purchaseCaptor.getValue();

        // book restock to 95(old stock) + 5(purchase return quantity) = 100(new stock)
        // business logic applies to foundBook, stock increase correctly
        assertEquals(100, purchaseToSave.getBook().getStock());
        // response get updated new book stock correctly
        assertEquals(100,responseDTO.getUpdatedStock());
        // business logic applies to purchaseToSave, status changed to RETURN
        assertEquals(PurchaseStatus.RETURN, purchaseToSave.getStatus());
        // response status changed to RETURN correctly
        assertEquals(PurchaseStatus.RETURN, responseDTO.getStatus());
    }

    // POST test
    // given a non-existing purchase ID, return book should throw NotFoundException
    @Test
    void returnBookShouldThrowNotFoundExceptionGivenNonExistingPurchaseID() {
        // Assign
        // create returnBook argument
        ReturnBookRequestDTO request = new ReturnBookRequestDTO();
        request.setPurchaseId(1L);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookService.returnBook(request));

        // Verify
        verify(purchaseRepository, times(1)).findById(1L);
        verifyNoInteractions(bookRepository);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    // POST test
    // given an invalid book ID, return book should throw NotFoundException
    // current database design has a constraint: on delete restrict
    // on foreign key book id in purchases table
    // theoretically, book not found will not happen,
    // service still get book not found gated to prevent potential error
    @Test
    void returnBookShouldThrowsNotFoundExceptionsGivenNonExistingBookID() {
        // Assign
        // create returnBook argument
        ReturnBookRequestDTO request = new ReturnBookRequestDTO();
        request.setPurchaseId(1L);

        Book potentialBook = new Book();
        potentialBook.setId(10L);

        Purchase foundPurchase = new Purchase();
        foundPurchase.setId(1L);
        foundPurchase.setBook(potentialBook);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(foundPurchase));
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(NotFoundException.class, () -> bookService.returnBook(request));
        assertEquals("ReturnBook--book not found for with id: 10", exception.getMessage());

        // Verify
        verify(purchaseRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).findById(10L);
        verify(bookRepository,never()).save(any(Book.class));
        verify(purchaseRepository,never()).save(any(Purchase.class));
    }

    // POST test
    // given a purchase that already got book return processed,
    // return book should throw DuplicateOperationException
    // all process rolls back, no entity changes
    @Test
    void returnBookShouldThrowDuplicateOperationExceptionWhenAlreadyReturned() {
        // Assign
        // create returnBook argument
        ReturnBookRequestDTO request = new ReturnBookRequestDTO();
        request.setPurchaseId(1L);

        Purchase foundPurchase = new Purchase();
        foundPurchase.setId(1L);
        foundPurchase.setStatus(PurchaseStatus.RETURN);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(foundPurchase));

        // Act & Assert
        Exception exception = assertThrows(DuplicateOperationException.class, () -> bookService.returnBook(request));
        assertEquals("Books of purchase id: 1 has already been restocked", exception.getMessage());

        // Verify
        verify(purchaseRepository, times(1)).findById(1L);
        verifyNoInteractions(bookRepository);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    // POST test
    // when fails to save updated purchase
    // return purchase should throw DatabaseException
    @Test
    void returnBookShouldThrowDatabaseExceptionWhenFailsToSavePurchase() {
        // Assign
        // create returnBook argument
        ReturnBookRequestDTO request = new ReturnBookRequestDTO();
        request.setPurchaseId(1L);

        // create book entity the repo returns when findById
        Book foundBook = new Book();
        foundBook.setId(10L);
        foundBook.setStock(95);

        // create purchase entity the repo returns when findById
        Purchase foundPurchase = new Purchase();
        foundPurchase.setId(1L);
        foundPurchase.setQuantity(5);
        foundPurchase.setBook(foundBook);
        foundPurchase.setStatus(PurchaseStatus.CONFIRMED);

        // create book entity the repo returns when save() is called
        Book updatedBook = new Book();
        updatedBook.setId(10L);
        updatedBook.setStock(100);
        updatedBook.setTitle("Title");
        updatedBook.setAuthor("Author");

        // create customer entity used in the returned purchase entity after save() via purchaseRepository
        Customer customer = new Customer();
        customer.setId(100L);

        // create purchase entity the repo returns when save() is called
        Purchase updatedPurchase = new Purchase();
        updatedPurchase.setId(1L);
        updatedPurchase.setQuantity(5);
        updatedPurchase.setCustomer(customer);
        updatedPurchase.setBook(foundBook);
        updatedPurchase.setStatus(PurchaseStatus.RETURN);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(foundPurchase));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(foundBook));
        when(purchaseRepository.save(any(Purchase.class))).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(DatabaseException.class, () -> bookService.returnBook(request));

        // Verify
        verify(purchaseRepository,times(1)).findById(1L);
        verify(bookRepository,times(1)).findById(10L);
        // the real "no rollback needed because we never got there" check
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

}