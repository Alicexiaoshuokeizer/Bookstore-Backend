package com.cfg.BookStoreBackend.controller;

import com.cfg.BookStoreBackend.model.dto.PurchaseRequestDTO;
import com.cfg.BookStoreBackend.model.dto.PurchaseResponseDTO;
import com.cfg.BookStoreBackend.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping("/api/purchases")
    public ResponseEntity<PurchaseResponseDTO> makePurchase(@Valid @RequestBody PurchaseRequestDTO requestDTO) {
        return ResponseEntity
                .status(201)
                .body(purchaseService.makePurchase(requestDTO));
    }
}
