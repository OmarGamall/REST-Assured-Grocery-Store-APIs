# Grocery Store REST API Automation Project

A comprehensive REST API automation project built with Java, Rest-Assured, and TestNG that provides 100% test coverage for the Grocery Store APIs (see the specification document: [simple-grocery-store-api.md](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/simple-grocery-store-api.md) ).

> [!IMPORTANT]
> The exported test case documentation (in CSV format for Google Sheets) can also be accessed via the Google Drive folder:
> **[Google Drive - Test Cases Folder](https://drive.google.com/drive/folders/1bYYEW1SPnJIWSxgHjUWB03rIsHc5AvBn?usp=drive_link)**

---

## Table of Contents

- [Features](#features)
- [Module Coverage](#module-coverage)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Running Tests](#running-tests)
- [Continuous Integration (CI)](#continuous-integration-ci)

---

## Features

- **End-to-End Testing Flows**: Validates complete customer journeys, such as searching products, adding items to a cart, checking out, verifying the order, and deleting it.
- **Robust Error & Boundary Validations**: Covers negative testing paths including out-of-stock items, exceeding limits, invalid payloads, missing headers, and client resource boundary access rules (unauthorized multi-tenant attempts).
- **Dynamic Test Data**: Leverages JavaFaker to dynamically produce unique names, emails, and comments for registration and order creation.
- **Automatic Token Management**: Automatically registers a fresh client at suite startup or utilizes a predefined token from `config.properties` to authenticate bearer token endpoints.
- **Object-Oriented Mapping**: Uses Jackson Databind for smooth serialization and deserialization of JSON request/response payloads to strongly typed DTOs.
- **Contract Testing & Schema Validation**: Integrates the Rest-Assured JSON Schema Validator to dynamically validate API response payloads against pre-defined JSON schemas (`draft-07`), ensuring structural integrity and preventing regression issues from API contract changes.
- **Boilerplate Reduction & Fluent Builders**: Integrates Project Lombok to eliminate verbose DTO boilerplate (getters, setters, toString) and implements the **Builder Design Pattern** (`@Builder`) for highly readable, flexible, and type-safe object instantiations, replacing rigid overloaded constructors.
- **Centralized HTTP Client Wrapper (RestHelper)**: Implements a custom fluent HTTP builder client wrapping REST Assured. This decouples the API models from REST Assured, centralizes logging, exception handling, and SSL validation configurations, and eliminates repeating execution boilerplate across all endpoints.
- **Parallel Test Execution & Thread Safety**: Engineered for high-throughput concurrency by executing test methods in parallel threads via the `maven-surefire-plugin`. The test suite is designed to be fully independent, utilizing isolated dynamic test data generation (via `JavaFaker`) and a thread-safe `synchronized` token manager to prevent race conditions or cross-test state leakage.
- **Interactive HTML Test Reports (Allure)**: Integrated Allure TestNG for producing highly visual, interactive HTML test execution reports. Captured report features include dynamic failure categorizations, historical trend lines, execution timelines, and custom test steps.
- **Automated API Request & Response Logger (Allure Rest-Assured)**: Utilizes the `AllureRestAssured` filter to automatically capture HTTP request/response payloads, headers, parameters, and status codes, attaching them directly to test steps inside Allure reports for simplified debugging and diagnosis.
- **Detailed Test Reports**: Features clear step-by-step test execution logic in Markdown and CSV formats for import into Excel or Google Sheets.

---

## Module Coverage

The automation suite covers the following API modules:

1. **Authentication & Clients (`/api-clients`)**
   - Registering new clients, validation on empty name/email, invalid email format, and duplicate emails (409 Conflict).
2. **Products (`/products`)**
   - Retrieving all products, filtering by category/availability, results limitation boundaries (-1, 0, 20, 1000), lookup by product ID, and non-existent product ID handling (404).
3. **Cart (`/carts`)**
   - Cart creation, adding items, validations (out-of-stock, zero/negative quantity, duplicate items, exceeding stock limits), replacing cart items, updating quantities, and deleting items.
4. **Orders (`/orders`)**
   - Order placement (for single or multiple unique cart items), order retrieval (standard and with invoice details), updating customer details/comments, deletion, and cross-client access authorization boundary validations.

---

## Tech Stack

- **Language**: Java 21
- **API Client**: Rest-Assured (v5.5.6)
- **Schema Validation**: Rest-Assured JSON Schema Validator (v5.5.6)
- **Test Framework**: TestNG (v7.10.2)
- **JSON Processing**: Jackson Databind (v2.17.0)
- **Data Generation**: JavaFaker (v1.0.2)
- **Boilerplate Reduction**: Project Lombok (v1.18.32)
- **Reporting**: Allure (v2.24.0) via `allure-testng` and `allure-rest-assured`
- **Build Tool**: Maven

---

## Project Structure

```
.
├── pom.xml                             # Maven Dependencies and Build Configurations
├── simple-grocery-store-api.md         # API Reference / Specification Document
├── README.md                           # Project Documentation
├── testing_docs/                       # Test Case Reference Documentation (Markdown & CSV)
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── grocerystore/
    │               ├── apis/       # Rest-Assured API Endpoints and Headers builder
    │               ├── models/     # Jackson DTOs mapping Request & Response bodies
    │               │   ├── cart/   # Cart request/response models
    │               │   ├── client/ # API Client registration models
    │               │   ├── order/  # Order request/response models
    │               │   ├── product/# Product query and item models
    │               │   └── ErrorResponse.java
    │               ├── services/   # Business logic helper services (e.g. Products availability)
    │               ├── steps/      # Multi-endpoint business workflow sequences (Cart, Orders, etc.)
    │               └── utils/      # Technical utility tools
    │                   ├── ConfigLoader.java # Properties loader configuration manager
    │                   ├── TokenManager.java # Dynamic token lifecycle management
    │                   ├── RestHelper.java   # Centralized HTTP builder client
    │                   └── Client_Token_Generation_Flow.png # Authentication flow diagram
    └── test/
        ├── resources/
        │   ├── config.properties       # Configuration details (token, client details)
        │   └── schemas/                # Predefined JSON schemas for contract testing & validation
        └── java/
            └── com/
                └── grocerystore/
                    └── testcases/  # TestNG Suites (Auth, Cart, Order, Product)
                        ├── cart/   # Cart management and boundary suites
                        ├── order/  # Order execution and client access validation suites
                        ├── product/# Product filtration and lookup validation suites
                        ├── AuthTest.java # Client registration suite
                        └── BaseTest.java # Base Test Setup with TestNG hooks and JavaFaker
```

---

## Running Tests

### Prerequisites
- Java Development Kit (JDK) 21 installed.
- Apache Maven installed and configured.

### Execute the Automation Suite
You can execute all tests using the Maven command line in the project root. By default, this runs against the **Production** environment:

```bash
mvn clean test
```

### Environment Selection
The framework supports dynamic environment switching. You can target different environments by passing the `env` parameter via the Maven CLI:

* **Production (Default)**:
  ```bash
  mvn clean test -Denv=production
  ```
* **Testing**:
  ```bash
  mvn clean test -Denv=testing
  ```
* **Custom / Ad-Hoc URL**:
  ```bash
  mvn clean test -Denv=https://custom-grocery-store-api.click
  ```

### Parallel Execution Tuning
By default, the framework is configured to run tests concurrently at the **method level** to optimize execution speed. This is managed via the `maven-surefire-plugin` configuration in [pom.xml](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/pom.xml):

* **Parallel Mode**: `methods` (executes individual `@Test` methods in parallel)
* **Default Thread Pool**: `10` concurrent threads

To dynamically override the thread count at runtime (e.g., in resource-constrained CI/CD pipelines or high-performance runners), pass the `threadCount` property via the Maven CLI:
```bash
mvn clean test -DthreadCount=5
```

You can combine both parameters to run tests concurrently on a specific environment:
```bash
mvn clean test -Denv=testing -DthreadCount=12
```

### Generating Allure Reports
Allure generates raw test execution results under the `target/allure-results` directory. You can generate and view the interactive HTML report using the Allure Command Line Tool.

1. **Prerequisite**: Ensure Allure CLI is installed on your machine (e.g., via `scoop install allure` on Windows or `brew install allure` on macOS/Linux).
2. **Execute Tests**: Run the Maven test command to generate the raw test execution results:
   ```bash
   mvn clean test
   ```
3. **Generate & Open Report**:
   - To build and view the report dynamically in a local web browser server immediately:
     ```bash
     allure serve target/allure-results
     ```
   - To build a static, self-contained HTML report in the `allure-report` folder:
     ```bash
     allure generate target/allure-results --clean -o allure-report
     allure open allure-report
     ```

### Configuration
Update the `src/test/resources/config.properties` file:
- **`env`**: The target execution environment (`production` or `testing`). Defaults to `production`.
- **`api.token`**: If left blank, the suite dynamically registers a new API client. Provide a token here if you wish to run all tests under a pre-existing token.
- **`client.name` / `client.email`**: Used as client details for dynamically registered tokens. Leave blank to generate using JavaFaker.

---

## Continuous Integration (CI)

This test automation suite is fully integrated with **GitHub Actions** to implement a robust CI pipeline. The pipeline validates package compiles, tests runtime stability, and ensures API schema compliance across all codebase modifications.

The pipeline configuration is stored in [.github/workflows/main.yml](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/.github/workflows/main.yml).

### Pipeline Stage Details
1. **Workspace Virtualization**: Initializes a fresh container instance utilizing `ubuntu-latest`.
2. **Environment Configuration**: Bootstraps the workspace with OpenJDK 21 (Eclipse Temurin distribution).
3. **Execution Optimization (Caching)**: Caches the local Maven `.m2` repository to bypass redundant dependency resolutions, dramatically reducing pipeline execution times.
4. **Execution Stage**: Executes `mvn clean test` which targets parallel test execution at the method level.
5. **Artifact Preservation & Archival**: Regardless of build outcome (pass or fail), the runner archives execution diagnostics:
   - **Surefire Reports**: Standard HTML/XML execution reports saved as `surefire-reports`.
   - **Allure Raw Results**: Captured JSON/CSV execution models saved as `allure-results` for detailed local or external reporting.

### Trigger Profiles
The CI pipeline triggers automatically on:
- All `push` operations directed to the `main` branch.
- All `pull_request` events targeting the `main` branch.

