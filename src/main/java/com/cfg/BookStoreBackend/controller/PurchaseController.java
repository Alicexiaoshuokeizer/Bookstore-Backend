package com.cfg.BookStoreBackend.controller;


import com.cfg.BookStoreBackend.model.entity.Purchase;
import com.cfg.BookStoreBackend.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    // PUT /api/purchase/{id}/refund
    @PutMapping("/{id}/refund")
    public ResponseEntity<Purchase> refundPurchaseOrder(@PathVariable Long id) {
        Purchase processedRefund = purchaseService.refundPurchase(id);
        return ResponseEntity.ok(processedRefund);
    }
}
