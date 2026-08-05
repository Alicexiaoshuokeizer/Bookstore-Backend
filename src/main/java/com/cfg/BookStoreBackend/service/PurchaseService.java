package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.NotFoundException;
import com.cfg.BookStoreBackend.exception.DatabaseException;
import com.cfg.BookStoreBackend.model.entity.Book;
import com.cfg.BookStoreBackend.model.entity.Purchase;
import com.cfg.BookStoreBackend.model.repository.BookRepository;
import com.cfg.BookStoreBackend.model.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Purchase refundPurchase(Long id) {
        // 1. Fetching & Business Input Validation (Kept clean outside of infrastructure error catching)
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Refund failed: Purchase record not found with id: {}", id);
                    return new NotFoundException("Purchase record not found with id: " + id);
                });

        // Block duplicate refund triggers cleanly before starting database transaction writes
        if ("REFUNDED".equalsIgnoreCase(purchase.getStatus())) {
            log.warn("Refund rejected: Purchase id {} is already refunded", id);
            throw new DatabaseException("This purchase has already been fully refunded");
        }

        // 2. Pure Database Update & Save Operations
        try {
            purchase.setStatus("REFUNDED");
            Purchase updatedPurchase = purchaseRepository.save(purchase);

            // Locate corresponding inventory item to credit back stock allocation
            Book associatedBook = bookRepository.findById(purchase.getBookId())
                    .orElseThrow(() -> new NotFoundException("Book not found with id: " + purchase.getBookId()));

            associatedBook.setStock(associatedBook.getStock() + 1);
            bookRepository.save(associatedBook);

            log.info("Successfully processed refund for purchase ID {} and restocked item ID {}", id, associatedBook.getId());
            return updatedPurchase;

        } catch (NotFoundException ex) {
            // Bypass block: Catch and rethrow NotFoundException so it bypasses the 500 error catch-all below
            throw ex;
        } catch (Exception ex) {
            log.error("Database failure encountered while resolving refund ID {}: {}", id, ex.getMessage());
            throw new DatabaseException("Failed to update database records for this refund operation");
        }
    }
}

