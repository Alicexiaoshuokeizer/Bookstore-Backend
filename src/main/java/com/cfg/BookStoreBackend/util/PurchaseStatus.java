package com.cfg.BookStoreBackend.util;

public enum PurchaseStatus {
    // PENDING: not paid
    PENDING,
    // CONFIRMED: paid, waiting for shipping
    CONFIRMED,
    // RETURN: book returned
    RETURN
}
