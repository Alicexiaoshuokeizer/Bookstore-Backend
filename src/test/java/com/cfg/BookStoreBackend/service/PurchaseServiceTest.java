package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.exception.DuplicateOperationException;
import com.cfg.BookStoreBackend.exception.NotFoundException;
import com.cfg.BookStoreBackend.model.dto.PurchaseResponseDTO;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.model.entity.Customer;
import com.cfg.BookStoreBackend.model.entity.Purchase;
import com.cfg.BookStoreBackend.model.repository.BookRepository;
import com.cfg.BookStoreBackend.model.repository.PurchaseRepository;
import com.cfg.BookStoreBackend.util.PurchaseStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO:REFACTOR ACCORDING TO NEW PURCHASE ENTITY
@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private PurchaseService purchaseService;

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

    // REFUND test, trying to refund an order that was already returned should throw a DatabaseException.
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
