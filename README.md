# 📚 Bookstore Backend Project

This is a backend web service built with Java and Spring Boot. It manages a book shop catalog, tracks customers, and handles purchase orders.

---

## 🏗️ How the Project is Structured

We designed the structure of this application using **Figma** to make sure the parts of our code are cleanly separated.

### 🎨 Visual Architecture Diagram
![System Architecture Diagram](./documentation/images/backend-architecture.png)


## 🛠️ Tools You Need on Your Computer

*   **Java**: OpenJDK version 26
*   **Build Tool**: Maven version 3.9 or newer
*   **Containers**: Docker Desktop and Docker Compose
*   **Database**: MySQL version 8.4

### Configuration Settings (`application.yaml`)
The application uses these default names to talk to your computer systems. You can change them if needed:

| Setting Name | Default Value | What it is for |
|:---|:---|:---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/bookstore_db` | The connection address to find the MySQL database |
| `SPRING_DATASOURCE_USERNAME` | `root` | The user name to log into the database |
| `SPRING_DATASOURCE_PASSWORD` | *(Empty)* | The password to log into the database |
| `app.validation.minimum-price` | `0.0` | The lowest price allowed for a book |

---

## 🏃 How to Run the Project Locally

### Step 1: Create your environment file
1. Copy the sample file in your project folder to create a new file named `.env`:
   ```bash
   cp .env.example .env
   ```
2. Open the `.env` file in your text editor and type your database passwords:
   ```ini
   DB_USERNAME=root
   DB_PASSWORD=your_secure_password
   DB_ROOT_PASSWORD=your_secure_root_password
   ```

### Option A: Run everything automatically with Docker (Recommended)
This command downloads the database, builds your Java code, and automatically fills your system with **mock seed data** (15 books, 10 customers, and 20 sample sales):

```bash
# Stop old versions and start the clean application stack
docker compose down -v
docker compose up --build -d

# Check that your application is running
docker ps
```
Once it finishes, the application will be ready to test at `http://localhost:8080`.

### Option B: Run the database in Docker and the code manually
If you want to run the Java code directly on your computer instead of inside a container:

1.  Start only the MySQL database container:
    ```bash
    docker compose up -d mysql
    ```
2.  Start your Spring Boot application using the wrapper command:
    ```bash
    set -a
    source .env
    set +a

    ./mvnw spring-boot:run
    ```

---

## 🧪 How to Run Automated Tests
You can run our automated testing suite at any time. The tests use mock database tools, so you do **not** need a real database running on your computer for the tests to pass:

```bash
./mvnw clean test
```

---

## 📖 How to Test the API Endpoints in Your Browser

Once the application is running, you can view, read, and test every single endpoint using the interactive **Swagger UI** page in your web browser:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### Core API Endpoint Reference Table

| HTTP Action | API Path                     | What it does                           | Expected Result                |
|:------------|:-----------------------------|:---------------------------------------|:-------------------------------|
| **POST**    | `/api/books`                 | Add a new book to the store catalog    | `201 Created`                  |
| **GET**     | `/api/books`                 | Get a list of every book in the shop   | `200 OK`                       |
| **GET**     | `/api/books/{id}`            | Find a single book using its ID number | `200 OK` / `404 Not Found`     |
| **PUT**     | `/api/books/{id}`            | Edit the details of an existing book   | `200 OK` / `404 Not Found`     |
| **DELETE**  | `/api/books/{id}`            | Delete a book completely from the shop | `204 No Content`               |
| **POST**    | `/api/purchases`             | Buy a book (Fails if out of stock)     | `201 Created` / `409 Conflict` |
| **POST**    | `/api/purchases/{id}/refund` | Process a customer refund request      | `200 OK` / `409 Conflict`      |
| **POST**    | `/api/returns`               | Process a physical book return         | `200 OK` / `409 Conflict`      |
