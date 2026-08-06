//package com.cfg.BookStoreBackend.service;
//
//import com.cfg.BookStoreBackend.exception.BookNotFoundException;
//import com.cfg.BookStoreBackend.exception.DatabaseException;
//import com.cfg.BookStoreBackend.model.entity.Book;
//import com.cfg.BookStoreBackend.model.entity.Purchase;
//import com.cfg.BookStoreBackend.model.repository.BookRepository;
//import com.cfg.BookStoreBackend.model.repository.PurchaseRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
// TODO:REFACTOR ACCORDING TO NEW PURCHASE ENTITY
//@ExtendWith(MockitoExtension.class)
//class PurchaseServiceTest {
//
//    @Mock
//    private PurchaseRepository purchaseRepository;
//
//    @Mock
//    private BookRepository bookRepository;
//
//    @InjectMocks
//    private PurchaseService purchaseService;
//
//    // REFUND test, processing a valid refund should change order status and increase book stock.
//    @Test
//    void refundPurchaseShouldIncrementStockAndChangeStatusWhenValid() throws DatabaseException {
//        // Arrange: Align status properties directly with your service text inputs
//        Purchase samplePurchase = new Purchase();
//        samplePurchase.setId(1L);
//        samplePurchase.setBookId(10L);
//        samplePurchase.setCustomerId(100L);
//        samplePurchase.setAmount(15.00);
//        samplePurchase.setStatus("CONFIRMED");
//
//        Book sampleBook = new Book();
//        sampleBook.setId(10L);
//        sampleBook.setTitle("The Hobbit");
//        sampleBook.setAuthor("J.R.R. Tolkien");
//        sampleBook.setPrice(15.00);
//        sampleBook.setStock(5);
//
//        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(samplePurchase));
//        when(purchaseRepository.save(any(Purchase.class))).thenReturn(samplePurchase);
//        when(bookRepository.findById(10L)).thenReturn(Optional.of(sampleBook));
//        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);
//
//        // Act
//        Purchase result = purchaseService.refundPurchase(1L);
//
//        // Assert: Visual Anchor: Expecting "REFUNDED" instead of "RETURN"
//        assertEquals("REFUNDED", result.getStatus());
//        assertEquals(6, sampleBook.getStock());
//
//        verify(purchaseRepository, times(1)).save(any(Purchase.class));
//        verify(bookRepository, times(1)).save(any(Book.class));
//    }
//
//    // REFUND test, refunding a non-existent purchase should throw a 404 style exception.
//    @Test
//    void refundPurchaseShouldThrowBookNotFoundExceptionWhenPurchaseIdIsMissing() {
//        when(purchaseRepository.findById(999L)).thenReturn(Optional.empty());
//
//        assertThrows(BookNotFoundException.class, () -> purchaseService.refundPurchase(999L));
//
//        verify(purchaseRepository, never()).save(any(Purchase.class));
//        verify(bookRepository, never()).save(any(Book.class));
//    }
//
//    // REFUND test, trying to refund an order that was already returned should throw a DatabaseException.
//    @Test
//    void refundPurchaseShouldThrowDatabaseExceptionWhenAlreadyRefunded() {
//        // Arrange: Expecting "REFUNDED" status text mismatch control
//        Purchase alreadyRefundedPurchase = new Purchase();
//        alreadyRefundedPurchase.setId(1L);
//        alreadyRefundedPurchase.setStatus("REFUNDED");
//
//        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(alreadyRefundedPurchase));
//
//        // Act & Assert
//        DatabaseException exception = assertThrows(
//                DatabaseException.class,
//                () -> purchaseService.refundPurchase(1L)
//        );
//
//        assertEquals("This purchase has already been fully refunded", exception.getMessage());
//
//        verify(purchaseRepository, never()).save(alreadyRefundedPurchase);
//        verify(bookRepository, never()).save(any(Book.class));
//    }
//
//    // REFUND test, purchase found but associated catalog item missing should yield a 404 style book target exception.
//    @Test
//    void refundPurchaseShouldThrowBookNotFoundExceptionWhenAssociatedBookIdIsMissing() { // 👈 Added extra test method
//        // Arrange
//        Purchase samplePurchase = new Purchase();
//        samplePurchase.setId(1L);
//        samplePurchase.setBookId(10L);
//        samplePurchase.setStatus("CONFIRMED");
//
//        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(samplePurchase));
//        when(purchaseRepository.save(any(Purchase.class))).thenReturn(samplePurchase);
//        when(bookRepository.findById(10L)).thenReturn(Optional.empty()); // Simulating catalog deletion scenario
//
//        // Act & Assert
//        BookNotFoundException exception = assertThrows(
//                BookNotFoundException.class,
//                () -> purchaseService.refundPurchase(1L)
//        );
//
//        assertEquals("Book not found with id: 10", exception.getMessage());
//
//        verify(purchaseRepository, times(1)).findById(1L);
//        verify(purchaseRepository, times(1)).save(any(Purchase.class));
//        verify(bookRepository, times(1)).findById(10L);
//        verify(bookRepository, never()).save(any(Book.class));
//    }
//}
