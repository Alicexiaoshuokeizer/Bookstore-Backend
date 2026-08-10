package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.exception.DuplicateOperationException;
import com.cfg.BookStoreBackend.exception.NotFoundException;
import com.cfg.BookStoreBackend.exception.OutOfStockException;
import com.cfg.BookStoreBackend.model.dto.PurchaseRequestDTO;
import com.cfg.BookStoreBackend.model.dto.PurchaseResponseDTO;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.model.entity.Customer;
import com.cfg.BookStoreBackend.model.entity.Purchase;
import com.cfg.BookStoreBackend.model.repository.BookRepository;
import com.cfg.BookStoreBackend.model.repository.CustomerRepository;
import com.cfg.BookStoreBackend.model.repository.PurchaseRepository;
import com.cfg.BookStoreBackend.util.PurchaseStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    // MAKE PURCHASE test, processing a valid purchase request
    // should reduce book stock and return PurchaseResponseDTO with CONFIRMED status and price info
    @Test
    void makePurchaseShouldReduceBookStockAndReturnCorrectPurchaseStatusAndPriceInfoWhenValid() {
        // Arrange
        PurchaseRequestDTO request = new PurchaseRequestDTO();
        request.setBookId(10L);
        request.setCustomerId(15L);
        request.setQuantity(5);

        Book book = new Book();
        book.setId(10L);
        book.setStock(20); // stock > order quantity
        book.setPrice(1.0);
        book.setAuthor("Test Author");
        book.setTitle("Test Title");

        Customer customer = new Customer();
        customer.setId(15L);

        Purchase purchase = new Purchase(
                book,
                customer,
                request.getQuantity(),
                BigDecimal.valueOf(book.getPrice() * request.getQuantity()),
                LocalDateTime.now(),
                PurchaseStatus.CONFIRMED
        );

        when(customerRepository.findById(15L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

        // Act
        PurchaseResponseDTO response = purchaseService.makePurchase(request);

        // Assert
        assertEquals(PurchaseStatus.CONFIRMED, response.getStatus());
        assertEquals(BigDecimal.valueOf(5.0), response.getTransactionPrice());
        assertEquals(15L, response.getCustomerId());
        assertEquals(10L, response.getBookId());
        // book stock reduces accordingly
        assertEquals(15, book.getStock());

        verify(customerRepository,times(1)).findById(15L);
        verify(bookRepository, times(1)).findById(10L);
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    // MAKE PURCHASE test, processing an invalid purchase request with not-existing customer ID
    // should throw NotFoundException
    @Test
    void makePurchaseShouldThrowNotFoundWhenCustomerIdIsNotFound() {
        // Arrange
        PurchaseRequestDTO request = new PurchaseRequestDTO();
        request.setCustomerId(999L);

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> purchaseService.makePurchase(request));
        assertEquals("Customer not found with id: 999", ex.getMessage());

        verify(customerRepository,times(1)).findById(999L);
        verify(bookRepository, never()).findById(10L);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    // MAKE PURCHASE test, processing an invalid purchase request with not-existing book ID
    // should throw NotFoundException

    @Test
    void makePurchaseShouldThrowNotFoundWhenBookIdIsNotFound() {
        // Arrange
        PurchaseRequestDTO request = new PurchaseRequestDTO();
        request.setCustomerId(1L);
        request.setBookId(1000L);

        Customer customer = new Customer();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(1000L)).thenReturn(Optional.empty());

        // Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> purchaseService.makePurchase(request));
        assertEquals("Book not found with id: 1000", ex.getMessage());

        verify(customerRepository, times(1)).findById(1L);
        verify(bookRepository,times(1)).findById(1000L);
        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    // MAKE PURCHASE test, processing an invalid purchase request
    // with an order quantity > book stock
    // should throw OutOfStockException
    @Test
    void makePurchaseShouldThrowOutOfStockExceptionWhenInsufficientStock() {
        // Arrange
        PurchaseRequestDTO request = new PurchaseRequestDTO();
        request.setBookId(10L);
        request.setCustomerId(15L);
        request.setQuantity(50);

        Book book = new Book();
        book.setId(10L);
        book.setStock(20); // stock < order quantity
        book.setTitle("Title");

        Customer customer = new Customer();
        customer.setId(15L);

        when(customerRepository.findById(15L)).thenReturn(Optional.of(customer));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        // Assert: throws OutOfStockException and book stock has not changed as process rolls back
        assertThrows(OutOfStockException.class, () -> purchaseService.makePurchase(request));
        assertEquals(20, book.getStock());

        verify(customerRepository,times(1)).findById(15L);
        verify(bookRepository, times(1)).findById(10L);
    }

    // REFUND test, processing a valid refund should change order status
    @Test
    void refundPurchaseShouldChangeStatusWhenValid() throws DatabaseException {
        // Arrange: Align status properties directly with your service text inputs
        Book sampleBook = new Book();
        sampleBook.setId(10L);
        Customer sampleCustomer = new Customer();
        sampleCustomer.setId(100L);
        Purchase samplePurchase = new Purchase();
        samplePurchase.setId(1L);
        samplePurchase.setBook(sampleBook);
        samplePurchase.setCustomer(sampleCustomer);
        samplePurchase.setTransactionPrice(BigDecimal.valueOf(15.00));
        samplePurchase.setStatus(PurchaseStatus.CONFIRMED);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(samplePurchase));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(samplePurchase);

        // Act
        PurchaseResponseDTO result = purchaseService.refundPurchase(1L);

        // Assert: Visual Anchor: Expecting "REFUNDED"
        assertEquals(PurchaseStatus.REFUNDED, result.getStatus());

        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    // REFUND test, refunding a non-existent purchase should throw a 404 style exception.
    @Test
    void refundPurchaseShouldThrowNotFoundExceptionWhenPurchaseIdIsMissing() {
        when(purchaseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> purchaseService.refundPurchase(999L));

        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    // REFUND test, trying to refund an order that was already returned should throw a DuplicateOperationException.
    @Test
    void refundPurchaseShouldThrowDuplicateOperationExceptionWhenAlreadyRefunded() {
        // Arrange: Expecting "REFUNDED" status text mismatch control
        Purchase alreadyRefundedPurchase = new Purchase();
        alreadyRefundedPurchase.setId(1L);
        alreadyRefundedPurchase.setStatus(PurchaseStatus.REFUNDED);

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(alreadyRefundedPurchase));

        // Act & Assert
        DuplicateOperationException exception = assertThrows(
                DuplicateOperationException.class,
                () -> purchaseService.refundPurchase(1L)
        );

        assertEquals("This purchase has already been fully refunded", exception.getMessage());

        verify(purchaseRepository, never()).save(alreadyRefundedPurchase);
    }

}
