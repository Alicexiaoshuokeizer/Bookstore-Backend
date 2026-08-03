package com.cfg.BookStoreBackend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
public class Book {
    // auto-increment primary key id column of book table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // column: title, it must not be blank
    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    // column: author, it must not be blank
    @NotBlank
    @Column(name = "author", nullable = false)
    private String author;

    // column: price, it must not be >= 0
    @PositiveOrZero
    @Column(name = "price", nullable = false)
    private  Double price;

    // column: price, it must not be >= 0
    @PositiveOrZero
    @Column(name = "stock", nullable = false)
    private Integer stock;
}
