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
- [Flaky Test Retry Mechanism](#flaky-test-retry-mechanism)
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
- **Dynamic Environment Resolution**: Supports execution across multiple environments (`production` and `testing`) or custom URLs. The target URL is dynamically resolved at runtime by the method [resolveBaseUri()](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/main/java/com/grocerystore/apis/Routes.java#L9) inside the [Routes](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/main/java/com/grocerystore/apis/Routes.java) class based on the configured or CLI-passed `env` parameters.
- **Modular API Client Layer**: Implements dedicated Api classes (`com.grocerystore.apis.*`) mirroring the target API's domain modules (Cart, Orders, Products, Users). Each class wraps low-level HTTP mechanics—such as endpoint URLs, path variables, query parameters, and HTTP verbs—into strongly typed Java methods (e.g., `OrdersApi.getOrderById()`). This prevents raw HTTP details from leaking into test files, ensuring that any server-side endpoint changes only require updates in a single, centralized location.
- **Reusable Business Workflows (Steps Layer - Facade Design Pattern)**: Implements an intermediate Steps Layer (`com.grocerystore.steps.*`) that acts as a Facade over the modular API client layer and helper services. It encapsulates complex, multi-endpoint API sequences (e.g., cart initialization, product selection, registration, and checkout) into single, reusable business workflows. This hides low-level request choreography, serializations, and setup assertions, significantly enhancing test readability and reducing code duplication.
- **Centralized Expected Error Messages (Constants Registry)**: Avoids duplication and hardcoding of expected API error response validation strings across the test suite by grouping them into a central `com.grocerystore.constants.ErrorMessages` registry class. This enhances test resilience to API message updates and enforces uniform assertion expectations.
- **Hybrid Assertion Strategy (Hard & Soft Assertions)**: Employs a hybrid assertion model combining TestNG hard assertions and `SoftAssert`. Hard assertions are used as guard clauses for blocking conditions (such as checking HTTP status codes, verifying JSON schema compliance, and array size boundaries). Soft assertions are used for verifying independent response fields and collection items.
- **Targeted Test Grouping & Suite Customization**: Leverages TestNG groups to support multi-layered execution schemes. Categorizes tests by Execution Phase (`smoke`, `regression`, `e2e`), Functional Module (`auth`, `products`, `cart`, `orders`), and behavioral profiles (`happy-path`, `validation`). Enables executing specific test groups dynamically via Maven command-line arguments (`-Dgroups`) or executing targeted execution suite XML configurations.
- **Automatic Flaky Test Retry**: Features a TestNG `IRetryAnalyzer` listener that automatically detects failed test cases and retries them up to a configurable number of times. This filters out transient network issues, API rate limits, or server hiccups, ensuring stable CI execution.

### 4. Parallel Execution & Thread Safety
- **Independent Test Design & Parallel Execution**: Engineered for high-throughput concurrency by executing test methods in parallel threads via TestNG XML suite files. Every automated test case is written to be fully independent—utilizing isolated, dynamic runtime data (such as fresh cart IDs, order IDs, and Faker data) so that parallel tests never experience resource conflicts or cross-test state leaks.
- **Automatic & Shared Token Caching**: Dynamically resolves and caches a single authentication token globally using a thread-safe, volatile static reference. It checks for a pre-configured `api.token` in `client.properties`, or lazily registers a new client once per suite execution using Faker-generated values, caching the token at the class level to eliminate duplicate registration requests and avoid memory leaks.

### 5. Data Generation & Serialization
- **Dynamic Test Data**: Leverages JavaFaker to dynamically produce unique names, emails, and comments for registration and order creation.
- **Object-Oriented Mapping**: Uses Jackson Databind for smooth serialization and deserialization of JSON request/response payloads to strongly typed DTOs.
- **Boilerplate Reduction & Fluent Builders**: Integrates Project Lombok to eliminate verbose DTO boilerplate (getters, setters, toString) and implements the **Builder Design Pattern** (`@Builder`) for highly readable, flexible, and type-safe object instantiations, replacing rigid overloaded constructors.

### 6. Execution Reporting & Diagnostics
- **Interactive HTML Test Reports (Allure)**: Integrated Allure TestNG for producing highly visual, interactive HTML test execution reports. Captured report features include dynamic failure categorizations, historical trend lines, execution timelines, custom test steps, and **test case severity levels** (`BLOCKER`, `CRITICAL`, `NORMAL`, `MINOR`).
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
├── testng.xml                          # Master test suite (Runs all tests in parallel)
├── testng-smoke.xml                    # Targeted suite running only smoke tests
├── testng-regression.xml               # Targeted suite running regression and E2E tests
├── testng-happy-path.xml               # Targeted suite running happy-path, smoke, and E2E tests
├── simple-grocery-store-api.md         # API Reference / Specification Document
├── README.md                           # Project Documentation
├── testing_docs/                       # Test Case Reference Documentation (Markdown & CSV)
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── grocerystore/
    │               ├── apis/       # Rest-Assured API Endpoints and Headers builder
    │               ├── constants/  # Centralized expected error messages constants registry
    │               ├── models/     # Jackson DTOs mapping Request & Response bodies
    │               │   ├── cart/   # Cart request/response models
    │               │   ├── client/ # API Client registration models
    │               │   ├── order/  # Order request/response models
    │               │   ├── product/# Product query and item models
    │               │   └── ErrorResponse.java
    │               ├── services/   # Business logic helper services (e.g. Products availability)
    │               ├── steps/      # Reusable business workflow steps implementing the Facade pattern
    │               └── utils/      # Technical utility tools
    │                   ├── PropertyReader.java # Properties loader configuration manager
    │                   ├── TokenManager.java # Dynamic token lifecycle management
    │                   ├── RestHelper.java   # Centralized HTTP builder client
    │                   ├── AllureUtilities.java # Utility to clean Allure results programmatically
    │                   └── Client_Token_Generation_Flow.png # Authentication flow diagram
    └── test/
        ├── resources/
        │   ├── env.properties          # Target environment and base URLs configuration
        │   ├── client.properties       # API client credentials configuration
        │   ├── retry.properties        # Test retry limit configuration
        │   ├── META-INF/services/
        │   │   └── org.testng.ITestNGListener # SPI registration for listeners
        │   └── schemas/                # Predefined JSON schemas for contract testing & validation
        └── java/
            └── com/
                └── grocerystore/
                    ├── listeners/      # TestNG Custom Listeners (Retry, AnnotationTransformer, TokenCleanupListener)
                    │   ├── AnnotationTransformer.java
                    │   ├── Retry.java
                    │   └── TokenCleanupListener.java
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
Update the values in the split properties files under [src/test/resources](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/resources) to establish target execution defaults:
- **`env`** (in [env.properties](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/resources/env.properties)): The target execution environment (`production` or `testing`). Defaults to `production`.
- **`api.token`** (in [client.properties](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/resources/client.properties)): If left blank, the suite dynamically registers a fresh client once per suite run. Provide a pre-existing token here to run all tests under that single token.
- **`client.name` / `client.email`** (in [client.properties](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/resources/client.properties)): Default client credentials for registrations. Leave blank to generate dynamically via JavaFaker.
- **`retry.limit`** (in [retry.properties](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/resources/retry.properties)): The maximum number of automatic retries for a failed test. Defaults to `2`. Set to `0` to disable retries.

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
By default, the framework is configured to run tests concurrently at the **method level** with **10 concurrent threads** to optimize execution speed. 

This is managed centrally inside the master [testng.xml](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/testng.xml) suite file (and individual suite XMLs like [testng-regression.xml](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/testng-regression.xml)), ensuring that your execution behaviors remain identical whether you run tests from your IDE or the Maven CLI.

* **Parallel Mode**: `methods` (executes individual `@Test` methods in parallel)
* **Default Thread Pool**: `10` concurrent threads

To change the thread count or parallel mode, simply modify the `thread-count` and `parallel` attributes in the active TestNG XML suite file.

#### Shared Token Caching & Lifecycle Management
To support high-throughput parallel execution with minimal overhead, the [TokenManager](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/main/java/com/grocerystore/utils/TokenManager.java) caches a single, globally shared client access token. This design eliminates complex thread-locals, resolves memory leaks, and isolates test runs.

##### 1. How Caching and Thread Safety Work
* **Globally Shared Cache:** A single static token is cached globally and shared across all parallel execution threads. 
* **Lock-Free Reads (Double-Checked Locking):** We use a `volatile` static reference combined with a synchronized double-checked lock. Once the token is resolved by the first thread, all subsequent threads read the token lock-free and instantly, introducing zero synchronization overhead.
* **Minimal API Pollutions:** The entire suite execution registers exactly **one** API client, rather than one per thread or one per test method.

##### 2. Lifecycle Cleanup and Run Isolation
* **TokenCleanupListener:** We registered a TestNG execution listener (`TokenCleanupListener` implementing `IExecutionListener`) via SPI. 
  * At the start of a run (`onExecutionStart`), it clears the token to prevent token bleed when Surefire reuses JVMs, and calls `AllureUtilities.cleanAllureResults()` to clear stale allure results programmatically.
  * At the end of a run (`onExecutionFinish`), it clears the token from memory to prevent memory leaks.

##### 3. Conditional Stale Token Refresh
* If a token expires mid-suite, the failing test is automatically retried by the TestNG `Retry` analyzer. 
* Inside `Retry.java`, we inspect the failure exception. If it is an authentication failure (containing `401`, `Unauthorized`, or `bearer token`), it calls `TokenManager.clearToken()`. 
* On the retried attempt, the first call to `getToken()` resolves a fresh token seamlessly, while normal assertion/validation failures bypass this cleanup and reuse the cached token.

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
Allure generates raw test execution results under the `target/allure-results` directory (configured via `src/test/resources/allure.properties` so it applies to both Maven CLI and IDE test runs).

To prevent old test executions from accumulating in subsequent runs (especially when running directly in IDEs without a Maven `clean` command), the framework features `AllureUtilities.cleanAllureResults()` which is executed automatically at suite startup by the global TestNG listener to programmatically clear out the directory.

#### Option A: Using the Allure Maven Plugin (Recommended)
You do not need to install the Allure CLI locally. Run the following Maven commands:
* **To build and serve the report dynamically in your default browser:**
  ```bash
  mvn allure:serve
  ```
* **To build a static, self-contained HTML report in the `target/allure-report` folder:**
  ```bash
  mvn allure:report
  ```

#### Option B: Using the Local Allure CLI Tool
If you have the Allure Command Line Tool installed locally, you can run:
* **To serve the report:**
  ```bash
  allure serve target/allure-results
  ```
* **To generate a static report inside `target/allure-report`:**
  ```bash
  allure generate target/allure-results --clean -o target/allure-report
  allure open target/allure-report
  ```

---

## Flaky Test Retry Mechanism

To handle transient network latency, random timeouts, or minor server glitches, the framework integrates an automatic retry mechanism.

### Architectural Flow
1. **Dynamic Registration**: The framework registers the [AnnotationTransformer](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/java/com/grocerystore/listeners/AnnotationTransformer.java) using Java's Service Provider Interface (SPI) at [org.testng.ITestNGListener](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/resources/META-INF/services/org.testng.ITestNGListener).
2. **Annotation Binding**: At suite startup, the transformer programmatically assigns the [Retry](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/java/com/grocerystore/listeners/Retry.java) class to every test method.
3. **Execution**: If a test fails, the retry logic reads the configured limit from [retry.properties](file:///d:/Edu/Omar%20Courses-Referances/APIs/RestAssured/GroceryStoreAPIs/src/test/resources/retry.properties), logs the attempt, and triggers a retry if the limit isn't exceeded.

### Overriding at Runtime
To change the retry threshold dynamically from your CLI (for example, in your GitHub Actions pipeline), you can pass a system property override:
```bash
mvn clean test -Dretry.limit=3
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
