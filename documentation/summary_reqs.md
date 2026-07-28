# 📚 Project Summary & Requirements Specification

This document provides a comprehensive overview of the Bookstore Backend system application layer, its architectural requirements, and the technical strategies used to implement them.

---

## 1. Executive Application Summary

The **Bookstore Backend** is a lightweight, high-performance microservice built to manage a retail book inventory catalog. The application functions as a headless service (no front-end interface) exposing standard REST API endpoints. It allows corporate operators to safely ingest new book titles and retrieve individual catalog items.

The service handles incoming request structures, runs core business validation rules, automatically prints contextual execution logs, and persistently reads and writes data to a secure relational MySQL database tier.

---

## 2. System Requirements Matrix

### 2.1 Functional Requirements (FR)
*   **FR-1 (Data Schema)**: The application must store and track five core attributes for every book record: `Title` (Text string), `Author` (Text string), `ISBN` (Unique international tracking number string), `Price` (Decimal currency value), and `Stock` (Integer quantity value).
*   **FR-2 (Data Ingestion)**: The system must expose a `POST` API endpoint at `/api/books` to allow incoming payloads to create and append a new book record inside the persistent storage layer.
*   **FR-3 (Data Querying)**: The system must expose a `GET` API endpoint at `/api/books/{id}` to query and retrieve a single specific book entry using its automatically generated database primary tracking key (`id`).
*   **FR-4 (Input Validation)**: The service layer must automatically validate data bounds before updating tables. Book titles must not be empty or blank, and book prices must evaluate greater than or equal to a minimum configurable threshold floor.
*   **FR-5 (Error Masking)**: The application must catch validation issues or database missing exceptions gracefully, responding to clients with a uniform HTTP status code and a clean error message rather than standard raw server stack traces.

### 2.2 Non-Functional Requirements (NFR)
*   **NFR-1 (Observability)**: The application must write structural execution details to the terminal console (e.g., info logs on success, warn/error logs on exceptions) to make debugging straight-forward.
*   **NFR-2 (Isomorphic Architecture)**: Database entity instances must cleanly represent object-oriented models (a 'books' database table row explicitly translates into a single 'Book' Java object).
*   **NFR-3 (Containerization)**: The system must bundle all code execution dependencies into a single lightweight container image capable of running isolated alongside an ephemeral database service instance.

---

## 3. Technical Achievement Strategy

To fully satisfy the requirements outlined above while maintaining high quality, the project uses the following unified software stack engineering architecture:

| System Layer              | Technology Component         | Strategy Application Details                                                                                                             |
|:--------------------------|:-----------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------|
| **Core Framework**        | Spring Boot (v3.x) & Java 21 | Provides embedded Tomcat container layer, structured execution configuration management, and modern dependency injection.                |
| **Web Service API**       | Spring Web MVC Starter       | Exposes REST controllers mapped via uniform annotations to safely parse JSON payloads and dispatch semantic HTTP responses.              |
| **Data Persistence**      | Spring Data JPA (Hibernate)  | Eliminates tedious boilerplate SQL code. Connects structural Java objects straight to relational structures via automated repositories.  |
| **Database System**       | MySQL Database Server        | Provides a secure, persistent relational management layer to store books.                                                                |
| **Boilerplate Reduction** | Project Lombok               | Automatically generates constructor injections, builders, data transfer abstractions, and automated logging variables behind the scenes. |

---

## 4. Design & Modeling Artifacts

The comprehensive system architecture, database relational tracking bounds, and decoupled controller layer workflows were originally blueprinted and mapped visually inside **Figma** during the Week 1 requirements phase. A static rendering of this high-fidelity design layout is archived under `./documentation/images/backend-architecture.png`.
