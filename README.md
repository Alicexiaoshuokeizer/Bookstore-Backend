# Bookstore Backend

## Project Structure
```
com/group/bookstore/
├── controller/      → REST endpoints (@RestController)
├── service/         → Business logic (@Service) interface + impl
├── model/
│   ├── entity/       → Book, Purchase, Customer etc. (@Entity)
│   ├── repository/   → BookRepository, PurchaseRepository, CustomerRepository (@Repository)
│   └── dto/          → BookDto, PurchaseDto,ReturnBookDto, etc.
└── util/            → Helper class
```