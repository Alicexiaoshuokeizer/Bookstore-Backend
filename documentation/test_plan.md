# 🧪 Manual Test Plan Matrix

This document establishes the comprehensive manual verification matrix used to validate the functional integrity, validation parameters, and error handling boundaries of the Bookstore Backend.

---

## 1. Test Environment Pre-conditions

Before executing the test scenarios below, ensure that:
1. The Docker orchestration suite is up and running via `docker-compose up -d`.
2. The database schema has been verified using MySQL Workbench or via the raw initialization script execution.
3. The server application layer is live and responding at base address `http://localhost:8080`.

---

## 2. Functional Test Verification Suite

| Test ID    | Target Endpoint   | HTTP Method | Input Payload Conditions                                                                                                     | Expected Status Code | Expected Body Response                                                                              |
|:-----------|:------------------|:------------|:-----------------------------------------------------------------------------------------------------------------------------|:---------------------|:----------------------------------------------------------------------------------------------------|
| **TP-001** | `/api/books`      | `POST`      | Valid JSON: `{"title": "Clean Code", "author": "Robert Martin", "isbn": "9780132350884", "price": 35.99, "stock": 10}`       | `201 Created`        | Returns the populated Book JSON payload along with its system-allocated ID (e.g., `"id": 1`).       |
| **TP-002** | `/api/books`      | `POST`      | Blank Title: `{"title": "", "author": "Robert Martin", "isbn": "9780132350884", "price": 35.99, "stock": 10}`                | `400 Bad Request`    | Text body error message: `"Book title cannot be blank."`                                            |
| **TP-003** | `/api/books`      | `POST`      | Negative Price: `{"title": "Effective Java", "author": "Joshua Bloch", "isbn": "9780134685991", "price": -5.00, "stock": 5}` | `400 Bad Request`    | Text body error message: `"Book price must evaluate greater than or equal to configuration floor."` |
| **TP-004** | `/api/books/{id}` | `GET`       | Existing Record Path: `/api/books/1`                                                                                         | `200 OK`             | The matching complete Book record JSON payload matching database states.                            |
| **TP-005** | `/api/books/{id}` | `GET`       | Missing Record Path: `/api/books/999`                                                                                        | `404 Not Found`      | Custom exception message text payload: `"Book with tracking ID 999 not found."`                     |

---

## 3. Execution & Verification Guide

### 3.1 Verification using the Integrated OpenAPI Playground

1. Launch a browser page and navigate to your interactive UI suite at: `http://localhost:8080/swagger-ui/index.html`.
2. Locate the specific target method endpoint header banner (`POST` or `GET`) and click it open.
3. Click the **"Try it out"** functional status toggle on the right section.
4. Input the specific target payload or path variable arguments listed in the verification matrix above.
5. Click **"Execute"** and evaluate that the returned Server Response code and payload values accurately match the expected criteria.

### 3.2 Verification via Command-Line Terminal (cURL alternate fallback)

Execute clean cURL operations directly to assert the error boundary parsing functions:

```bash
# Verify execution of test scenario TP-003 (Negative Price Block)
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title": "Test", "author": "Author", "isbn": "111222", "price": -1.0, "stock": 1}'
```
