package com.cfg.BookStoreBackend.model.dto;

import com.cfg.BookStoreBackend.model.entity.Purchase;
import com.cfg.BookStoreBackend.util.PurchaseStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReturnBookResponseDTO {
    // show information to confirm details of books
    // and purchase after the purchased books are returned

    // purchase info
    private Long purchaseId;
    private Long customerId;
    private Integer quantity;
    private PurchaseStatus status;

    // book info
    private Long bookId;
    private String title;
    private String author;
    private Integer updatedStock;

    // method
    public static ReturnBookResponseDTO toResponseDTO(Purchase purchase) {
        ReturnBookResponseDTO dto = new ReturnBookResponseDTO();
        // purchase info
        dto.setPurchaseId(purchase.getId());
        dto.setCustomerId(purchase.getCustomer().getId());
        dto.setQuantity(purchase.getQuantity());
        dto.setStatus(purchase.getStatus());

        // book info
        dto.setBookId(purchase.getBook().getId());
        dto.setTitle(purchase.getBook().getTitle());
        dto.setAuthor(purchase.getBook().getAuthor());
        dto.setUpdatedStock(purchase.getBook().getStock());

        return dto;
    }

}
