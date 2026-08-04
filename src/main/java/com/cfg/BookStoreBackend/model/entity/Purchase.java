package com.cfg.BookStoreBackend.model.entity;

import com.cfg.BookStoreBackend.util.PurchaseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Data
@NoArgsConstructor
public class Purchase {

    // auto-increment primary key id column of the purchases table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // column: book_id, it must not be null, ManyToOne relationship foreign key, only load the data needed
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // column: customer_id, it must not be null
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // column: amount, must be >= 0
    @PositiveOrZero
    @Column(name = "amount", nullable = false)
    private Double amount;

    // column: date, it must not be null
    // the purchase data must be past or present date
    @NotNull
    @PastOrPresent
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    // column: status, it must not be null
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PurchaseStatus status = PurchaseStatus.PENDING;
}
