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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// indicate this is a service component for spring
@Service
// log info for debugging and tracking
@Slf4j
// constructor injection via lombok to indicate dependency of any final field variables
@RequiredArgsConstructor
public class PurchaseService {
    // fields
    // create purchase, customer and book repository for server to communicate with db
    private final PurchaseRepository purchaseRepository;
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;

  // methods
    // add new purchase to purchases table via purchaseRepository
    // if success, returns purchaseResponseDTO class object of the newly added purchase
    // if fails, rollback whole process and throw Exception
    @Transactional
    public PurchaseResponseDTO makePurchase(PurchaseRequestDTO requestDTO)
            throws NotFoundException, OutOfStockException, DatabaseException
    {
        // verify customer id
        Customer customer = customerRepository
                .findById(requestDTO.getCustomerId())
                .orElseThrow(()-> {
                    log.warn("New purchase failed: customer not found with id: {}", requestDTO.getCustomerId());
                    return new NotFoundException("Customer not found with id: " + requestDTO.getCustomerId());
                });

        // verify book id
        Book book = bookRepository
                .findById(requestDTO.getBookId())
                .orElseThrow(() -> {
                    log.warn("New purchase failed: book not found with id: {}", requestDTO.getBookId());
                    return new NotFoundException("Book not found with id: " + requestDTO.getBookId());
                });

        // verify quantity to be ordered is <= stock in book
        int quantityOrdered = requestDTO.getQuantity();
        if (book.getStock() < quantityOrdered) {
            log.warn("New purchase failed: stock insufficiency book id: {}, quantity ordered: {}", requestDTO.getBookId(), quantityOrdered);
            throw new OutOfStockException("Book does not have sufficient stock, title:"+ book.getTitle());
        }

        // reduce book stock by 1
        book.setStock(book.getStock() - quantityOrdered);

        // make purchase entity instance for db storing process
        Purchase purchase = new Purchase(
                book,
                customer,
                quantityOrdered,
                // use BigDecimal.valueOf temporally to address book price is Double type
                // change it when book price data type is changed to BigDecimal
                BigDecimal.valueOf(book.getPrice() * quantityOrdered),
                LocalDateTime.now(),
                PurchaseStatus.CONFIRMED
        );

        // store new purchase in purchases table
        // convert saved purchase to purchaseResponseDTO and return it
        try {
            Purchase saved = purchaseRepository.save(purchase);
            log.info("New purchase save successfully with id: {}", saved.getId());
            return PurchaseResponseDTO.toResponseDTO(saved);
        }
        catch (Exception e) {
            log.error("Failed to save purchase to db: {}", e.getMessage());
            throw new DatabaseException("Failed to saved purchase");
        }
    }


    @Transactional
    public PurchaseResponseDTO refundPurchase(Long id)
            throws NotFoundException, DuplicateOperationException, DatabaseException
    {
        // 1. Fetching & Business Input Validation (Kept clean outside of infrastructure error catching)
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Refund failed: Purchase record not found with id: {}", id);
                    return new NotFoundException("Purchase record not found with id: " + id);
                });

        // Block duplicate refund triggers cleanly before starting database transaction writes
        if (purchase.getStatus() == PurchaseStatus.REFUNDED) {
            log.warn("Refund rejected: Purchase id {} is already refunded", id);
            throw new DuplicateOperationException("This purchase has already been fully refunded");
        }

        // 2. Pure Database Update & Save Operations
        try {
            purchase.setStatus(PurchaseStatus.REFUNDED);
            Purchase updatedPurchase = purchaseRepository.save(purchase);
            log.info("Successfully processed refund for purchase ID {}", id);
            return PurchaseResponseDTO.toResponseDTO(updatedPurchase);
        } catch (Exception ex) {
            log.error("Database failure encountered while resolving refund ID {}: {}", id, ex.getMessage());
            throw new DatabaseException("Failed to update database records for this refund operation");
        }
    }
}
