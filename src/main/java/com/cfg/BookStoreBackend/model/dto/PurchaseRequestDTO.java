package com.cfg.BookStoreBackend.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class PurchaseRequestDTO {

    // validate bookId, it must not be null
    @NotNull
    private Long bookId;

    // validate customerId, it must not be null
    @NotNull
    private Long customerId;

    @Positive
    private Integer quantity;

    // amount (total transaction_price) is calculated by server-side based on the quantity and the bookId(price)
    // date of purchase is defined by the server-side service when the purchase is actually written in db
    // status of purchase is not included in this DTO
    // because only server-side can update the status to avoid input pollution
}
