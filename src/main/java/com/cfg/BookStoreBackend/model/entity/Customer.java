package com.cfg.BookStoreBackend.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
public class Customer {

    // auto-increment primary key id column of the customers table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // column: name, it must not be blank
    @NotBlank
    @Column(name = "name")
    private String name;

    // column: email, it must not be blank
    @NotBlank
    @Column(name = "email")
    private String email;

    // column: created_at, it must not be null
    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
