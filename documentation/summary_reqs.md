# 📚 Project Summary & Rules Specification

This document explains what the Bookstore project does, who it is for, and the simple functional rules it must follow.

---

## 1. What is this Project?

The **Bookstore Backend** is a lightweight engine built to manage an online bookshop's inventory catalog and track customer sales orders. This application does not have a visual front-end user interface. Instead, it works entirely behind the scenes by exposing clean web links (REST APIs).

Other developers (like front-end web developers or mobile app creators) can link their buttons to our service to allow users to add new books, check available stock, buy items, return items, or request cash refunds.

---

## 2. Basic Requirements Check

### 2.1 Functional Requirements (What the system must do)
*   **FR-1 (Book Information)**: The system must store five details for every book entry: an ID tracking number, a title, an author name, a price, and an available stock count. No missing or imaginary fields (like ISBN) are used.
*   **FR-2 (Order Lifecycle)**: Every purchase order must track its current lifecycle status using simple words: `PENDING`, `CONFIRMED`, `RETURN`, or `REFUNDED`. It must use high-precision decimals (`BigDecimal`) to track financial totals so currency calculation math is never wrong.
*   **FR-3 (Catalog Changes)**: Operators must be able to add new books (`POST`), view all books (`GET`), edit book details (`PUT`), and delete books (`DELETE`) using the `/api/books` routes.
*   **FR-4 (Sales and Service Operations)**: Customers must be able to buy books (`POST /api/purchases`), ask for refunds (`POST /api/purchases/{id}/refund`), and log bookstore returns (`POST /api/returns`).
*   **FR-5 (Inventory and Fraud Protection)**: The system must protect the shop catalog. If a book has 0 stock, it must block the purchase. If a purchase was already refunded, it must block a second refund attempt.
*   **FR-6 (Friendly Error Handling)**: If a user types a wrong ID or requests an out-of-stock item, the app must show a clean, friendly text message with a clear code status (`404` or `409`) instead of crashing or showing ugly server code stack traces.

### 2.2 Non-Functional Requirements (How the system behaves)
*   **NFR-1 (Clear Logging)**: The application must print status updates to the terminal screen (like info logs for success, and warn/error logs for failures) so developers can track actions instantly.
*   **NFR-2 (Fail-Safe Code Execution)**: If an operation updates two database tables at the same time (like increasing book stock while changing an order status) and the second step fails, the system must undo the first step automatically (`@Transactional`) so data never gets corrupted.
*   **NFR-3 (Easy Setup)**: The entire codebase must be packageable inside a single container script (Docker) so that any developer can start the application and database together instantly.

---

## 3. Visual Layout Reference

All core architectural designs, database relationship maps, and controller path workflows were blueprinted visually inside **Figma** during the early planning phase. The file layout can be viewed inside our team's workspace folder design assets directory.
