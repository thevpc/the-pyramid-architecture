# The Pyramid Architecture White Paper
### A Modular, Agile and Scalable Software Architecture for Modern Products

---

## Executive Summary

**Problem**: Systems struggle to balance rapid iteration with long-term evolvability; monoliths become rigid, microservices become complex.

**Solution**: The Pyramid Architecture enforces modular decomposition with strict layering, enabling:

- ✅ Independent module development and testing
- ✅ Mechanical migration from modulith to microservices
- ✅ Swappable infrastructure via extensions/drivers
- ✅ Built-in support for AI-agent integration

**Best suited for**: ERP platforms, multi-tenant SaaS, regulated environments requiring data sovereignty, and teams prioritizing incremental evolution over big-bang rewrites.

---

> **Abstract**
>
> The Pyramid Architecture is a software architecture pattern that enforces a clean separation of concerns both at the business domain level and at the technical layer level. It defines a structured decomposition of a system into autonomous, composable modules, each organized into specialized processing layers that converge upward into a single, unified entry point. Built on the principles of Clean Architecture, it introduces concrete constraints and tooling that make it particularly well-suited for iterative product development, startup environments, and systems that must evolve gracefully from a monolith to a microservices deployment — and back — without structural redesign.
>
> This document describes the Pyramid Architecture as a pattern. Code examples and directory structures are illustrative of a representative implementation and are illustrative; the architecture defines structural contracts, not implementation syntax. The architecture is implementation-agnostic: it can be realized in any language, framework, or build system that supports the structural concepts described here.

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [The Pyramid](#2-the-pyramid)
   - 2.1 [Vertical Decomposition — Modules](#21-vertical-decomposition--modules)
   - 2.2 [Horizontal Decomposition — Layers](#22-horizontal-decomposition--layers)
   - 2.3 [Communication Rules](#23-communication-rules)
   - 2.4 [Module Anatomy](#24-module-anatomy)
   - 2.5 [Isolation](#25-isolation)
   - 2.6 [Scalability](#26-scalability)
3. The Four Faces of the Pyramid Architecture
   - 3.1 [Structural Face](#31-structural-face)
   - 3.2 [Connective Face](#32-connective-face)
   - 3.3 [Elastic Face](#33-elastic-face)
   - 3.4 [Convergence Face](#34-convergence-face)
4. [Data Management](#3-data-management)
   - 4.1 [Domain Entities](#31-domain-entities)
   - 4.2 [Repositories and DAOs](#32-repositories-and-daos)
   - 4.3 [Storage Entities](#33-storage-entities)
   - 4.4 [Extended References](#34-extended-references)
   - 4.5 [Data Segregation](#35-data-segregation)
   - 4.6 [Cross-Module Queries](#36-cross-module-queries)
   - 4.7 [Transaction Boundaries](#37-transaction-boundaries)
5. [Services](#4-services)
   - 5.1 [CRUD Services](#41-crud-services)
   - 5.2 [Business Services](#42-business-services)
   - 5.3 [Facade Services](#43-facade-services)
   - 5.4 [Exposed Services](#44-exposed-services)
6. [Extensions and Drivers](#5-extensions-and-drivers)
   - 6.1 [Extensions](#51-extensions)
   - 6.2 [Drivers](#52-drivers)
   - 6.3 [The Ports and Adapters Relationship](#53-the-ports-and-adapters-relationship)
7. [Deployment Models](#6-deployment-models)
   - 7.1 [The M/P Model](#61-the-mp-model)
   - 7.2 [The Assembly Layer](#62-the-assembly-layer)
   - 7.3 [Modulith to Microservices](#63-modulith-to-microservices)
8. [Interceptors](#7-interceptors)
   - 8.1 [Entity Interceptors](#71-entity-interceptors)
   - 8.2 [Query Interceptors](#72-query-interceptors)
   - 8.3 [Design Principles](#73-design-principles)
9. [Infrastructure and Tooling](#8-infrastructure-and-tooling)
   - 9.1 [The Application Manifest](#81-the-application-manifest)
   - 9.2 [Code Generation](#82-code-generation)
   - 9.3 [The Single Source of Truth Principle](#83-the-single-source-of-truth-principle)
10. [Comparison with Other Architectures](#9-comparison-with-other-architectures)
11. [Known Limitations and Trade-offs](#10-known-limitations-and-trade-offs)
12. [Conclusion](#11-conclusion)
13. [The Pyramid Architecture and AI-First Systems](#12-the-pyramid-architecture-and-ai-first-systems)

---

## 1. Introduction

Modern software systems face a fundamental tension: they must be simple enough to build quickly, yet structured enough to evolve without accumulating crippling technical debt. Traditional monolithic architectures offer simplicity at the cost of rigidity. Microservices architectures offer flexibility at the cost of operational complexity. Most real-world systems eventually find themselves trapped between these two extremes, unable to scale the architecture without rewriting significant portions of the system.

The **Pyramid Architecture** is a response to this tension. It proposes that the structure of a system should mirror both its business boundaries and its technical concerns — simultaneously and consistently — through a disciplined decomposition model that remains valid regardless of the deployment topology chosen.

The name derives from the geometric shape of the composition model itself: many fine-grained components at the base (repositories, data access objects), grouped progressively into fewer, broader components at each layer (services, facades), converging into a single entry point at the apex (the API or the UI). When multiple modules are assembled into an application, the picture becomes a collection of pyramids — each self-contained, each communicating only at their tips.

The Pyramid Architecture is not a replacement for established patterns such as Hexagonal Architecture, Clean Architecture, or Domain-Driven Design. It is a **concrete, opinionated instantiation** of their principles, extended with specific structural rules and tooling guidance that make the principles enforceable rather than aspirational.

### A Note on Examples

Throughout this document, code snippets and directory structures are drawn from a reference implementation in Java and Spring Boot. These are **illustrative examples** of one way to realize the pattern. The architecture itself is language- and framework-agnostic. The concepts — modules, layers, facades, interceptors, drivers — map to equivalent constructs in any technology stack that supports modular decomposition and dependency inversion.

Similarly, specific annotations, naming conventions, and file organization shown in examples represent one team's choices, not architectural requirements. The requirement is the concept; the implementation is a choice.

---

## 2. The Pyramid

The Pyramid Architecture rests on two orthogonal decomposition axes applied simultaneously:

- A **vertical (business) axis** that decomposes the system into autonomous business modules.
- A **horizontal (technical) axis** that decomposes each module into specialized processing layers.

The intersection of these two axes defines a grid of components, each with a precise responsibility. The **convergence rule** — each component owns and aggregates one or more components from the layer directly below it — gives the architecture its pyramidal shape.

Convergence Rule: For any component C at layer Lₙ, the set of components C directly depends upon must be a non-empty subset of layer Lₙ₋₁, and C must not depend on any component at layer Lₖ where k < n−1 or k = n.

![The Pyramid](images/pyramid-01.png)

![The Pyramid](images/pyramid-02.png)

```
          [Single Entry Point]              ← Apex (API / UI)
                  ↑
           [Module Facade]                  ← Service layer top
          ↑       ↑       ↑
       [Svc1]  [Svc2]  [Svc3]              ← Domain services
       ↑  ↑     ↑  ↑     ↑  ↑
    [R1] [R2] [R3] [R4] [R5] [R6]          ← Data access layer
```

*Figure 1 — The pyramid shape of a single module. Many repositories at the base converge through domain services into a single facade at the apex.*

When the system contains multiple modules, each module forms its own pyramid. The collection of pyramids communicates only at the apex level — facade to facade:

```
      [Facade-A]  ←————→  [Facade-B]
          ▲                    ▲
         /|\                  /|\
        / | \                / | \
       /  |  \              /  |  \
      /____|___\            /____|___\
     repos/svcs            repos/svcs

    Module A                Module B
```

*Figure 2 — Two modules communicating apex to apex. Their bases remain entirely private.*

---

### 2.1 Vertical Decomposition — Modules

The first decomposition axis is **vertical and business-driven**. The system is divided into elementary business units called **modules**. A module encapsulates a complete, cohesive, and self-sufficient business concern. The key qualification for a module is that it should be **functionally elementary** — difficult or impossible to decompose further without losing business coherence.

Standard software engineering principles apply: **high cohesion** within a module, **loose coupling** between modules.

#### Module Autonomy

A module is designed to be a fully autonomous unit. It can, in principle:

- Be developed, tested, and deployed independently of any other module.
- Own its own data store — a database schema, a collection, or a dedicated database instance.
- Own its own user interface if the use case demands it.
- Be implemented in a different technology stack if required.

When a module uses a fully separate technology stack, the system naturally converges toward a classical microservices model. The Pyramid Architecture does not prohibit this — it simply does not require it as a default.

#### Module Groups

Related modules may be organized into **module groups** — collections of modules that share a common business domain and are co-developed together. A module group is a development-time organizational convenience, not an architectural unit. Modules within a group remain independently deployable; the group simply reflects team ownership or domain proximity.

*Example: a finance module group might contain `invoices`, `expenses`, `payments`, and `bank-transactions` as separate modules, co-developed by the same team but each with its own full pyramid anatomy.*

The module group has no architectural significance at runtime. At deployment time, modules from any group can be assembled in any combination regardless of their group membership.

#### Handling Circular Dependencies Between Modules

When two modules appear to have a circular dependency — Module A needs something from Module B and Module B needs something from Module A — this is a signal that a shared concept has not been properly identified. The resolution in the Pyramid Architecture is to **extract an extension**: promote the shared concept to a first-class extension with its own API, which both modules depend on without depending on each other.

This keeps the module dependency graph acyclic and the pyramid shapes intact.

---

### 2.2 Horizontal Decomposition — Layers

Once modules are defined, each module is decomposed horizontally into **three canonical layers**, following a classic three-tier model: Data Access, Business Logic, and Presentation/Exposure. Each layer has a precise role and a precise set of component types.

#### Layer Responsibilities

| Layer | Role | Typical Component Types |
|---|---|---|
| **Data Access (DAL)** | Persistence and retrieval | Entities, DAOs, Repositories |
| **Service** | Business logic and orchestration | Domain Services, Business Services, Module Facade |
| **Web Service (WS)** | External exposure | REST Controllers, gRPC endpoints, Remote client proxies |

#### The Convergence Rule

Within each layer, each component is **exclusively responsible** for one or more components in the layer directly below it. A component may not skip a layer or reach sideways to consume a peer component at the same layer level. This rule produces the pyramid shape and gives the architecture its key properties:

- **Testability** — any component can be tested by mocking the layer immediately below it.
- **Replaceability** — any layer implementation can be swapped without affecting layers above.
- **Traceability** — every piece of data or behavior can be traced from the apex down to its origin.

#### Layer Interfaces

Each layer is separated from the layer above it by an **abstract interface**. No layer makes any assumption about how the layer below it is implemented. The interface boundary is the seam that enables implementation swapping, independent testing, and the modulith-to-microservices transition described in Section 6.

```
[WS Layer]          depends on  →  Service API (interface)
[Service Layer]     depends on  →  DAL API (interface)
[DAL Layer]         implements  →  DAL API (interface)
```

Concrete implementations of each interface are never directly referenced by the layers above them. They are wired together only at the application assembly level.

---

### 2.3 Communication Rules

Communication rules in the Pyramid Architecture are strict and explicit. They are the mechanism by which the pyramid shape is enforced at runtime.

#### Within a Module

```
WS layer        →  Facade only                                ✓
Service         →  any Repository in the same module          ✓
Service         →  Repository primarily owned by a peer Svc   ✓  (read)
Service         →  another Service in the same module         ✗
```

A service component may access any repository within the same module, including repositories whose primary owner is a sibling service. This is a deliberate relaxation — within a module, the team has full visibility of all data, and direct repository access avoids unnecessary indirection. A service should not write to repositories it does not own, as this blurs data ownership.

The prohibition on service-to-service calls within a module is deliberate. It prevents circular dependencies and keeps the convergence rule intact: if a service needs data managed by another service, it accesses the repository directly, not through the service layer.

#### Between Modules

```
Facade-A        →  Facade-B                                   ✓
Service-A       →  Facade-B  (consuming the published API)    ✓
Service-A       →  Service-B (internal to Module B)           ✗
Service-A       →  Repository-B (owned by Module B)           ✗
```

Communication between two modules is **always facade-to-facade** — apex to apex. No component in Module A may reference any internal component of Module B other than its facade. This ensures that inter-module dependencies are always expressed at the highest abstraction level, never leaking internal implementation details.

The geometric intuition is direct: **pyramids touch only at their apexes**. Their bases remain fully private.

---

### 2.4 Module Anatomy

Each module is physically organized into a set of sub-projects or sub-packages, one per layer variant. The canonical anatomy of a module is:

```
module-xxx-infra              Shared types: DTOs, constants, interfaces,
                              entity definitions, query objects, references.
                              No dependency on any other layer.

module-xxx-dal-api            Repository interfaces. No implementation.

module-xxx-dal-jpa            Relational/ORM implementation of dal-api.
                              Contains storage entities and ORM repositories.

module-xxx-dal-mongo          Document store implementation of dal-api.
                              Contains document entities and document repositories.

module-xxx-service-api        The module facade interface. The published contract.

module-xxx-service-impl       Local implementation of the facade.
                              Contains domain services, business services,
                              and interceptors.

module-xxx-service-rest-cli   REST client proxy implementing service-api.
                              Used when this module is deployed as a remote service.

module-xxx-service-grpc-cli   gRPC client proxy implementing service-api.
                              Alternative remote consumption variant.

module-xxx-ws-rest            REST controllers exposing the module facade.

module-xxx-ws-grpc            gRPC handlers exposing the module facade.
```

*Figure 3 — Canonical module anatomy. Not all sub-projects need to exist for every module.*

Not all sub-projects need to be present. A module with no persistence needs no DAL sub-projects. A module always deployed locally needs no remote client sub-projects. The anatomy defines the full possibility space; a given module instantiates the subset it needs.

**Reserved slots** are a useful practice: a sub-project may exist as an empty placeholder to signal that the slot is architecturally present even if not yet implemented. This keeps the structure symmetric and makes future implementation straightforward without structural reorganization.

The critical dependency rule across sub-projects:

```
ws-rest / ws-grpc       →  service-api        (never service-impl)
service-impl            →  dal-api            (never dal-jpa or dal-mongo)
service-rest-cli        →  service-api        (implements the contract remotely)
dal-jpa / dal-mongo     →  dal-api + infra
infra                   →  no internal deps
```

Concrete implementations are never referenced by the layers above them. This rule is ideally enforced at the build system level so that violations become build errors.

---

### 2.5 Isolation

The Pyramid Architecture enforces isolation at multiple levels:

**Compile-time isolation** is achieved through the sub-project dependency structure. When a WS sub-project depends only on `service-api`, it is physically unable to call `service-impl` methods — they are not on its classpath. Architectural violations become compilation errors.

**Runtime isolation** is achieved through the deployment model. In a microservices deployment, module boundaries correspond to process boundaries. In a modulith, isolation is enforced by the dependency rules above.

**Data isolation** is the most important form of isolation. Each module owns its data exclusively. No other module may directly access another module's data store. Cross-module data sharing is handled exclusively through the facade API and through the extended reference pattern (Section 3.4). Data isolation is what makes the M/P deployment model sound — a module that has never shared its data store can be extracted to its own process without data migration.

---

### 2.6 Scalability

The Pyramid Architecture supports scalability across multiple dimensions simultaneously:

**Team scalability** — each module is a bounded unit that a single team or developer can own end-to-end. Module boundaries correspond naturally to team boundaries. A new developer can be productive on a module without understanding the rest of the system.

**Deployment scalability** — any subset of modules can be independently extracted and scaled. A module under high load can be isolated to its own process and scaled horizontally without touching any other module.

**Feature scalability** — adding a new module requires no modification to any existing module. The new module integrates at the application assembly level only.

**Complexity scalability** — as the number of modules grows, the pyramid structure prevents complexity accumulation. Each module remains bounded and comprehensible in isolation. The total complexity of the system is approximately the sum of its module complexities, not a super-linear function of them.

---

## 3. The Four Faces of the Pyramid Architecture

The Pyramid Architecture is defined by four orthogonal, interdependent faces. Each face governs a distinct architectural concern, yet together they form a complete system design philosophy. This framing is not an addition of new capabilities, but a conceptual synthesis of the pattern's core mechanisms. It provides a shared mental model for architects, product teams, and engineers to reason about structure, interaction, deployment, and value evolution.

```
          [Face 4: Convergence — Added Value & Agency]
                     ↑ synthesizes capabilities into proactive outcomes
    [Face 2: Connective — Interaction & Adaptability] ←→ [Face 3: Elastic — M/P Deployment]
                     ↑ enables cross-boundary coordination & swappability
          [Face 1: Structural — Decomposition]
                     ↑ establishes isolation & bounded contexts
```

### 3.1 Face 1: The Structural Face (Decomposition)
**Definition**
The Structural Face is the skeleton of the system. It defines how the architecture is decomposed simultaneously along two orthogonal axes:

    Vertical (Business): The system is sliced into autonomous business modules, each representing a cohesive domain with clear boundaries.
    Horizontal (Technical): Each module is further decomposed into canonical layers (DAL → Service → WS), with strict dependency directionality.

**Enforcement Mechanisms**

    Sub-project isolation: Physical separation of layer implementations ensures that compile-time dependencies can only flow upward toward the facade.
    The Convergence Rule: Every component at layer Lₙ may depend only on components at layer Lₙ₋₁, producing the characteristic pyramidal shape where many low-level components converge into a single apex.
    Facade-as-apex: The module facade is the only permitted entry point for external interaction, encapsulating all internal complexity.

**Architectural Value**

    Prevents architectural drift and "spaghetti" dependencies by making violations build errors.
    Ensures each module is a unit of sovereignty: it owns its data, its logic, and its exposure contract.
    Enables independent development, testing, and reasoning about any module in isolation.
    Provides the clean, typed surfaces that downstream faces (especially Convergence) rely upon for automation and AI integration.

### 3.2 Face 2: The Connective Face (Interaction & Adaptability)

**Definition**

The Connective Face governs how isolated modules coordinate without becoming entangled, and how the system adapts to its operational environment without internal refactoring.

**Core Mechanisms**

| Mechanism                 | Purpose                                                                                                                                                 | Key Property                                                                                                     |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| **Interceptors**          | React to entity/query lifecycle events across module boundaries                                                                                         | Asynchronous, order-independent, saga-capable |
| **Extensions**            | Publish typed contracts for cross-cutting capabilities **+ orchestrate** driver selection, lifecycle management, shared logic, and optional WS exposure | Contract-first API + active coordination layer; modules depend only on the published contract |
| **Drivers**               | Provide pluggable implementations of extensions                                                                                                         | Swappable at assembly time via manifest; isolated from module business logic |
| **Extended References**   | Embed lightweight foreign-entity snapshots to avoid cross-module joins                                                                                  | Denormalized for read resilience, freshness managed via interceptors |


Architectural Value

- **Autonomy**: Modules can change their internal "how" (Driver) without affecting the "what" (Extension API) consumed by others.
- **Resilience**: Interceptors enable cross-module consistency patterns (sagas) without distributed transactions.
- **Adaptability**: Infrastructure choices (database, file storage, messaging) become deployment-time decisions, not code-time commitments.
- **Observability**: The interceptor chain provides a natural hook for audit, monitoring, and—critically—AI observation (Face 4).

Extensions are not passive ports. While they define the what (the API contract), they also provide the glue that enables drivers to function: runtime driver selection, shared validation logic, fallback strategies, and—when needed—their own Web Service endpoints for direct external consumption. This makes Extensions first-class architectural elements that sit between modules and infrastructure: modules depend on the Extension API; the Extension orchestrates which Driver fulfills it; and the Extension may itself be exposed externally via WS when the capability is a surface of the system (e.g., /webhooks/incoming for a messaging extension). This preserves the dependency inversion principle while acknowledging that cross-cutting capabilities often require active coordination, not just interface definitions.

> The Connective Face turns isolated pyramids into a coordinated constellation.

### 3.3 Face 3: The Elastic Face (M/P Deployment)

**Definition**

The Elastic Face governs the deployment topology: how M modules are distributed across P processes. It decouples architectural design from operational scaling.

**Behavioral Model**

```
M/1  →  All modules in one process          (Modulith)
         • Lowest operational overhead
         • Ideal for development, startups, resource-constrained envs

M/P  →  Selected modules extracted          (Selective Microservices)
         • Scale hot modules independently
         • Respect team or compliance boundaries

1/P  →  One module per process              (Full Microservices)
         • Maximum isolation and autonomy
         • Highest operational complexity
```

**Key Enablers**

- Service API as migration seam: The service-api interface is the pre-engineered boundary where local implementation can be swapped for a remote client proxy without code changes.
- Assembly layer: Manifest-driven composition declares module activation, DAL/WS variants, and service topology (local, remote-rest, remote-grpc) as configuration, not code.
- Tenant-aware activation: In multi-tenant deployments, the same binary can activate different module subsets per tenant, with different driver bindings.

**Architectural Value**

- Mechanical migration: Moving from modulith to microservices is an operational decision, not a refactoring project.
- Right-sized complexity: Teams start simple (M/1) and extract modules only when operational, compliance, or scaling demands justify it.
- Deployment as a variable: The architecture remains valid across the entire M/P spectrum, making topology a tunable parameter rather than a foundational commitment.

> The Elastic Face makes deployment topology a dial, not a destiny.

### 3.4 Face 4: The Convergence Face (Business Value & Agency)

**Definition**

The Convergence Face represents the synthesis of the system's capabilities into higher-order value. It is where clean structure, safe interaction, and flexible deployment converge to enable proactive, intelligent, and automated behaviors that sit above standard CRUD operations.

**Why "Convergence"?**

The name was chosen deliberately to:

- Echo the core Convergence Rule of Face 1, creating terminological consistency.
- Emphasize that value emerges from the integration of the first three faces, not from bolted-on features.
- Remain technology-agnostic: "Convergence" accommodates BPM, analytics, reporting, rules engines, AI agents, and future paradigms without locking into current hype cycles.

**Core Components**

| Component                | Role                                                                                                                         | Enabled By                                                    |
|--------------------------|------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------|
| Tool Surface             | Export module facades as typed LLM tool definitions (JSON Schema, OpenAI functions)                                          | Face 1 (clean facades) + Code Generator                       |
| Observation Layer        | Observation Layer                                                                                                            | Face 2 (interceptors) + Audit trail                           |
| Action Graph             | Action Graph                                                                                                                 | Face 1 (typed contracts) + Face 2 (saga patterns)             |
| BPM & Automation Engine  | Execute approved action graphs as long-running processes; allow AI to generate new process definitions from natural language | Face 2 (extensions/drivers) + Face 3 (deployment flexibility) |
| Cross-Module Read Models | Denormalized projections for reporting/analytics, updated via interceptors                                                   | Face 2 (interceptors) + Face 1 (data segregation)             |
| Extension Tool Surfaces  | Export extension APIs (not just module facades) as LLM tool definitions, including optional WS endpoints                     | Face 2 (active extensions) + Code Generator                   |







**Architectural Value**

- Machine-legibility: The disciplined structure of Faces 1–3 produces typed, observable, swappable surfaces that AI agents can navigate with 100% accuracy—no guessing, no hallucinated APIs.
- Proactive capability: The system shifts from reactive request/response to anticipatory action, initiated by agents observing business events.
- Human-in-the-loop safety: Action graphs and BPM integration ensure that autonomous proposals are validated, explainable, and reversible before execution.
- Progressive automation: Approved action patterns can be compiled into reusable BPM processes, creating a self-reinforcing loop where human decisions train future automation.

> The Convergence Face is where the pyramid becomes a platform—for humans, for agents, for both.

### 3.5 How the Faces Interlock

The four faces are not sequential phases; they operate simultaneously and reinforce each other:

```
          [Face 4: Convergence]
                     ↑ consumes clean interfaces & observability
    [Face 2: Connective] ←→ [Face 3: Elastic]
                     ↑ enables cross-boundary coordination & swappability
          [Face 1: Structural]
                     ↑ establishes isolation & bounded contexts
```

- Face 1 enables Face 2: Clean module boundaries and facade contracts give interceptors and extensions precise surfaces to observe and extend.
- Face 2 enables Face 3: Pluggable drivers and topology-aware interceptors allow the same modules to run locally or remotely without behavioral changes.
- Face 3 enables Face 4: Tenant-aware assembly and swappable LLM/BPM drivers let cognitive capabilities be deployed selectively per environment or compliance requirement.
- Face 4 validates Faces 1–3: AI agents, action graphs, and automation workflows stress-test the architecture's legibility. If an agent cannot reliably navigate facades, observe interceptors, or adapt to topology changes, the underlying faces require refinement.


**Why This Framing Matters**

- **Shared Vocabulary**: Teams can discuss architectural concerns without conflating structure, deployment, interaction, and intelligence.
- **Incremental Adoption**: Organizations can adopt faces independently. Start with Face 1 (modular decomposition), add Face 3 (M/P deployment), layer in Face 2 (interceptors/extensions), and graduate to Face 4 (convergence/automation) when ready.
- **Future-Proofing**: By treating convergence capabilities as a first-class architectural face, the pattern avoids treating AI as a feature bolt-on. Instead, it positions intelligence and automation as natural consequences of architectural cleanliness.
- **Decision Guidance**: When evaluating trade-offs, architects can ask: Which face is affected? For example, introducing a cross-module query impacts Face 2 (interceptors/read models) and Face 3 (deployment topology), not Face 1 (module boundaries).


> The Pyramid Architecture in Four Faces  
>  - Structural – Clean decomposition into autonomous modules and layers.  
>  - Connective – Safe, swappable interaction via interceptors and extensions.  
>  - Elastic – Deployment topology as a tunable variable (M/P model).  
>  - Convergence – Synthesis into proactive value: automation, AI, BPM.
>  - Together, they make systems simple today, scalable tomorrow, and legible to both humans and agents.


## 4. Data Management

### 4.1 Domain Entities

A **domain entity** is the primary data model of a module. It represents a business concept in its full richness and is the type that flows through the service layer. Domain entities are defined in the `infra` sub-project and shared across all layers of the same module.

Domain entities are:

- **Rich** — they carry all business-relevant fields.
- **Mutable** — they are the objects that services read, modify, and persist.
- **Layer-scoped** — visible to all layers within the module, but never exposed directly across module boundaries.

>     "Without the Structural Face, there is no pyramid—only a pile of components."

 
When a module needs to reference an entity owned by another module, it uses an **extended reference** (Section 3.4) rather than importing the full entity type.

### 4.2 Repositories and DAOs

A **repository** is the abstraction between the service layer and the data store. It is defined as an interface in `dal-api` and implemented in the concrete DAL sub-project. A repository operates on domain entities and typically provides:

- Standard lifecycle operations: add, update, remove, find by identifier.
- Query operations via a typed query object.
- Paginated variants of query operations.
- Audit-aware variants of mutation operations that record who changed what and when.

A **DAO** (Data Access Object) is a lower-level construct, internal to a DAL implementation, used for specific persistence concerns such as native queries, aggregation pipelines, or index management. DAOs are never exposed outside the DAL implementation sub-project.

### 4.3 Storage Entities

A **storage entity** is the persistence model — the object mapped directly to a database row, document, or record. Storage entities may differ structurally from domain entities. The mapping between the two is entirely the responsibility of the DAL implementation.

This separation allows the persistence schema to evolve independently of the business model, and allows the same business model to be persisted in different storage technologies by different DAL implementations — relational, document, or otherwise.

### 4.4 Extended References

The **extended reference** is a first-class data pattern in the Pyramid Architecture. It is a lightweight, denormalized snapshot of a foreign entity, embedded inline within a domain entity that references it, rather than storing only a foreign key and resolving it at query time.

A typical extended reference carries:

- `id` — the unique identifier of the referenced entity.
- `name` — the primary human-readable label.
- `shortName` / `longName` — display variants for different UI contexts.
- `photo` — a thumbnail identifier when applicable.
- `type` — a discriminator string when the reference is polymorphic.

```
// Conceptual structure

Entity: Invoice
    id: string
    client: ClientRef              ← extended reference, not a foreign key
        id: string
        name: string
        photo: string
        type: string
    amount: decimal
    date: date
```

The extended reference is a **modeling discipline**, not merely a performance optimization. Its purpose is to enforce module data autonomy: a module that holds an extended reference can answer all common display and filtering queries about that reference without calling the owning module's facade. The invoice module can display the client name and photo without ever calling the client module.

This also means the invoice module continues to function correctly even if the client module is temporarily unavailable — a resilience property that matters in microservices deployments.

#### Reference Freshness

When a referenced entity is updated in its owning module — the client changes their name, for example — stored extended references in other modules become stale. Managing this staleness is the responsibility of the owning module, typically via an entity interceptor (Section 7) that propagates the change to all modules that hold a reference.

The acceptable staleness window is a business decision. In most operational scenarios, a name change propagating within seconds to minutes is acceptable. If instant consistency is required, the reference update must be synchronous and the owning module must know its consumers — which reintroduces coupling. The architecture makes this trade-off explicit.

#### When Not to Use Extended References

Extended references are appropriate for read-heavy, display-oriented relationships. They are not the right tool for:

- **Relationships that are the subject of complex queries** — if the system frequently queries invoices by detailed client attributes (credit rating, sector, city), a denormalized read model may be needed.
- **Highly volatile data** — if the referenced data changes very frequently, propagation overhead may become significant.

### 4.5 Data Segregation

The Pyramid Architecture mandates **strict data segregation by module**. Each module owns its data exclusively. No other module may read from or write to another module's data store directly, regardless of deployment topology.

This has two important practical consequences:

**No shared database schemas.** Even when multiple modules are co-deployed against the same database server, each module operates on its own schema, table prefix, or collection namespace. The database boundary mirrors the code boundary.

**No cross-module ORM sessions.** A service in Module A cannot load an entity from Module B's repository within the same ORM session or database transaction. Cross-module data access is always mediated by the facade API.

Data segregation is the property that makes the M/P model sound. A module that has never shared its data store with another module can be extracted to its own process without any data migration or schema surgery.

### 4.6 Cross-Module Queries

Strict data segregation means that queries spanning multiple modules cannot be expressed as a single database query. The Pyramid Architecture acknowledges this honestly and addresses it through a hierarchy of techniques:

**Extended references as the primary tool.** The most common cross-module display patterns — a list of invoices with client names, expenses with supplier information — are satisfied by the extended reference fields embedded in each entity. No join is needed because the relevant data is already present.

**Service-level composition for moderate cases.** When a query genuinely requires data from multiple modules, it is composed at the service layer by calling multiple facades and joining results in memory. This is less efficient than a single database query but is often acceptable for operational use cases.

**Dedicated read models for reporting.** When high-performance cross-module queries are a genuine requirement — dashboards, financial summaries, analytics — a dedicated read model is introduced as a separate module or extension. The read model maintains a denormalized projection updated via the interceptor system. It is optimized for query, not for write.

The guiding principle is: **if a cross-module query is needed at read time, it is likely a signal that relevant data was not captured at write time.** The correct resolution is usually to enrich the extended reference with additional fields, not to introduce a cross-module join.

### 4.7 Transaction Boundaries

Transactions in the Pyramid Architecture are **scoped to a single module**. A service operation within a module may be atomic — all its repository operations succeed or all fail — but atomicity does not extend across module boundaries.

This is a direct consequence of data segregation. When Module A's service calls Module B's facade, the two operations run in separate transaction contexts. There is no distributed transaction coordinator.

Module A Service → [local transaction] → Repository A
→ Facade B call → [separate transaction] → Repository B
→ Interceptor AFTER_ADD → async notification to Module C


#### Handling Cross-Module Consistency

The Pyramid Architecture addresses cross-module consistency through the **interceptor system** (Section 7) rather than distributed transactions:

- A `BEFORE_*` interceptor can validate cross-module preconditions before a primary operation commits.
- An `AFTER_*` interceptor can propagate side effects to other modules after a successful primary operation.
- Compensating interceptors on `AFTER_*_REMOVE` phases can undo the effects of prior operations.

This approach implements saga-like patterns: a sequence of operations across modules, each with a defined compensation. It does not provide ACID guarantees across modules. It does provide a practical toolbox for managing the most common cross-module consistency scenarios without the complexity and performance cost of distributed transaction coordination.

For the rare cases where ACID atomicity is genuinely required across what appear to be separate modules, the recommended resolution is to reconsider the module boundary. **If two operations must always be atomic, they likely belong in the same module.**

---

## 5. Services

### 5.1 CRUD Services

A **CRUD service** is the lowest-level service component. It is responsible for a single domain entity type and provides the standard lifecycle operations: add, update, remove, and the full set of query operations. CRUD services delegate directly to repositories without adding business logic.

CRUD services are prime candidates for code generation. Because their structure is entirely determined by the entity type and repository interface they manage, a generator can produce them automatically from the entity definition. All generated CRUD services follow the same structure and naming conventions.

*Architectural requirement:* a CRUD service must delegate to the repository interface, not to the concrete implementation. It must contain no business logic — no branching, no validation beyond structural null checks, no side effects. Business logic belongs in business services or interceptors.

### 5.2 Business Services

A **business service** is a service component that encapsulates non-trivial domain logic. It may coordinate multiple CRUD services and repositories, enforce business rules, manage complex state transitions, and interact with other modules through their facades.

Business services are always hand-written — they represent the intellectual content of the module and cannot be generated from structural definitions.

*Architectural requirement:* a business service must depend only on repository interfaces and the facades of other modules, never on concrete DAL implementations or the internal services of other modules.

### 5.3 Facade Services

The **module facade** is the single, unified entry point to a module's service layer. It is defined as an interface in `service-api` and implemented as an aggregating class that delegates to the module's CRUD and business services.

The facade interface is the module's **published contract** — the only surface that other modules are permitted to depend on. Everything inside the module is an implementation detail hidden behind this interface.

```
// Conceptual — not prescriptive syntax

Interface: BanksModule
    addBankAccount(account) → BankAccount
    updateBankAccount(account) → BankAccount
    removeBankAccount(id, strategy) → boolean
    findBankAccountById(id) → Optional<BankAccount>
    findBankAccountsByQuery(query) → List<BankAccount>
    findBankAccountRefById(id) → Optional<BankAccountRef>

Implementation: BanksModuleImpl
    delegates to: BankAccountService
    delegates to: BankAccountTypeService
    declares:  module name, hard dependencies, optional dependencies
```

The facade serves two distinct purposes simultaneously:

1. **Internal aggregation** — it presents the entire module as a single object to the WS layer above it.
2. **Inter-module contract** — it is the only interface through which other modules interact with this module.

#### Module Metadata

The facade implementation is the appropriate place to declare **module metadata**: the module's identifier, its hard dependencies (modules that must be present), and its optional dependencies (modules that, if present, enable additional behavior).

*Example: an expenses module declares a hard dependency on the banks module (bank accounts are required for expenses) and an optional dependency on the currencies module (currency conversion is enabled if currencies is present).*

This metadata serves both documentation and runtime validation purposes.

### 4.4 Exposed Services

An **exposed service** (WS layer component) translates the module facade into an external protocol. In a REST implementation, it maps HTTP verbs and paths to facade method calls. In a gRPC implementation, it maps RPC methods to facade calls.

Exposed services are thin by design — they perform protocol translation only, with no business logic. They are candidates for code generation from the facade interface definition.

*Architectural requirement:* an exposed service must depend only on the `service-api` interface, never on `service-impl`. It must contain no domain logic.

---

## 6. Extensions and Drivers

The Pyramid Architecture distinguishes three types of vertical components: **modules**, **extensions**, and **drivers**. This distinction reflects the difference between owning a business domain and providing a cross-cutting or pluggable capability.

### 6.1 Extensions

An extension is a cross-cutting architectural element that serves two complementary roles:
1. **Contract Publication**: It defines a typed API that specifies *what* capability is available to modules, without exposing implementation details.
2. **Orchestration Glue**: It provides the active coordination layer that manages driver selection, lifecycle handling, shared validation/fallback logic, and optional external WS exposure.

Extensions define the boundary of a capability without owning the business domain it serves. Modules depend exclusively on the extension's published contract; they never reference drivers, driver selection logic, or internal routing mechanisms.

| Extension        | Published Contract                           | Orchestration Responsibilities                                                            |
|------------------|----------------------------------------------|-------------------------------------------------------------------------------------------|
| File storage     | `store()`, `retrieve()`, `delete()`          | Driver routing, multipart handling, fallback to secondary storage, `/files/*` WS exposure |
| Sharing          | `shareWith()`, `revoke()`, `getRecipients()` | Channel normalization, delivery retry, rate limiting, `/webhooks/sharing` WS exposure     |
| Notifications    | `send()`, `template()`, `status()`           | Priority routing, channel fallback, deduplication, audit logging                          |
| Messaging broker | `publish()`, `subscribe()`, `ack()`          | Topic routing, consumer group management, dead-letter handling                            |
| Workflow / BPM   | `start()`, `complete()`, `queryState()`      | Process instance lifecycle, task routing, approval delegation                             |
| Language model   | `generate()`, `embed()`, `callTools()`       | Provider routing, quota management, local/cloud fallback, prompt templating               |

Extensions do not own persistent business data directly. When persistence is required, it is managed by the drivers that implement the extension's contract.

### 6.2 Drivers

A **driver** is a pluggable implementation of an extension's API. Drivers define *how* a capability is provided. Multiple drivers can implement the same extension, and the active driver is selected at application assembly time.

Drivers typically have a DAL layer — they persist their own operational data. Drivers do not typically have a WS layer — they are internal implementations, not external APIs.

```
Extension: file-storage-api
    Driver A: driver-fs-local      stores files on the local filesystem
    Driver B: driver-fs-s3         stores files in Amazon S3
    Driver C: driver-fs-sftp       stores files on a remote SFTP server

Extension: messaging-broker-api
    Driver A: driver-broker-memory     in-process event bus (development)
    Driver B: driver-broker-rabbitmq   RabbitMQ (production)
    Driver C: driver-broker-kafka      Kafka (high-throughput production)
```

Swapping drivers requires no code changes — only a change to the application manifest and the active classpath.

### 6.3 The Ports and Adapters Relationship

The extension/driver pattern is inspired by Ports and Adapters ((Hexagonal Architecture) pattern), but extends it to address multi-module, multi-tenant, and AI-first realities.


```
Module  →  Extension API  ←  Driver
              ↑
              │
      [Extension Glue Layer]
      • Driver selection & lifecycle
      • Shared validation / fallback logic
      • Optional WS exposure for direct consumption
```


This means that changing a driver — swapping RabbitMQ for Kafka, or a local filesystem for S3 — requires no changes to any module that uses the extension. The swap is invisible to the business logic.

**Key distinctions from classic Ports & Adapters:**

| Classic Ports & Adapters           | Pyramid Extension/Driver                                                          |
|------------------------------------|-----------------------------------------------------------------------------------|
| Port = passive interface           | Extension API = active contract + orchestration                                   |
| Adapter = direct implementation    | Driver = swappable implementation, isolated from business logic                   |
| Application wires port→adapter     | Extension glue wires API→driver at runtime via manifest                           |
| WS exposure belongs to application | Extension may expose its own WS endpoints when the capability is a system surface |


**Why this matters:**

- Tenant-aware driver selection: The extension glue can select drivers per tenant (e.g., driver-llm-local for regulated tenants, driver-llm-cloud for others) without module code changes.
- Fallback & resilience: The extension can implement retry logic, circuit-breaking, or fallback drivers transparently.
- First-class external surfaces: When a cross-cutting capability is itself an entry point (e.g., receiving webhooks, serving files), the extension's WS layer provides that surface without polluting module facades.

**Architectural rule**: Modules depend only on the Extension API. They never import Driver classes, never inspect driver selection logic, and never call extension WS endpoints directly (unless the extension is explicitly designed as a public surface). This preserves isolation while enabling rich cross-cutting behavior.


---

## 7. Deployment Models

### 7.1 The M/P Model

The M/P deployment model describes how M modules are distributed across P processes. Because all modules share the same technology stack, any subset of modules can be co-deployed in a single process.

```
M/1  →  all modules in one process        Modulith
         Suitable for: development, small teams,
         resource-constrained environments,
         initial product launch.

M/P  →  selected modules extracted        Selective microservices
         Suitable for: modules with distinct
         scaling needs, independent release
         cycles, or strict team boundaries.

1/P  →  one module per process            Full microservices
         Suitable for: large organizations,
         very high scale, maximum team autonomy.
```

The critical property: **the code is identical across all configurations**. Moving from M/1 to 1/P is an operational decision, not a development decision. No refactoring is required. The module code does not know or care how it is deployed.

### 7.2 The Assembly Layer

The assembly layer is the set of artifacts whose sole purpose is to compose modules, extensions, and drivers into deployable units. Assembly artifacts contain no business logic — they are pure dependency declarations.

A typical assembly structure contains two levels:

**Layer bundles** — intermediate artifacts that aggregate the dependencies of a specific layer across all included modules:

```
app-base-dal-jpa      aggregates all module-xxx-dal-jpa dependencies
app-base-dal-mongo    aggregates all module-xxx-dal-mongo dependencies
app-base-service      aggregates all module-xxx-service-impl dependencies
app-base-ws           aggregates all module-xxx-ws-rest dependencies
```

Layer bundles are reusable across packaging variants. They encapsulate the "which modules and which DAL/WS variant" decisions independently of the "what packaging" decision.

**Packaging variants** — the final deployable artifacts, each composing layer bundles for a specific target:

```
app-jar      embeds app-base-ws + app-base-dal-jpa
             produces a self-contained runnable JAR

app-war      same module composition as app-jar
             produces a WAR file for servlet container deployment

app-tool     embeds app-base-service only (no WS layer)
             produces a CLI or batch processing tool —
             no HTTP server, services accessible directly
```

Adding a new packaging target (native binary, serverless function, etc.) requires only a new packaging variant artifact — the layer bundles remain unchanged.

#### Tenant Configuration

In a multi-tenant deployment, the assembly layer is where per-tenant configuration lives. Each tenant has a configuration declaration that specifies:

- Which modules are active for that tenant.
- The tenant's data source connection.
- Which drivers are active (a tenant may use a different file storage driver than another).
- Any tenant-specific overrides.

The same deployed binary can serve tenants with different active module sets, different storage backends, and different extension/driver bindings, all within a single process. The module activation is resolved at startup per tenant.

### 7.3 Modulith to Microservices

The Pyramid Architecture provides a **mechanical path** from a modulith to a microservices deployment. The migration seam is the `service-api` interface — the facade contract.

When a module is extracted to a standalone process, its consumers' dependency on the facade interface is satisfied not by the local implementation but by a generated remote client:

```
Modulith:
    Module-A  →  BanksModule (interface)
                      ↓ wired as
                 BanksModuleImpl (local, same process)

Microservices:
    Module-A  →  BanksModule (interface)
                      ↓ wired as
                 BanksModuleRestClient (remote, HTTP call)
```

The calling code in Module A is **identical in both cases**. The only change is in the application manifest:

```
// Conceptual manifest snippet

modules:
    banks(svc: local)           same process
    edu(svc: remote-rest)       edu is a standalone service
    invoices(svc: local)        same process
```

The `service-rest-cli` sub-project — which may exist as an empty placeholder from the beginning — is the slot that gets populated when a module is extracted. The architectural decision to support extraction is made at design time by having the slot; the deployment decision is made at assembly time by filling it.

This makes the modulith-to-microservices migration a **deployment decision driven by operational requirements**, not a refactoring project driven by architectural debt.

---

## 8. Interceptors

The interceptor system is a first-class mechanism that allows modules to react to lifecycle events of entities in the same or other modules, without creating direct compile-time dependencies between those modules. It is the primary tool for cross-module side effects, extended reference freshness, and saga-like consistency patterns.

### 8.1 Entity Interceptors

An **entity interceptor** is triggered by CRUD lifecycle events on a specific entity type. It can react before the event (to enrich or validate), after the event (to propagate side effects), or after removal (to compensate).

| Phase               | Trigger                   | Typical Use                                                |
|---------------------|---------------------------|------------------------------------------------------------|
| `BEFORE_ADD`        | Before first persistence  | Enrich entity, validate business rules, resolve references |
| `BEFORE_UPDATE`     | Before update persistence | Same as BEFORE_ADD                                         |
| `AFTER_ADD`         | After successful creation | Propagate to other modules, update denormalized data       |
| `AFTER_UPDATE`      | After successful update   | Same as AFTER_ADD                                          |
| `AFTER_SOFT_REMOVE` | After logical deletion    | Notify dependent modules                                   |
| `AFTER_HARD_REMOVE` | After physical deletion   | Compensating actions, cleanup in dependent modules         |

A conceptual example — an interceptor on a financial transfer entity:

```
ON BEFORE_ADD / BEFORE_UPDATE of FncTransfer:
    → resolve the fiscal period from the transfer date
    → resolve the currencies of the debit and credit bank accounts
    → enrich the entity before persistence

ON AFTER_ADD / AFTER_UPDATE of FncTransfer:
    → post an accounting entry to the transactions module
    → notify any registered accounting loggers

ON AFTER_HARD_REMOVE of FncTransfer:
    → remove the corresponding accounting entry from the transactions module
```

This pattern allows the `expenses` module to automatically maintain consistency with the `transactions` module. The `transactions` module has no knowledge of `expenses`. The dependency arrow is unidirectional and the modules remain decoupled.

### 8.2 Query Interceptors

A **query interceptor** transforms a query object before it is executed against the data store. It expands or rewrites query criteria transparently — for example, expanding a hierarchical reference to include all descendants in a tree, or injecting tenant-specific filters.

```
// Conceptual example

Query interceptor on ExpenseQuery:
    input:  filter by institution = [branch-A]
    output: filter by institution = [branch-A, dept-1, dept-2, team-1, ...]

    (expansion performed by calling the institutions module facade
     to resolve the full organizational subtree)
```

Query interceptors are completely transparent to the caller. The caller constructs a query with a simple institution reference; the interceptor expands it before the database query executes. The caller never needs to know about the expansion logic or the organizational hierarchy.

### 8.3 Design Principles

**Order independence.** Interceptors are designed to be order-independent. Each interceptor should produce a correct result regardless of the order in which it executes relative to other interceptors on the same entity and phase. This forces interceptor authors to avoid hidden dependencies between interceptors — a stronger correctness guarantee than ordered execution. If ordering is genuinely required for a specific case, it should be expressed through an explicit priority mechanism rather than assumed.

**Single responsibility.** Each interceptor should handle one cross-cutting concern. An interceptor that enriches an entity, posts an accounting entry, sends a notification, and updates a cache is doing too much. Split it into separate interceptors with separate concerns.

**Failure handling asymmetry.** `BEFORE_*` interceptors may abort the operation by raising an exception — the primary operation has not yet committed. `AFTER_*` interceptors run after the primary operation has already committed; if an `AFTER_*` interceptor fails, the primary operation's effects are permanent. This asymmetry must be understood: `AFTER_*` interceptors should be designed to tolerate partial failure or to implement explicit compensating actions.

**Asynchrony.** `AFTER_*` interceptors that trigger side effects in other modules are candidates for asynchronous execution. If the side effect does not need to be synchronous — sending a notification, updating a dashboard metric — executing it asynchronously through the messaging broker extension improves responsiveness and reduces coupling.


### 8.4 Error Propagation Strategy

Interceptors and facade calls must distinguish between:
- **Recoverable errors** (validation failures, transient unavailability): returned as typed result objects, allowing callers to decide retry/fallback logic.
- **Non-recoverable errors** (invariant violations, contract breaches): propagated as unchecked exceptions or error-domain types that terminate the current operation.

This distinction enables resilient composition without leaking infrastructure concerns into business logic.


---

## 9. Infrastructure and Tooling

### 9.1 The Application Manifest

The **application manifest** is a single configuration file that serves as the composition root of the entire system. It is the authoritative declaration of:

- Which modules, extensions, and drivers are included in this deployment.
- The DAL variant for each component (relational, document, etc.).
- The WS variant for each component (REST, gRPC, etc.).
- The service topology for each module (local implementation or remote client).
- The application version, propagated to all generated artifacts.

The exact format of the manifest is implementation-defined — TSON, JSON, YAML, HCL, or a custom DSL. The semantic contract is format-agnostic.

A conceptual example:

```
// Application manifest — format is illustrative

application: my-erp
version: 1.0.0
default-dal: relational
default-ws: rest
default-svc: local

modules:
    banks                           uses all defaults
    invoices                        uses all defaults
    edu           dal: document     overrides dal to document store
    customers     svc: remote-rest  this module runs as a remote service

drivers:
    notifications    dal: auto      dal variant resolved at runtime
    sequences        dal: auto
    users-auth                      no dal needed

extensions:
    share            ws: auto       ws variant resolved at runtime
    file-storage     ws: auto
    dashboard        ws: auto
```

The manifest drives the code generator (Section 8.2). It also drives the application startup process, which uses it to validate that all declared dependencies are satisfiable before activating any module.

### 9.2 Code Generation

The Pyramid Architecture treats code generation as a first-class concern. Because the structure of CRUD services, repositories, query objects, WS controllers, and client proxies is entirely determined by entity and facade definitions, these artifacts are ideal candidates for generation.

A generator driven by the application manifest and entity/facade definitions can produce:

| Generated Artifact                            | Source                                   |
|-----------------------------------------------|------------------------------------------|
| Repository interfaces                         | Entity definition                        |
| CRUD service classes                          | Entity definition + repository interface |
| Query DSL classes                             | Entity definition                        |
| Entity reference classes                      | Entity definition                        |
| WS controllers (REST/gRPC)                    | Facade interface                         |
| Remote service client proxies                 | Facade interface                         |
| Frontend client code (TypeScript, Dart, etc.) | Facade interface + entity definitions    |
| LLM tool definitions                          | Facade interface                         |
| Application dependency manifests              | Application manifest                     |

Generated artifacts are clearly marked and are never manually edited. Business logic lives exclusively in hand-written classes: facade implementations, business services, and interceptors. This separation makes the generator **safe to rerun at any time** — regeneration overwrites generated artifacts without affecting hand-written business logic. The architecture is self-healing by construction.

#### Generated Dependency Management

In build systems with explicit dependency declarations, the number of dependency entries grows proportionally with the number of modules and variants. The generator can maintain a **marked section** within each dependency file, updating only its section while leaving manually managed dependencies intact:

```xml
<!-- manually managed dependencies -->
<dependency>...</dependency>

<!-- [GENERATED] DO NOT EDIT BELOW -->
<dependency>...module-banks-dal-jpa...</dependency>
<dependency>...module-invoices-dal-jpa...</dependency>
<!-- [/GENERATED] -->
```

Generated dependencies are kept as single-line entries — a deliberate choice that makes tool-based inspection (`grep`) return complete dependency information in one hit and produces clean, readable single-line diffs in version control history.

### 9.3 The Single Source of Truth Principle

The application manifest, the entity definitions, and the facade interfaces together form the **single source of truth** for the entire system. From these three inputs, the generator derives all generated artifacts. Nothing else needs to be manually maintained across multiple files.

When a new module is added, the developer writes:

1. The entity definitions (the business model).
2. The facade interface (the published contract).
3. The business services and interceptors (the domain logic).
4. One entry in the application manifest (the composition declaration).

Everything else — CRUD services, repositories, WS controllers, client proxies, frontend clients, dependency declarations — is generated automatically and kept in sync on each generator run.

---

## 10. Comparison with Other Architectures

| Property | Pyramid | Hexagonal | Clean Arch | Microservices | Monolith |
|---|---|---|---|---|---|
| Business decomposition | ✅ Modules | ⚠️ Implicit | ⚠️ Implicit | ✅ Services | ❌ None |
| Layer separation | ✅ Enforced | ✅ Enforced | ✅ Enforced | ⚠️ Per-service | ⚠️ Optional |
| Deployment flexibility | ✅ M/P model | ❌ None | ❌ None | ❌ Fixed | ❌ Fixed |
| Modulith→µService path | ✅ Mechanical | ❌ Manual | ❌ Manual | N/A | ❌ Rewrite |
| Code generation support | ✅ First-class | ❌ None | ❌ None | ⚠️ Ad-hoc | ❌ None |
| Cross-cutting concerns | ✅ Extensions+Drivers | ✅ Ports+Adapters | ⚠️ Use cases | ⚠️ Sidecar | ⚠️ Shared lib |
| Compile-time enforcement | ✅ Sub-project deps | ⚠️ Packages | ⚠️ Packages | ✅ Process boundary | ❌ None |
| Data segregation | ✅ Explicit | ⚠️ Implicit | ⚠️ Implicit | ✅ Per-service | ❌ Shared |
| Cross-module consistency | ✅ Interceptors/Sagas | ❌ Undefined | ❌ Undefined | ⚠️ Sagas (complex) | ✅ Transactions |
| AI-first readiness | ✅ Inherent | ⚠️ Partial | ⚠️ Partial | ⚠️ Partial | ❌ None |

### Relationship to Hexagonal Architecture

The Pyramid Architecture is, at the module level, a **strict superset** of Hexagonal Architecture. The extension API is the port; the driver is the adapter; the facade is the application boundary. The sub-project structure enforces the dependency rules that Hexagonal Architecture expresses only as conventions.

The Pyramid Architecture adds what Hexagonal Architecture does not address:

- A formal model for multi-module systems and inter-module communication rules.
- A continuous deployment flexibility model (M/P).
- A mechanical modulith-to-microservices transition path.
- A prescribed code generation strategy.
- Explicit data segregation and cross-module consistency patterns.

### Relationship to Domain-Driven Design

The Pyramid Architecture's module boundary corresponds closely to a DDD **bounded context**. The module facade corresponds to the **published language** at the context boundary. The extended reference pattern corresponds to DDD's guidance on cross-context entity references — holding only the information needed about a foreign entity rather than importing its full model.

The Pyramid Architecture does not prescribe DDD tactical patterns (aggregates, value objects, domain events) within a module. These remain design decisions for the module's team and are compatible with the Pyramid Architecture without being required by it.

### Relationship to Microservices

The Pyramid Architecture and microservices are not mutually exclusive. A Pyramid Architecture system can be deployed as full microservices (the 1/P model). The difference is in the default and the path.

Microservices architecture begins with process separation as the norm. The Pyramid Architecture begins with process co-location as the norm, with process separation as an option applied selectively. The Pyramid Architecture's position is that premature process separation is a significant source of operational complexity, network latency, and distributed consistency problems. Modules should be extracted to separate processes when there is a demonstrated operational need — not by default.

---

## 11. Known Limitations and Trade-offs

The Pyramid Architecture makes deliberate trade-offs. Acknowledging them is part of using it well.

### Cross-Module Query Performance

Strict data segregation means that queries spanning multiple modules cannot be expressed as single database queries. Service-level composition (calling multiple facades and joining in memory) has higher latency and memory overhead than a single optimized query. For most operational queries this is acceptable. For large-scale reporting and analytics, a dedicated read model is needed.

**Mitigation:** the extended reference pattern eliminates the most common cross-module display queries. A read model extension, updated via interceptors, addresses the reporting case.

### Cross-Module Transaction Atomicity

Operations spanning module boundaries do not share a transaction. If a primary operation succeeds and its cross-module side effect fails, the system is in a partially consistent state. The interceptor-based saga pattern manages this but does not eliminate the fundamental limitation.

**Mitigation:** saga patterns via interceptors handle the majority of real cases. When ACID atomicity is genuinely required across what appear to be module boundaries, the correct resolution is usually to reconsider the boundary — the two operations likely belong in the same module.

### Generator as a Single Point of Failure

The code generator concentrates significant architectural leverage. Generator bugs propagate to all generated code. Template changes require regeneration of all affected artifacts.

**Mitigation:** the generator should be independently versioned and well-tested. Generated code should be committed to version control so that regressions appear as diffs. Generated code should be human-readable so that bugs are visible in review.

### Learning Curve

The Pyramid Architecture introduces more structural concepts than a flat monolith. Teams face a learning curve understanding modules, facades, interceptors, extensions, drivers, and the assembly layer.

**Mitigation:** the structural consistency of the pattern works in its favor once internalized. Every module has the same anatomy. Every facade has the same role. Every interceptor has the same lifecycle. The investment in learning the pattern amortizes across the entire codebase.

---

## 12. Conclusion

The Pyramid Architecture offers a structured, practical answer to one of the most persistent challenges in software engineering: how to build a system that is simple today and scalable tomorrow, without committing prematurely to either a monolithic or a microservices deployment model.

Its key contributions are:

1. **A dual decomposition model** — vertical by business domain, horizontal by technical layer — that produces a clean, navigable structure at every scale.

2. **The M/P deployment model** — a continuous spectrum from full monolith to full microservices, traversable without code changes, making deployment topology an operational decision.

3. **A mechanical migration path** — the service API interface as a pre-engineered seam that makes modulith-to-microservices extraction a configuration change, not a refactoring project.

4. **The extension/driver pattern** — a formal model for cross-cutting concerns that keeps module code free of infrastructure dependencies and makes technology choices swappable at assembly time.

5. **Explicit data management patterns** — extended references, data segregation, and the interceptor system for cross-module consistency — each with an honest accounting of trade-offs.

6. **A code generation strategy** — that enforces architectural rules through tooling rather than discipline, making the architecture self-healing by construction and dramatically reducing the overhead of adding new modules.

The Pyramid Architecture is particularly well-suited to:

- **Startup environments**, where the team is small, the product is evolving rapidly, and premature microservices complexity is a liability.
- **ERP and domain-rich systems**, where the number of business modules is large, grows over time, and each module has a distinct owner and lifecycle.
- **Systems with variable deployment requirements**, where some clients require on-premise deployment, others SaaS, and resource constraints vary significantly.
- **Multi-tenant platforms**, where different tenants activate different module subsets against different data stores with different driver bindings.
- **AI-augmented systems**, where the clean facade structure provides a natural, typed tool surface for LLM-based agents.

The architecture does not eliminate complexity — no architecture does. It relocates complexity to where it belongs: in the business logic and in the application assembly, rather than in the structural scaffolding that surrounds it.

---

## 13. The Pyramid Architecture and AI-First Systems

The rise of Large Language Models as active participants in software systems — not merely as features but as autonomous agents capable of invoking business operations, proposing workflows, and learning from system history — introduces a new set of architectural requirements that most existing systems are poorly equipped to meet. The Pyramid Architecture, designed well before AI agents became a practical concern, turns out to satisfy these requirements naturally, as a consequence of its core structural properties rather than by deliberate AI-specific design.

### The Facade as a Tool Surface

An LLM-based agent interacts with a system through **tools** — typed, named, callable operations with defined inputs and outputs. The module facade is precisely this: a bounded, typed, named interface whose methods map directly to business operations. An agent given access to the `InvoicesModule` facade knows exactly what it can do, what parameters each operation requires, and what it returns. No additional abstraction layer is needed.

Because the code generator already produces a complete artifact catalog from facade interfaces — REST controllers, TypeScript clients, mobile clients — extending it to produce LLM tool definitions (JSON Schema, OpenAI function specifications, or equivalent) is a mechanical step using the same source.

```
Module facade interface
        ↓  (code generator)
REST controller             existing output
TypeScript client           existing output
Mobile client               existing output
LLM tool definition         new output, same source
```

The tool catalog is always in sync with the system's actual capabilities. Adding a method to the facade automatically adds it to the tool catalog on the next generator run. There is no risk of the agent believing it can call an operation that no longer exists.

### The Interceptor System as an Observation Layer

An AI agent that only acts on explicit user requests is reactive. A genuinely AI-first system requires **proactive** behavior: the agent observes what happens and initiates actions autonomously. The interceptor system provides exactly this observation layer. Every significant entity lifecycle event already passes through the interceptor chain. Registering an AI-aware interceptor requires no modification to the modules being observed.

```
// Conceptual: AI observation via existing interceptor infrastructure

ON AFTER_ADD of any entity:
    IF ai-module is active for this tenant:
        feed event to AI agent (asynchronously, non-blocking)
        → agent analyses context
        → agent proposes action graph if warranted
        → action graph enters approval workflow
```

The agent can be woken by any business event — a new invoice submitted, a period closed, an anomaly in bank reconciliation — and respond with proposed actions, without any modification to the module that produced the event.

### The Audit Trail as Training Data

The audit trail records every significant operation in the system: who did what, when, on which object, with what outcome. This constitutes a high-quality, domain-specific dataset of human decisions and their contexts.

Approved AI-proposed action graphs become positive training examples. Rejected proposals become negative examples. Modified proposals — where a human adjusted the AI's suggestion before approving — become refinement signals. Over time, the agent's proposals converge toward the patterns of the specific organization it serves, without requiring any external dataset or manual labeling. The audit trail is a byproduct of good operational practice; in an AI-first deployment, it becomes a learning asset.

### The Action Graph and BPM Integration

In an AI-first deployment, the agent does not execute operations directly. Instead, it proposes an **action graph** — a directed acyclic graph of typed facade method calls with explicit dependency relationships. Each node carries the method call, its arguments, and the agent's natural-language reasoning. Dependencies between nodes reflect business sequencing constraints.

```
AI proposes:
    Node 1: createFiscalPeriod(Q1-2025)
        reason: "no period exists for Q1-2025; required before posting"
            ↓ depends on
    Node 2: postTransactions([32 pending transactions])
        reason: "32 validated transactions await posting to Q1-2025"
            ↓ depends on
    Node 3: closeFiscalPeriod(Q1-2025)
        reason: "all transactions posted; period closure conditions met"
```

The action graph is presented to a human operator in a dashboard. The operator can inspect each node, ask the agent to explain its reasoning, modify node payloads, and approve or reject individually. Rejecting a node causes dependent nodes to be flagged for agent re-evaluation. Approved nodes execute as facade calls under a dedicated AI principal — subject to the same security, tenancy, and audit infrastructure as any human user.

Because each node is a typed facade call, the action graph maps naturally onto a **BPM process instance**: nodes become tasks, dependencies become sequence flows, approval becomes task completion, and execution becomes a service task. The BPM engine is a driver implementing a workflow extension — swappable between any compliant BPM engine or an in-memory variant for development.

Furthermore, the agent can **generate new BPM process definitions** from natural language descriptions, using the tool catalog as the vocabulary of available actions. A generated process definition is immediately valid — every action it references is a real, typed, callable facade method — and can be deployed to the BPM engine for repeated automated execution. This allows the AI to progressively extend the system's automation surface, subject to human approval at each step.

The self-reinforcing loop this creates:

```
Recurring situation observed (via interceptor or scheduler)
    ↓
Agent proposes action graph
    ↓
Human approves / refines / rejects
    ↓
Outcomes recorded in audit trail
    ↓
Agent recognizes recurrence, proposes reusable BPM process
    ↓
Human approves process deployment
    ↓
Future occurrences handled automatically by BPM
    ↓
Outcomes feed back into agent calibration
```

### Data Sovereignty and Local Deployment

AI-first systems that rely exclusively on cloud LLM APIs face a fundamental tension with the data sovereignty requirements of enterprise and institutional customers whose operational data cannot leave their premises. The extension/driver pattern addresses this directly: the LLM provider is itself a driver implementing a language model extension.

```
Extension: language-model-api
    Driver A: driver-llm-cloud-a      maximum capability, cloud
    Driver B: driver-llm-cloud-b      maximum capability, cloud
    Driver C: driver-llm-local        on-premise, data sovereignty
    Driver D: driver-llm-hybrid       local for routine tasks,
                                      cloud for complex reasoning
```

The active driver is selected per tenant at assembly time. A regulated tenant uses the local driver exclusively. An unrestricted tenant may use a cloud driver for higher capability. The agent code, the action graph, the BPM integration, and the audit trail are identical in both cases — only the inference backend differs. This makes Pyramid Architecture AI-first deployments viable in regulated industries where cloud-based AI has been blocked by compliance requirements.

### Summary

The Pyramid Architecture is well-suited to AI-first systems not because it was designed for AI, but because the properties that make it a good architecture for human developers — clean boundaries, typed interfaces, observable lifecycle events, comprehensive audit trails, swappable implementations — are precisely the properties that make a system legible and operable by an AI agent.

A system built on the Pyramid Architecture does not need to be redesigned to become AI-first. It needs, at most, three additions:

1. **A tool catalog generator output** — a new template for the existing generator, producing LLM tool definitions from facade interfaces. The generator infrastructure already exists.

2. **An action graph module** — a new first-class module managing proposed action graphs, their human approval lifecycle, BPM process instantiation, and execution under an AI principal.

3. **An LLM driver** — a new driver implementing the language model extension API, wired to the provider of choice per tenant. The driver pattern already exists.

Everything else — the facades, the interceptors, the audit trail, the BPM integration, the security model, the multitenancy — is already in place.

> A system built with discipline for human developers turns out to be, without redesign, a system that AI agents can understand, navigate, and extend. Architectural cleanliness is the precondition for AI-first capability.

---

## Adoption Check List

- □ Does your system have clear business domain boundaries?
- □ Do you anticipate needing to scale modules independently?
- □ Is data sovereignty or tenant isolation a requirement?
- □ Does your team value compile-time architectural enforcement?
- □ Are you building systems that may integrate AI agents?

If ≥3 boxes checked: Pyramid Architecture is likely a strong fit.

** Adoption Checklist by Face ** 

| Face        | Question to Ask                                                            | If No, Consider…                                                          |
|-------------|----------------------------------------------------------------------------|---------------------------------------------------------------------------------|
| Structural  | Are module boundaries clear and enforced at compile time?                  | Introduce sub-project isolation; define facade contracts                        |
| Connective  | Can infrastructure choices be swapped without code changes?                | Introduce extensions/drivers; refactor cross-cutting logic into interceptors    |
| Elastic     | Could any module be extracted to its own process tomorrow?                 | Ensure service-api is the only inter-module dependency; add remote client slots |
| Convergence | Could an AI agent reliably discover and invoke your system's capabilities? | Generate tool catalogs from facades; expose lifecycle events via interceptors   |



## Migration Path Table
For teams adopting incrementally:

| Current State        | First Step                                     | Next Milestone                        | Target        |
|----------------------|------------------------------------------------|---------------------------------------|---------------|
| Legacy monolith      | Extract one module with facade + dal-api       | Add interceptor for cross-module sync | M/1 modulith  |
| Ad-hoc microservices | Define facade interfaces for existing services | Introduce manifest-driven assembly    | M/P hybrid    |
| Greenfield project   | Start with module anatomy + generator          | Add extensions/drivers as needed      | Full Pyramid  |


## Reference Real-World Analogues
This pattern has been validated in systems managing ['ERP', 'Transport Information System', 'Financial System'] where module isolation enabled independent certification of safety-critical components.

## Document Governance

- **Current version**: 0.2 (draft)
- **Stability**: Concepts stable; examples illustrative
- **Feedback**: Contributions welcome via [link/process]
- **Citation**: If referencing this architecture, please cite as:  
  *Dr. Taha BEN SALAH. (2026). The Pyramid Architecture White Paper v1.0.*
