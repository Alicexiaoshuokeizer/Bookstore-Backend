package com.cfg.BookStoreBackend.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReturnBookRequestDTO {

    // must provide a purchase id to trace purchase details for further process
    @NotNull
    private Long purchaseId;
}
