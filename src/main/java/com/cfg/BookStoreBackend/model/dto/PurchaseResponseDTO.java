package com.cfg.BookStoreBackend.model.dto;

import com.cfg.BookStoreBackend.util.PurchaseStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class PurchaseResponseDTO {

    private Long id;
    private Long bookId;
    private Long customerId;
    private Double amount;
    private LocalDateTime date;
    private PurchaseStatus status;

}
