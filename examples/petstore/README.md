# Pyramid Architecture — PetStore Reference Implementation

This project is the complete, official reference implementation for **The Pyramid Architecture** (Whitepaper v1.0 by Taha Ben Salah). It demonstrates all **Four Faces** of the architecture in a clean, self-contained, and fully testable Maven multi-module project.

---

## 🏛️ Architecture Overview

The Pyramid Architecture organizes software into autonomous, convergent modules structured as pyramids:
- **Broad Base (Infra & DAL)**: Pure types and persistence isolation.
- **Convergent Body (Service)**: Business logic, entity CRUD services, and interceptors.
- **Apex (Service API Facade)**: The single published contract for the module.
- **North Face (REST & Front WS)**: Exposure layers generated/synchronized mechanically from the Apex.

```
                  /\  <-- Apex Facade (service-api)
                 /  \
                / WS \ <-- North Face (ws-rest, service-rest-cli)
               /------\
              / Service\ <-- Convergent Body (service-impl, interceptors)
             /----------\
            /  DAL & DB  \ <-- Broad Base (dal-api, dal-jpa)
           /--------------\
          / Infra & Domain \ <-- Foundation (infra)
         --------------------
```

---

## 📂 Project Structure

```text
examples/petstore/
├── pom.xml                                  # Aggregator root POM
├── tools/
│   └── pyramid-generator/                   # Face 3: JavaParser-based AST synchronizer & AI Tool Catalog generator
├── core/                                    # Standard Canonical Pyramid Module (7 submodules)
│   ├── core-infra/                          # Foundational annotations, AppAuditLog record & exceptions
│   ├── core-dal-api/                        # AppAuditLogRepository SPI
│   ├── core-dal-jpa/                        # Spring Data JPA entity, repository & adapter for audit logs
│   ├── core-service-api/                    # Interceptor SPI, ActionGraph & AppCoreModule facade
│   ├── core-service-impl/                   # InterceptorDispatcher, ActionGraphDispatcher & AppCoreModuleImpl
│   ├── core-service-rest-cli/               # Remote REST client for AppCoreModule
│   └── core-ws-rest/                        # REST Controller (/api/core)
├── extensions/
│   └── notification/                        # First-class Extension (NO DAL)
│       ├── extension-notification-infra
│       ├── extension-notification-service-api
│       ├── extension-notification-service-impl
│       ├── extension-notification-service-rest-cli
│       └── extension-notification-ws-rest
├── drivers/
│   ├── driver-notification-memory/          # In-memory logging driver (NO WS)
│   │   ├── infra, dal-api, dal-jpa, service-impl
│   └── driver-notification-console/         # Console driver (NO WS) using Nuts formatting
│       ├── infra, service-impl
├── modules/
│   ├── catalog/                             # Business Module: Catalog (Full 7 submodules)
│   │   ├── catalog-infra                    # Pure records & enums (Pet, Category, Tag, PetStatus)
│   │   ├── catalog-dal-api                  # PetRepository interface
│   │   ├── catalog-dal-jpa                  # Spring Data JPA entity, repository & adapter
│   │   ├── catalog-service-api              # PetCatalogModule (Apex facade)
│   │   ├── catalog-service-impl             # PetCrudService & PetCatalogModuleImpl
│   │   ├── catalog-service-rest-cli         # Client for remote deployment
│   │   └── catalog-ws-rest                  # REST Controller (North Face)
│   └── order/                               # Business Module: Order (Full 7 submodules + 3 Interceptors)
│       ├── order-infra                      # Order, OrderItem, OrderStatus
│       ├── order-dal-api                    # OrderRepository interface
│       ├── order-dal-jpa                    # JPA entities, repository & adapter
│       ├── order-service-api                # PetOrderModule (Apex facade)
│       ├── order-service-impl               # OrderCrudService, PetOrderModuleImpl & Interceptors:
│       │                                    #   - InventoryValidationInterceptor (BEFORE_ADD)
│       │                                    #   - PetStatusSyncInterceptor (AFTER_ADD)
│       │                                    #   - OrderNotificationInterceptor (AFTER_ADD)
│       ├── order-service-rest-cli           # Remote REST client
│       └── order-ws-rest                    # REST Controller
├── apps/                                    # Deployment Layouts Aggregator
│   ├── monolith/                            # Layout 1: Unified In-Process Monolith
│   │   └── app-petstore/                    # Monolith runnable Spring Boot app (Port 8080)
│   └── microservices/                       # Layout 2: Distributed Autonomous Microservices
│       ├── app-catalog-service/             # Standalone Catalog Microservice (Port 8081)
│       ├── app-order-service/               # Standalone Order Microservice (Port 8082, calls 8081 & 8083 via REST CLI)
│       └── app-notification-service/        # Standalone Notification Microservice (Port 8083)
└── tests/
    ├── architecture-tests/                  # ArchUnit tests enforcing architectural rules compile-time
    └── integration-tests/                   # End-to-end lifecycle, interceptor abort, AST generator, and AI ActionGraph tests
```

---

## 🎯 The Four Faces in Action

### Face 1: The Pyramid Shape & Layer Convergence
- Modules converge upward: `catalog-infra` has zero dependencies. `catalog-dal-api` depends only on `catalog-infra`.
- `catalog-service-impl` encapsulates `PetCrudService` and `PetRepository`. Outgoing module dependencies can **only** touch the `catalog-service-api` facade.
- Strict ArchUnit rules guarantee compile-time verification of layer convergence.

### Face 2: The Modulith-to-Microservices Axis (Apex-to-Apex)
- Inter-module communication is **strictly Apex-to-Apex**:
  - The `order` module never touches `catalog-dal` or `catalog-service-impl`.
  - Its interceptors inject `PetCatalogModule` (the apex interface).
- **Mechanical Migration**:
  - In a monolith, `app-petstore` packages `catalog-service-impl`.
  - In microservices, the application swaps `catalog-service-impl` for `catalog-service-rest-cli`. **Zero business logic changes.**

### Face 3: The North Face & Generator
- Web service controllers (`PetCatalogWS`, `PetOrderWS`) are annotated with `@AppModuleWS(PetCatalogModule.class)`.
- The `pyramid-generator` tool uses **JavaParser** to inspect the AST:
  - Methods already handwritten are preserved unmodified.
  - Missing methods from the Apex are synthesized losslessly.
  - Methods marked `@GeneratorDiscard("ws")` are ignored.
- Generates `tool-catalog.json` defining all Apex methods as function-call tools for AI agents.

### Face 4: The South Face & AI Agents
- The Apex facade serves as the direct tool surface for LLM reasoning agents.
- **Action Graph Execution**: `ActionGraphDispatcher` evaluates and executes directed graphs of tool invocations (`ActionNode`), resolving dependencies and passing context.
- **Interceptors as Guardrails**: All AI tool actions flow through the same transactional interceptor pipeline:
  - `BEFORE` phases execute preconditions and can abort invalid operations cleanly (`ValidationException`).
  - `AFTER` phases trigger downstream synchronizations and audit logs.
  - `InterceptorScope.suppress()` provides scoped bypassing for system migrations and batch jobs.

---

## 🔌 Extensions vs Drivers

| Concept | Purpose | Permitted Submodules | Forbidden Submodules | Example |
| :--- | :--- | :--- | :--- | :--- |
| **Extension** | Cross-cutting orchestrator / capability | `infra`, `service-api`, `service-impl`, `service-rest-cli`, `ws-rest` | **NO `dal`** | `extension-notification` |
| **Driver** | Hardware / service provider SPI implementation | `infra`, `dal-api`, `dal-jpa`, `service-impl` | **NO `ws` or controllers** | `driver-notification-memory`, `driver-notification-console` |

---

## 🚀 Running the Reference Implementation

### Prerequisites
- JDK 17+ (tested with OpenJDK 17 and 25)
- Apache Maven 3.8+

### 1. Build and Run All Tests
```bash
mvn clean test
```
All ArchUnit architecture tests and integration tests will execute against an in-memory H2 database.

### 2. Run the Code Generator
To synchronize missing controller endpoints and generate the AI Tool Catalog:
```bash
mvn compile exec:java -pl tools/pyramid-generator -Dexec.mainClass=net.thevpc.samples.petstore.tools.generator.PyramidGeneratorCli -Dexec.args="--root . --tools"
```

### 3. Run the Monolith Application
```bash
mvn spring-boot:run -pl apps/monolith/app-petstore
```
Access the H2 console at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:petstoredb`).

### 4. Run the Independent Microservices
You can run each microservice independently on its own port with its own isolated database:

```bash
# Terminal 1: Catalog Microservice (Port 8081)
mvn spring-boot:run -pl apps/microservices/app-catalog-service

# Terminal 2: Notification Microservice (Port 8083)
mvn spring-boot:run -pl apps/microservices/app-notification-service

# Terminal 3: Order Microservice (Port 8082 - communicates with 8081 and 8083 strictly via REST CLI)
mvn spring-boot:run -pl apps/microservices/app-order-service
```
