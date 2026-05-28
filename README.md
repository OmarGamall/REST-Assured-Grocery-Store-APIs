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

---

## Features

- **End-to-End Testing Flows**: Validates complete customer journeys, such as searching products, adding items to a cart, checking out, verifying the order, and deleting it.
- **Robust Error & Boundary Validations**: Covers negative testing paths including out-of-stock items, exceeding limits, invalid payloads, missing headers, and client resource boundary access rules (unauthorized multi-tenant attempts).
- **Dynamic Test Data**: Leverages JavaFaker to dynamically produce unique names, emails, and comments for registration and order creation.
- **Automatic Token Management**: Automatically registers a fresh client at suite startup or utilizes a predefined token from `config.properties` to authenticate bearer token endpoints.
- **Object-Oriented Mapping**: Uses Jackson Databind for smooth serialization and deserialization of JSON request/response payloads to strongly typed DTOs.
- **Contract Testing & Schema Validation**: Integrates the Rest-Assured JSON Schema Validator to dynamically validate API response payloads against pre-defined JSON schemas (`draft-07`), ensuring structural integrity and preventing regression issues from API contract changes.
- **Boilerplate Reduction & Fluent Builders**: Integrates Project Lombok to eliminate verbose DTO boilerplate (getters, setters, toString) and implements the **Builder Design Pattern** (`@Builder`) for highly readable, flexible, and type-safe object instantiations, replacing rigid overloaded constructors.
- **Parallel Test Execution & Thread Safety**: Engineered for high-throughput concurrency by executing test methods in parallel threads via the `maven-surefire-plugin`. The test suite is designed to be fully independent, utilizing isolated dynamic test data generation (via `JavaFaker`) and a thread-safe `synchronized` token manager to prevent race conditions or cross-test state leakage.
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
    └── test/
        ├── resources/
        │   ├── config.properties       # Configuration details (token, client details)
        │   └── schemas/                # Predefined JSON schemas for contract testing & validation
        └── java/
            └── test/
                └── GroceryStore/
                    └── com/
                        ├── apis/       # Rest-Assured API Endpoints and Headers builder
                        ├── models/     # Jackson DTOs mapping Request & Response bodies
                        │   ├── cart/   # Cart request/response models
                        │   ├── client/ # API Client registration models
                        │   ├── order/  # Order request/response models
                        │   ├── product/# Product query and item models
                        │   └── ErrorResponse.java
                        ├── services/   # Business logic helper services (e.g. Products availability)
                        ├── steps/      # Multi-endpoint business workflow sequences (Cart, Orders, etc.)
                        ├── testcases/  # TestNG Suites (Auth, Cart, Order, Product)
                        │   ├── cart/   # Cart management and boundary suites
                        │   ├── order/  # Order execution and client access validation suites
                        │   ├── product/# Product filtration and lookup validation suites
                        │   ├── AuthTest.java # Client registration suite
                        │   └── BaseTest.java # Base Test Setup with TestNG hooks and JavaFaker
                        └── utils/      # Utility tools
                            ├── ConfigLoader.java # Properties loader configuration manager
                            ├── TokenManager.java # Dynamic token lifecycle management
                            └── Client_Token_Generation_Flow.png # Authentication flow diagram
```

---

## Running Tests

### Prerequisites
- Java Development Kit (JDK) 21 installed.
- Apache Maven installed and configured.

### Execute the Automation Suite
You can execute all tests using the Maven command line in the project root:

```bash
mvn clean test
```

### Parallel Execution Tuning
By default, the framework is configured to run tests concurrently at the **method level** to optimize execution speed. This is managed via the `maven-surefire-plugin` configuration in [pom.xml](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/pom.xml):

* **Parallel Mode**: `methods` (executes individual `@Test` methods in parallel)
* **Default Thread Pool**: `10` concurrent threads

To dynamically override the thread count at runtime (e.g., in resource-constrained CI/CD pipelines or high-performance runners), pass the `threadCount` property via the Maven CLI:
```bash
mvn clean test -DthreadCount=5
```

### Configuration
Update the `src/test/resources/config.properties` file:
- **`api.token`**: If left blank, the suite dynamically registers a new API client. Provide a token here if you wish to run all tests under a pre-existing token.
- **`client.name` / `client.email`**: Used as client details for dynamically registered tokens. Leave blank to generate using JavaFaker.
