package com.cfg.BookStoreBackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookResponseDTO {

    private Long id;
    private String title;
    private String author;
    private Double price;
    private Integer stock;
}