# 🚀 App Deployment & Automation Pipeline Guide

This document explains how our code automatically builds, tests, and deploys onto cloud servers every time a developer merges changes into the core `main` branch on GitHub.

---

## 1. Cloud Server Setup

Our target live cloud environment splits our application parts into separate, safe network spaces to keep our data secure:

*   **Application Server**: Our containerized Java application runs inside an elastic orchestration environment (like AWS ECS or Kubernetes). It scales up automatically when traffic gets heavy and sits safely behind a public Load Balancer.
*   **Database Isolation Node**: The MySQL server runs inside a strictly confidential private subnet. It cannot talk to the public internet directly and only answers connection requests sent from our application container.
*   **Isolated Network Bridge**: All internal data traffic travels across an isolated virtual bridge network mapping (`bookstore-network`), matching our local Docker Compose setup.

---

## 2. Automated Build Sequence (CI/CD Pipeline)

Our build server executes five automated stages in a straight line every single time new code is pushed. If a single step fails, the pipeline stops instantly and blocks deployment to keep bugs off the server.

### 2.1 Visual Automation Mapping
Our complete user experience wireframes, infrastructure topologies, and automated pipeline step layouts are mapped visually inside our team design space.

---

## 3. Detailed Build Stage Breakdown

### 📊 Stage 1: Code Verification
*   **Trigger**: Code is merged into the `main` branch.
*   **What it does**: The system reviews the files to ensure everyone followed correct formatting guidelines, style rules, and folder structure parameters.
*   **Pass Condition**: Zero code styling mistakes found.

### 🧪 Stage 2: Automated Testing
*   **Trigger**: Verification stage passes successfully.
*   **What it does**: Runs our entire testing suite automatically using the framework command:
    ```bash
    ./mvnw clean test
    ```
    *Note: Our application startup tests use isolated mock repository beans. This allows the testing phase to pass completely without needing an active database server running on the build cloud.*
*   **Pass Condition**: **100% of all written tests must pass successfully**. A single test failure blocks the deployment immediately.

### 📦 Stage 3: Code Packaging
*   **Trigger**: 100% of tests pass cleanly.
*   **What it does**: Compiles the source files into a single, production-ready runnable package bundle (a fat `.jar` file) inside the project target directory:
    ```bash
    ./mvnw clean package -DskipTests
    ```
*   **Pass Condition**: The runnable package builds successfully without syntax compilation errors.

### 🐳 Stage 4: Container Construction
*   **Trigger**: Packaging stage completes successfully.
*   **What it does**: Reads our project `Dockerfile`, downloads a safe Java base layer image, inserts our runnable package, and builds a lightweight production container image.
*   **Pass Condition**: The built container image registers successfully inside our secure cloud registry.

### 🚀 Stage 5: Live Server Deployment
*   **Trigger**: Container image is securely registered.
*   **What it does**: Triggers a rolling update on the cloud host. The server pulls down the new container version, starts up the updated code layers, checks that they are healthy, and safely deletes the old outdated versions.
*   **Active Route Verification**: Confirms that operational web traffic maps perfectly to the updated database contracts:
  *   Book Sales: `POST /api/purchases`
  *   Refund Steps: `POST /api/purchases/{id}/refund`
  *   Book Returns: `POST /api/returns`
*   **Pass Condition**: The live server returns a clean success status code via its internal health actuator path.
