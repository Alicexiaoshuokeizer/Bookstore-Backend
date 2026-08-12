# 🧪 Manual Testing Guide

This document is a step-by-step checklist to test the Bookstore application by hand. It helps you verify that our web links (endpoints) work correctly, save data accurately, and show friendly error messages when things go wrong.

---

## 1. Things to Do Before You Start Testing

Make sure you check these four setups on your computer before sending any test requests:

| Step   | Setup Target                | What to Do                                           | Expected Status                                                   |
|:-------|:----------------------------|:-----------------------------------------------------|:------------------------------------------------------------------|
| **01** | **Docker Containers**       | Run `docker compose up -d` in your project terminal. | The app and database containers start up cleanly.                 |
| **02** | **Database Tables**         | Check your schema files or use MySQL Workbench.      | The tables (`books`, `customers`, `purchases`) exist.             |
| **03** | **Sample Data**             | Check that `seed.sql` loaded automatically.          | 15 books, 10 customers, and 20 sales appear in the DB.            |
| **04** | **Application Web Address** | Open your browser and check the server.              | The web service is live and listening at `http://localhost:8080`. |

---

## 2. API Endpoint Testing Table

| Test ID    | Web Address (Endpoint)       | HTTP Action | Data You Need to Input                                                                        | Expected Result Code | Expected Response Message                                                                            |
|:-----------|:-----------------------------|:------------|:----------------------------------------------------------------------------------------------|:---------------------|:-----------------------------------------------------------------------------------------------------|
| **TP-001** | `/api/books`                 | `POST`      | Valid Book: `{"title": "Clean Code", "author": "Robert Martin", "price": 35.99, "stock": 10}` | `201 Created`        | You get back the complete book details along with its new ID number (like `"id": 16`).               |
| **TP-002** | `/api/books`                 | `POST`      | Empty Title space: `{"title": " ", "author": "Robert Martin", "price": 35.99, "stock": 10}`   | `400 Bad Request`    | The system blocks the save and displays an input validation message.                                 |
| **TP-003** | `/api/books/{id}`            | `GET`       | Look for an existing book: `/api/books/1`                                                     | `200 OK`             | You see the details for *The Hobbit* with a price of `14.99` and stock of `120`.                     |
| **TP-004** | `/api/books/{id}`            | `GET`       | Look for a missing book ID: `/api/books/999`                                                  | `404 Not Found`      | You see a clear text message error: `"Book not found with id: 999"`.                                 |
| **TP-005** | `/api/purchases`             | `POST`      | Buy an available book: `{"bookId": 1, "customerId": 1, "quantity": 2}`                        | `201 Created`        | You see a confirmed order with a total price calculation of `29.98`.                                 |
| **TP-006** | `/api/purchases`             | `POST`      | Buy too many copies: `{"bookId": 10, "customerId": 1, "quantity": 500}`                       | `409 Conflict`       | The system blocks the sale and says: `"Book does not have sufficient stock, title: War and Peace"`.  |
| **TP-007** | `/api/purchases/{id}/refund` | `POST`      | Request a refund for the first time: `/api/purchases/1/refund`                                | `200 OK`             | The purchase record updates its status text field to say `"REFUNDED"`.                               |
| **TP-008** | `/api/purchases/{id}/refund` | `POST`      | Request a second refund for the same ID: `/api/purchases/1/refund`                            | `409 Conflict`       | The system blocks the duplicate request and says: `"This purchase has already been fully refunded"`. |
| **TP-009** | `/api/returns`               | `POST`      | Return a borrowed book: `{"purchaseId": 6}`                                                   | `200 OK`             | The purchase updates to `"RETURN"` status, and the book stock increases automatically.               |

---

## 3. How to Execute These Tests

### Method A: Testing inside your Web Browser (Easiest)
1. Open your web browser and navigate to: `http://localhost:8080/swagger-ui/index.html`.
2. Click on the endpoint row you want to test (like `POST /api/purchases`).
3. Click the silver **"Try it out"** button on the right side.
4. Copy and paste the example data from the testing table into the text box.
5. Click the big blue **"Execute"** button and check the response data at the bottom.

### Method B: Testing using your Terminal (cURL)
Open your standard computer terminal and run these commands directly to test errors:

```bash
# Test buying a book that is out of stock (TP-006)
curl -X POST http://localhost:8080/api/purchases \
  -H "Content-Type: application/json" \
  -d '{"bookId": 10, "customerId": 1, "quantity": 500}'

# Test a duplicate refund error block (TP-008)
curl -X POST http://localhost:8080/api/purchases/1/refund
```
