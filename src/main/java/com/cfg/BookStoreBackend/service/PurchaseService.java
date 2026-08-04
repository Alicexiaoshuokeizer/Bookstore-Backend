package com.cfg.BookStoreBackend.service;

import com.cfg.BookStoreBackend.exception.BookNotFoundException;
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
@Slf4j // Lombok automatically builds the background logger engine securely now
@RequiredArgsConstructor // Automatically creates the constructor for your final repositories
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final BookRepository bookRepository;

    @Transactional // Ensures status tracking changes and book restocking succeed atomically
    public Purchase refundPurchase(Long id) {
        // Locate purchase order tracker.
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Refund failed: Purchase record not found with id: {}", id);
                    return new BookNotFoundException(id);
                });

        // Block duplicate refund triggers
        if ("REFUNDED".equalsIgnoreCase(purchase.getStatus())) {
            log.warn("Refund rejected: Purchase id {} is already refunded", id);
            throw new DatabaseException("This purchase has already been fully refunded");
        }

        try {
            purchase.setStatus("REFUNDED");
            Purchase updatedPurchase = purchaseRepository.save(purchase);

            // Locate corresponding inventory item to credit back stock allocation
            Book associatedBook = bookRepository.findById(purchase.getBookId())
                    .orElseThrow(() -> new BookNotFoundException(purchase.getBookId()));

            associatedBook.setStock(associatedBook.getStock() + 1);
            bookRepository.save(associatedBook);

            log.info("Successfully processed refund for purchase ID {} and restocked item ID {}", id, associatedBook.getId());
            return updatedPurchase;

        } catch (Exception ex) {
            log.error("Database failure encountered while resolving refund ID {}: {}", id, ex.getMessage());
            throw new DatabaseException("Failed to update database records for this refund operation");
        }
    }
}

