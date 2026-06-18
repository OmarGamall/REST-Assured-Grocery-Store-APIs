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
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Continuous Integration (CI)](#continuous-integration-ci)

---

## Features

### 1. Test Coverage & Functional Scope
- **Test Case Specifications (Markdown & CSV)**: Includes detailed step-by-step test case documentation exported in Markdown and CSV formats (fully compatible with Excel and Google Sheets). This serves as the manual reference defining the exact API validation logic automated by the framework.
- **End-to-End Testing Flows**: Validates complete customer journeys, such as searching products, adding items to a cart, checking out, verifying the order, and deleting it.
- **Robust Error & Boundary Validations**: Automates comprehensive negative testing scenarios (HTTP 4xx errors) to verify API resilience. This includes validation of business rules (e.g., ordering out-of-stock items, exceeding stock limits, or invalid quantities) and security boundaries (e.g., preventing unauthorized access to other clients' carts or orders).

### 2. API Contract Verification
- **Contract Testing & Schema Validation**: Integrates the Rest-Assured JSON Schema Validator to dynamically validate API response payloads against pre-defined JSON schemas (`draft-07`), ensuring structural integrity and preventing regression issues from API contract changes.

### 3. Core Framework Architecture
- **Centralized HTTP Client Wrapper (RestHelper)**: Implements a custom fluent HTTP builder client wrapping REST Assured. This decouples the API models from REST Assured, centralizes logging, exception handling, and SSL validation configurations, and eliminates repeating execution boilerplate across all endpoints.
- **Reusable Business Workflows (Steps Layer)**: Implements an intermediate Steps Layer (`com.grocerystore.steps.*`) that encapsulates complex, multi-endpoint API sequences (e.g., cart initialization, product selection, and checkout) into single, reusable actions. This reduces code duplication, abstracts low-level REST execution details, simplifies framework maintenance, and enhances test case readability.
- **Targeted Test Grouping & Suite Customization**: Leverages TestNG groups to support multi-layered execution schemes. Categorizes tests by Execution Phase (`smoke`, `regression`, `e2e`), Functional Module (`auth`, `products`, `cart`, `orders`), and behavioral profiles (`happy-path`, `validation`). Enables executing specific test groups dynamically via Maven command-line arguments (`-Dgroups`) or executing targeted execution suite XML configurations.

### 4. Parallel Execution & Thread Safety
- **Independent Test Design & Parallel Execution**: Engineered for high-throughput concurrency by executing test methods in parallel threads via the `maven-surefire-plugin`. Every automated test case is written to be fully independent—utilizing isolated runtime data (such as dynamically generated cart IDs and order IDs) and a thread-isolated `ThreadLocal` token manager to prevent cross-test state leakage or resource conflicts during parallel execution.
- **Automatic Token Management**: Dynamically resolves and caches authentication tokens on a per-thread basis using `ThreadLocal`. It follows a prioritized logic: first checks for a pre-configured `api.token` in `config.properties`. If empty, it dynamically registers a fresh client using configured credentials or Faker-generated values. The resulting token is cached at the thread level, ensuring thread-isolated authentication and eliminating duplicate registration requests during parallel test runs.

### 5. Data Generation & Serialization
- **Dynamic Test Data**: Leverages JavaFaker to dynamically produce unique names, emails, and comments for registration and order creation.
- **Object-Oriented Mapping**: Uses Jackson Databind for smooth serialization and deserialization of JSON request/response payloads to strongly typed DTOs.
- **Boilerplate Reduction & Fluent Builders**: Integrates Project Lombok to eliminate verbose DTO boilerplate (getters, setters, toString) and implements the **Builder Design Pattern** (`@Builder`) for highly readable, flexible, and type-safe object instantiations, replacing rigid overloaded constructors.

### 6. Execution Reporting & Diagnostics
- **Interactive HTML Test Reports (Allure)**: Integrated Allure TestNG for producing highly visual, interactive HTML test execution reports. Captured report features include dynamic failure categorizations, historical trend lines, execution timelines, and custom test steps.
- **Automated API Request & Response Logger (Allure Rest-Assured)**: Utilizes the `AllureRestAssured` filter to automatically capture HTTP request/response payloads, headers, parameters, and status codes, attaching them directly to test steps inside Allure reports for simplified debugging and diagnosis.

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
                        ├── auth/   # Client registration and validation suites
                        ├── cart/   # Cart management and boundary suites
                        ├── order/  # Order execution and client access validation suites
                        ├── product/# Product filtration and lookup validation suites
                        └── BaseTest.java # Base Test Setup with TestNG hooks and JavaFaker
```

---

## Getting Started

### Prerequisites
Before running the tests, ensure you have the following installed on your machine:
- **Java Development Kit (JDK) 21** or higher.
- **Apache Maven** (build tool) installed and configured in your system path.
- **Allure Command Line Tool** (for generating HTML execution reports). Install via:
  - **Windows (Scoop)**: `scoop install allure`
  - **macOS (Homebrew)**: `brew install allure`
  - **Linux (Apt)**: `sudo apt-get install allure`

### Configuration Setup
Update the values in the [config.properties](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/resources/config.properties) file to establish target execution defaults:
- **`env`**: The target execution environment (`production` or `testing`). Defaults to `production`.
- **`api.token`**: If left blank, the suite dynamically registers a fresh client per thread. Provide a pre-existing token here to run all tests under that single token.
- **`client.name` / `client.email`**: Default client credentials for registrations. Leave blank to generate dynamically via JavaFaker.

---

## Running Tests

### Quick Start
Execute the entire TestNG automation suite against the default **Production** environment:
```bash
mvn clean test
```

### Environment Selection
The framework supports dynamic environment switching. Target different environments by passing the `env` parameter via the Maven CLI:
* **Production**:
  ```bash
  mvn clean test -Denv=production
  ```
* **Testing**:
  ```bash
  mvn clean test -Denv=testing
  ```
* **Custom URL**:
  ```bash
  mvn clean test -Denv=https://custom-grocery-store-api.click
  ```

### Parallel Execution Tuning
By default, the framework is configured to run tests concurrently at the **method level** to optimize execution speed. This is managed via the `maven-surefire-plugin` configuration in [pom.xml](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/pom.xml):

* **Parallel Mode**: `methods` (executes individual `@Test` methods in parallel)
* **Default Thread Pool**: `10` concurrent threads

To dynamically override the thread count at runtime, pass the `threadCount` property via the Maven CLI:
```bash
mvn clean test -DthreadCount=5
```

You can combine both parameters to run tests concurrently on a specific environment:
```bash
mvn clean test -Denv=testing -DthreadCount=12
```

#### Thread-Isolated Token Management (ThreadLocal)
To support high-throughput parallel execution, the [TokenManager](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/main/java/com/grocerystore/utils/TokenManager.java) caches client access tokens using a thread-isolated `ThreadLocal<String>` container rather than a globally synchronized static lock.

##### 1. Why `ThreadLocal`?
* **Elimination of Synchronization Locks:** The previous implementation used a `synchronized` static method, which forced threads to queue up and wait for one another to release the lock, creating a performance bottleneck.
* **Race Condition Prevention:** It ensures that multiple concurrent threads do not overwrite each other's access tokens when initiating registration requests simultaneously.

##### 2. Why Thread-Level Caching?
* We cache the generated token per thread so that if a single worker thread executes multiple test methods sequentially, it reuses its thread-local token. 
* This prevents redundant client registration requests, significantly reducing API network traffic and avoiding rate limits on the target API.

##### 3. Why We Do Not Clear Tokens Between Test Methods
* If we cleared the token in an `@AfterMethod` hook, it would immediately destroy the cache. This would force a new client registration for every single test method execution, creating high network overhead.
* **Automatic Teardown:** Once all tests finish, TestNG shuts down the worker thread pool, causing the threads to be destroyed. Java then automatically garbage-collects the internal thread-local maps, ensuring zero memory leaks.

##### 4. Pros & Cons of This Solution
* **Pros:**
  * **High Throughput:** Absolute lock-free execution for parallel test threads.
  * **Capped Resource Usage:** Limits registrations to the size of the thread pool (at most 10 registrations for 10 threads, rather than 50+).
  * **Automated Cleanup:** Relying on TestNG thread pool shutdown and JVM exit removes the need for complex, manual teardown code.
* **Cons:**
  * **Shared Client State per Thread:** Tests executed sequentially on the same thread share the same client registration. (However, since each test method creates and works on its own isolated `cartId` and `orderId`, they remain fully independent).

### Group-Based Execution (TestNG Groups)
The framework supports running targeted subsets of tests using **TestNG Groups**. Tests are categorized across three layers:
1. **Execution-Phase Groups** (defined at the test method level):
   * `smoke`: Fast, critical-path sanity tests.
   * `regression`: Standard feature testing and boundary checks.
   * `e2e`: Complex, multi-endpoint business flows.
2. **Functional/Module Groups** (defined at the class level):
   * `auth`: Client registration and authentication checks.
   * `products`: Product catalogs, filtration, and detail retrieval.
   * `cart`: Cart creation, modifications, replacements, and item deletion.
   * `orders`: Placing orders, order lookups, customer updates, and cross-client auth boundaries.
3. **Assertive/Behavioral Groups** (defined at the class level):
   * `happy-path`: Verifies standard, valid API interactions.
   * `validation`: Verifies negative testing paths, HTTP 4xx errors, and error responses.

#### Execute Groups via Maven CLI
You can run specific groups or exclude them directly from your terminal:
* **Run only smoke tests**:
  ```bash
  mvn clean test "-Dgroups=smoke"
  ```
* **Run only cart-related tests**:
  ```bash
  mvn clean test "-Dgroups=cart"
  ```
* **Run happy-path tests, excluding validation error tests**:
  ```bash
  mvn clean test "-Dgroups=happy-path" "-DexcludedGroups=validation"
  ```
* **Combine group filters (e.g., cart smoke tests)**:
  ```bash
  mvn clean test "-Dgroups=cart&amp;smoke"
  ```

#### Execute via TestNG XML Suite Files
You can also run pre-configured TestNG XML suites defined in the root folder:
* **Smoke Suite**:
  ```bash
  mvn clean test "-DsuiteXmlFile=testng-smoke.xml"
  ```
* **Happy Path Suite (excluding validations)**:
  ```bash
  mvn clean test "-DsuiteXmlFile=testng-happy-path.xml"
  ```
* **Regression Suite (running all tests)**:
  ```bash
  mvn clean test "-DsuiteXmlFile=testng-regression.xml"
  ```

### Generating Allure Reports
Allure generates raw test execution results under the `target/allure-results` directory. You can generate and view the interactive HTML report using the Allure Command Line Tool.

1. **Prerequisite**: Ensure Allure CLI is installed on your machine.
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
