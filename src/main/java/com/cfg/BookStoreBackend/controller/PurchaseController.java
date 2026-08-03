package com.cfg.BookStoreBackend.controller;

import com.cfg.BookStoreBackend.model.entity.Purchase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PurchaseController {

    @PostMapping("/api/purchases")
    public ResponseEntity<Purchase> makePurchase() {
        return null;
    }
}
