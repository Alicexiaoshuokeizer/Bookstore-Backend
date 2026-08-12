# 🚀 App Deployment & Automation Pipeline Guide

This document presents the technical proposal for the target deployment architecture and the Continuous Integration / Continuous Delivery (CI/CD) automation pipeline designed for the Bookstore Backend microservice.

---

## 1. Cloud Server Setup

Our target live cloud environment splits our application parts into separate, safe network spaces to keep our data secure:

*   **Application Hosting Layer**: The containerized Spring Boot backend engine runs inside an orchestration tier (such as AWS ECS or a managed Kubernetes cluster). It scales dynamically behind a public-facing Application Load Balancer (ALB).
*   **Persistent Storage Tier**: The MySQL relational database engine operates within a dedicated private subnet, completely isolated from direct public internet routing.
*   **Virtual Container Network**: Traffic between the application engine container and the relational storage nodes routes entirely over an isolated virtual network bridge (`bookstore-network`), matching local Docker settings.
---

## 2. Automated Build Sequence (CI/CD Pipeline)

Our build server executes five automated stages in a straight line every single time new code is pushed. If a single step fails, the pipeline stops instantly and blocks deployment to keep bugs off the server.

### 2.1 Visual Automation Mapping
Our complete team user layouts, cloud infrastructure maps, and automated build pipeline steps are designed visually.

You can view the full graphical chart embedded directly inside the main project **README.md** file at the root of this repository.

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
