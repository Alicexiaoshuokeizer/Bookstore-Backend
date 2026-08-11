-- ============================================================
-- Seed Data for bookstore_db
-- Run this AFTER the schema (CREATE TABLE) script has been applied.
-- ============================================================

USE bookstore_db;

-- Optional: clear existing data before reseeding (respects FK order)
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE purchases;
-- TRUNCATE TABLE books;
-- TRUNCATE TABLE customers;
-- SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- 1. Books
-- ------------------------------------------------------------
INSERT INTO books (title, author, price, stock) VALUES
    ('The Hobbit', 'J.R.R. Tolkien', 14.99, 120),
    ('1984', 'George Orwell', 9.99, 200),
    ('To Kill a Mockingbird', 'Harper Lee', 11.50, 85),
    ('Pride and Prejudice', 'Jane Austen', 8.75, 150),
    ('The Great Gatsby', 'F. Scott Fitzgerald', 10.25, 95),
    ('Brave New World', 'Aldous Huxley', 12.00, 60),
    ('Fahrenheit 451', 'Ray Bradbury', 9.50, 70),
    ('The Catcher in the Rye', 'J.D. Salinger', 10.99, 40),
    ('Moby-Dick', 'Herman Melville', 13.25, 30),
    ('War and Peace', 'Leo Tolstoy', 18.99, 25),
    ('Crime and Punishment', 'Fyodor Dostoevsky', 15.50, 45),
    ('The Alchemist', 'Paulo Coelho', 12.99, 110),
    ('Dune', 'Frank Herbert', 16.99, 75),
    ('Neuromancer', 'William Gibson', 11.75, 55),
    ('The Hitchhiker''s Guide to the Galaxy', 'Douglas Adams', 9.25, 90);

-- ------------------------------------------------------------
-- 2. Customers
-- ------------------------------------------------------------
INSERT INTO customers (name, email) VALUES
    ('Alice Johnson', 'alice.johnson@example.com'),
    ('Brian Smith', 'brian.smith@example.com'),
    ('Carla Martinez', 'carla.martinez@example.com'),
    ('David Lee', 'david.lee@example.com'),
    ('Emma Wilson', 'emma.wilson@example.com'),
    ('Farah Khan', 'farah.khan@example.com'),
    ('George Brown', 'george.brown@example.com'),
    ('Hannah Davis', 'hannah.davis@example.com'),
    ('Ivan Petrov', 'ivan.petrov@example.com'),
    ('Julia Nguyen', 'julia.nguyen@example.com');

-- ------------------------------------------------------------
-- 3. Purchases
-- transaction_price = books.price * quantity at time of purchase
-- status values: 'CONFIRMED', 'PENDING', 'RETURN', 'REFUNDED'
-- ------------------------------------------------------------
INSERT INTO purchases (book_id, customer_id, quantity, transaction_price, date, status) VALUES
    (1, 1, 2, 29.98, '2025-01-05 10:15:00', 'CONFIRMED'),
    (2, 2, 1, 9.99,  '2025-01-06 14:22:00', 'CONFIRMED'),
    (3, 3, 3, 34.50, '2025-01-08 09:05:00', 'CONFIRMED'),
    (4, 4, 1, 8.75,  '2025-01-10 16:40:00', 'PENDING'),
    (5, 5, 2, 20.50, '2025-01-12 11:30:00', 'CONFIRMED'),
    (6, 1, 1, 12.00, '2025-01-15 13:00:00', 'RETURN'),
    (7, 6, 4, 38.00, '2025-01-18 08:45:00', 'CONFIRMED'),
    (8, 7, 1, 10.99, '2025-01-20 17:10:00', 'REFUNDED'),
    (9, 8, 2, 26.50, '2025-01-22 12:05:00', 'PENDING'),
    (10, 9, 1, 18.99, '2025-01-25 15:55:00', 'CONFIRMED'),
    (11, 10, 3, 46.50, '2025-02-01 10:00:00', 'CONFIRMED'),
    (12, 2, 2, 25.98, '2025-02-03 09:30:00', 'REFUNDED'),
    (13, 3, 1, 16.99, '2025-02-05 14:15:00', 'RETURN'),
    (14, 4, 2, 23.50, '2025-02-07 11:20:00', 'CONFIRMED'),
    (15, 5, 5, 46.25, '2025-02-10 16:00:00', 'CONFIRMED'),
    (1, 6, 1, 14.99, '2025-02-12 10:10:00', 'PENDING'),
    (2, 7, 3, 29.97, '2025-02-14 13:45:00', 'CONFIRMED'),
    (3, 8, 1, 11.50, '2025-02-16 09:50:00', 'CONFIRMED'),
    (4, 9, 2, 17.50, '2025-02-18 12:30:00', 'CONFIRMED'),
    (5, 10, 1, 10.25, '2025-02-20 15:00:00', 'CONFIRMED');