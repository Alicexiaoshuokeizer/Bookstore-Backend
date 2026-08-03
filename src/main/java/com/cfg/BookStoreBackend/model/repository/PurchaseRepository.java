package com.cfg.BookStoreBackend.model.repository;

import com.cfg.BookStoreBackend.model.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
