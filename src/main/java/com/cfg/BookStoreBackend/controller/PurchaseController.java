package com.cfg.BookStoreBackend.controller;

import com.cfg.BookStoreBackend.model.dto.PurchaseRequestDTO;
import com.cfg.BookStoreBackend.model.dto.PurchaseResponseDTO;
import com.cfg.BookStoreBackend.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("")
    public ResponseEntity<PurchaseResponseDTO> makePurchase(@Valid @RequestBody PurchaseRequestDTO requestDTO) {
        return ResponseEntity
                .status(201)
                .body(purchaseService.makePurchase(requestDTO));
    }

    @PutMapping("/{id}/refund")
    public ResponseEntity<PurchaseResponseDTO> refundPurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.refundPurchase(id));
    }
}
