package com.cfg.BookStoreBackend.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PurchaseRequestDTO {

    // validate bookId, it must not be null
    @NotNull
    private Long bookId;

    // validate customerId, it must not be null
    @NotNull
    private Long customerId;

    // validate amount, must be >= 0
    // use BigDecimal for money to make sure decimal precision
    @PositiveOrZero
    private Double amount;

    // date of purchase is defined by the server-side service when the purchase is actually written in db
    // status of purchase is not included in this DTO
    // because only server-side can update the status to avoid input pollution
}
