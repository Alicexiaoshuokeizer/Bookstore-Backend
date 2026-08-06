package com.cfg.BookStoreBackend.model.repository;

import com.cfg.BookStoreBackend.model.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Visual Anchor: Correctly positioned at the class-level
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    // Spring Data JPA automatically hooks up all CRUD methods here
}
