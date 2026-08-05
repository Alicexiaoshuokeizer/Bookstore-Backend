package com.cfg.BookStoreBackend.model.dto;

import com.cfg.BookStoreBackend.model.entity.Purchase;
import com.cfg.BookStoreBackend.util.PurchaseStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PurchaseResponseDTO {

    private Long id;
    private Long bookId;
    private Long customerId;
    private Integer quantity;
    private BigDecimal amount;
    private LocalDateTime date;
    private PurchaseStatus status;

    public static PurchaseResponseDTO toResponseDTO(Purchase purchase) {
        // map purchase to PurchaseResponseDTO
        PurchaseResponseDTO dto = new PurchaseResponseDTO();
        dto.setId(purchase.getId());
        dto.setBookId(purchase.getBook().getId());
        dto.setCustomerId(purchase.getCustomer().getId());
        dto.setQuantity(purchase.getQuantity());
        dto.setAmount(purchase.getAmount());
        dto.setDate(purchase.getDate());
        dto.setStatus(purchase.getStatus());

        return dto;
    }

}
