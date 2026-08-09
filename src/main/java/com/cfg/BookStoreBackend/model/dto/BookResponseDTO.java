package com.cfg.BookStoreBackend.model.dto;

import com.cfg.BookStoreBackend.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

// used for every response the API sends back (POST and GET)
// keeps the API contract separate from the Book entity/table structure,
// so changing the database later doesn't automatically change the API
@Data
@AllArgsConstructor
public class BookResponseDTO {
    private Long id;
    private String title;
    private String author;
    private Double price;
    private Integer stock;

    // converts a Book entity into the shape the API actually sends back
    public static BookResponseDTO fromEntity(Book book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getStock()
        );
    }
}