-- 1. Create the Database Sandbox Namespace
CREATE DATABASE IF NOT EXISTS bookstore_db;
USE bookstore_db;

-- 2. Inventory Catalog Blueprint Table
CREATE TABLE IF NOT EXISTS books (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL
    );

-- 3. Consumer Data Registry Table
CREATE TABLE IF NOT EXISTS customers (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 4. Transaction Order Lifecycle Table
CREATE TABLE IF NOT EXISTS purchases (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         book_id BIGINT NOT NULL,
                                         customer_id BIGINT NOT NULL,
                                      -- quantity: Tracks how many copies are ordered
                                         quantity INT NOT NULL,
                                      -- transaction_price: The final total price paid for the transaction.
                                         transaction_price DECIMAL(10,2) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT
    );
