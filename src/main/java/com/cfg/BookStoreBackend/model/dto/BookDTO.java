package com.cfg.BookStoreBackend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookDTO {
    // validate title must not be blank
    @NotBlank
    private String title;

    // validate author must not be blank
    @NotBlank
    private String author;

    // validate price must be >= 0
    @PositiveOrZero
    private Double price;

    // validate stock must be >= 0
    @PositiveOrZero
    private Integer stock;
}
