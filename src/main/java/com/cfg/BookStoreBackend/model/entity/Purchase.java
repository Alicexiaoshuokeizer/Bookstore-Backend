package com.cfg.BookStoreBackend.model.entity;

import com.cfg.BookStoreBackend.util.PurchaseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    public Purchase(
            Book book,
            Customer customer,
            int quantityOrdered,
            BigDecimal transactionPrice,
            LocalDateTime date,
            PurchaseStatus status) {

        this.book = book;
        this.customer = customer;
        this.quantity = quantityOrdered;
        this.transactionPrice = transactionPrice;
        this.date = date;
        this.status = status;
    }

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

    // column : quantity, it must be > 0
    @Positive
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // column: amount, must be >= 0, price can be 0 because there are case that some books are free
    // use BigDecimal for money to make sure decimal precision
    @PositiveOrZero
    @Column(name = "transaction_price", nullable = false)
    private BigDecimal transactionPrice;

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
