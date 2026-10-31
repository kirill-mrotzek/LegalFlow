# LegalFlow

**Contract Lifecycle & Legal Risk Management Platform**

## Overview

LegalFlow is a backend Legal Tech platform for managing contracts, assessing contractual risks, providing explainable decision support, and managing legal review workflows.

The project demonstrates how legal and business rules can be translated into deterministic, explainable software logic and integrated into contract-related workflows.

LegalFlow is built with Java and Spring Boot and is designed as a foundation for future AI-assisted contract analysis.

## Why LegalFlow?

Legal departments often need to:

- evaluate contractual risks
- determine the appropriate level of legal review
- manage contract lifecycle states
- control approval workflows
- document important decisions
- maintain an audit trail
- identify upcoming contractual deadlines

LegalFlow models these processes as explicit business rules, risk factors, decision-support logic, workflow state transitions, and governance mechanisms.

The project demonstrates the practical intersection of:

- Legal expertise
- Legal Tech
- Backend Engineering
- Business Rule Engineering
- Contract Risk Management
- Workflow & Governance Engineering

---

## Key Features

### Contract Management

- Contract CRUD
- Contract search
- Filtering
- Pagination
- Contract status management
- Contract lifecycle management
- Contract renewal
- Upcoming contract deadlines

### Risk Management

- Explainable contractual risk assessment
- Configurable risk rules
- Type-safe risk factor model
- Risk scoring
- Risk levels: LOW / MEDIUM / HIGH
- Risk assessment history
- Risk dashboard

### Decision Support

- Explainable recommendations
- Decision support based on identifiable risk factors
- Legal review classification
- Standard / Enhanced legal review
- Decision history

### Review Workflow

- Review lifecycle management
- Explicit state transitions
- Start / approve / reject review actions
- Role-based authorization
- Review governance

### Security & Governance

- Spring Security
- HTTP Basic authentication
- Role-based access control
- Method-level authorization
- Fine-grained authorities
- Audit trail
- Security-aware business operations

### API & Engineering

- REST API
- OpenAPI / Swagger documentation
- Centralized API error handling
- Bean Validation
- Automated testing
- PostgreSQL persistence

---

# Architecture

```text
                         REST API
                            │
                            ▼
                  ┌───────────────────┐
                  │   Controllers      │
                  └─────────┬─────────┘
                            │
             ┌──────────────┼──────────────┐
             ▼              ▼              ▼
      Contract Management  Risk        Decision Support
             │           Assessment           │
             │              │                 │
             │              ▼                 │
             │         Risk Engine             │
             │              │                 │
             │              ▼                 │
             │      Risk Assessment            │
             │              │                 │
             └──────────────┼─────────────────┘
                            │
                            ▼
                  Legal Review Decision
                            │
                            ▼
                    Review Workflow
                            │
                            ▼
                     Human Decision
                            │
                            ▼
                  Audit / History Layer
                            │
                            ▼
                       PostgreSQL
```

The application separates several business concerns:

**Risk Assessment** — identifies and explains contractual risks.

**Decision Support** — translates identified risks into recommended organizational actions.

**Legal Review Decision** — determines the required level of legal review.

**Contract Lifecycle** — manages contractual lifecycle states.

**Review Workflow** — manages legal review state transitions.

**Governance & History** — records important decisions, risk assessments, renewals, and audit events.

---

# Risk Assessment

LegalFlow evaluates contracts using configurable risk rules.

Current risk factors include:

- High contract value
- Auto-renewal
- Long-term contract
- Foreign governing law within the EU
- Foreign governing law outside the EU
- Unlimited liability

Each risk factor contains:

- a type-safe `RiskFactorCode`
- risk points
- an explanation
- a reason

The resulting assessment contains:

- risk score
- risk level
- identifiable risk factors
- explanations

Risk thresholds and selected rule parameters are externalized through application configuration.

The core legal and business decision logic remains implemented in Java.

### Risk Levels

```text
LOW
 │
 ├──────────────► MEDIUM
 │
 └──────────────► HIGH
```

The risk engine is deterministic and explainable: the system can identify which rules contributed to the resulting risk assessment.

---

# Decision Support

The decision-support layer transforms risk assessment results into actionable recommendations.

It can provide:

- recommended action
- rationale
- priority
- required approval roles
- next action

The model explicitly distinguishes between:

### Risk Assessment

**What risks exist?**

### Decision Support

**What organizational action should be considered based on those risks?**

The recommendations are explainable and based on identifiable risk factors rather than opaque scores.

---

# Legal Review

LegalFlow determines the required level of legal review based on contract characteristics and identified legal risks.

The system distinguishes between:

- Standard Review
- Enhanced Review

The legal review decision is stored together with:

- review type
- reason
- decision timestamp
- decision maker

Historical decisions are preserved separately from the current contract state.

---

# Review Workflow

Legal review is modeled as a separate workflow from the contract lifecycle.

Current review states:

```text
PENDING
   │
   ▼
IN_REVIEW
   │
   ├──────────► APPROVED
   │
   └──────────► REJECTED
```

Available review actions include:

- Start review
- Approve review
- Reject review

The system validates allowed state transitions.

The project deliberately separates:

- contract lifecycle state
- legal review state
- legal review type

This prevents workflow state from being confused with the contractual lifecycle.

---

# Contract Lifecycle

The contract lifecycle is modeled independently from the review workflow.

Current lifecycle states:

```text
DRAFT
  │
  ▼
SIGNED
  │
  ▼
ACTIVE
  ├──────────► EXPIRED
  │
  └──────────► TERMINATED

EXPIRED ──────► ARCHIVED
TERMINATED ───► ARCHIVED
```

Invalid lifecycle transitions are rejected by the application.

Contract expiry is evaluated based on the contractual end date.

Contract renewal is modeled as a separate business operation: renewal extends the contractual end date without incorrectly treating renewal as a lifecycle state transition.

---

# Deadlines & Risk Dashboard

LegalFlow provides a query layer for upcoming contractual deadlines.

Upcoming deadlines can be queried based on a configurable time window.

The risk dashboard provides an aggregated view including:

- total contracts
- high-risk contracts
- medium-risk contracts
- low-risk contracts
- pending reviews
- expiring contracts

The dashboard composes existing business services rather than duplicating their underlying business logic.

---

# History & Audit Trail

LegalFlow maintains historical records for important business decisions.

Current history components include:

- Risk Assessment History
- Legal Review Decision History
- Contract Renewal History
- Audit Trail

The audit trail records important business operations such as:

- contract creation
- contract updates
- contract deletion
- contract renewal
- review start
- review approval
- review rejection
- lifecycle status changes

Audit actors are derived from the authenticated security context rather than supplied by the client request.

This creates a foundation for governance, traceability, and security-aware decision workflows.

---

# Security

LegalFlow uses Spring Security for authentication and authorization.

The current implementation includes:

- HTTP Basic authentication
- role-based access control
- method-level authorization
- fine-grained authorities
- security-aware service methods
- security integration tests

The authorization model distinguishes between different business responsibilities, including:

- Legal
- Finance
- Management
- Admin

Authorization is expressed through business-specific authorities such as:

```text
CONTRACT_READ
CONTRACT_CREATE
CONTRACT_UPDATE
CONTRACT_DELETE
CONTRACT_STATUS_CHANGE

REVIEW_START
REVIEW_APPROVE
REVIEW_REJECT
REVIEW_STATUS_CHANGE

FINANCIAL_APPROVE
MANAGEMENT_APPROVE
COMPLIANCE_REVIEW
```

The current authentication implementation is intentionally lightweight and serves as the security foundation for future application-level authentication.

---

# Technology Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Bean Validation
- Spring Security
- Maven
- Lombok
- MapStruct
- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- OpenAPI / Swagger

H2 was used during early development but has been removed from the current implementation.

---

# API

Main API areas include:

| Area | Endpoint |
|---|---|
| Contracts | `/contracts` |
| Risk Assessment | `/contracts/{id}/risk-assessment` |
| Decision Support | `/contracts/{id}/decision-support` |
| Contract Lifecycle | `/contracts/{id}/status` |
| Review Workflow | `/contracts/{id}/review/*` |
| Risk Dashboard | `/dashboard/risk` |

OpenAPI documentation is available through Swagger UI when the application is running.

---

# Running the Application

## Requirements

- Java 21
- Maven
- PostgreSQL 17 or compatible PostgreSQL version

## Database

Create a PostgreSQL database and configure the application connection in:

```text
src/main/resources/application.properties
```

The database password should be supplied through an environment variable rather than committed to source control.

Example:

```text
LEGALFLOW_DB_PASSWORD
```

## Start the application

```bash
mvn spring-boot:run
```

Or build and run:

```bash
mvn clean package
java -jar target/legalflow-*.jar
```

---

# Testing

LegalFlow uses automated tests at several levels:

- Unit tests
- Service tests
- Repository tests
- Controller tests
- Integration tests
- Security tests
- API validation tests
- Error handling tests
- State transition tests

The current test suite contains:

**314 tests — 0 failures — 0 errors**

The integration tests use a dedicated PostgreSQL test database.

The project therefore validates not only business logic in isolation, but also persistence, REST integration, security, workflow transitions, and database interactions.

---

# Swagger / OpenAPI

When the application is running, Swagger UI is available at:

`http://localhost:8080/swagger-ui/index.html`

---

# Example Workflow

A typical LegalFlow workflow:

```text
Create Contract
      │
      ▼
Risk Assessment
      │
      ▼
Decision Support
      │
      ▼
Legal Review Classification
      │
      ├── Standard Review
      │
      └── Enhanced Review
      │
      ▼
Legal Review Workflow
      │
      ├── Approve
      │
      └── Reject
      │
      ▼
Contract Lifecycle
      │
      ▼
Signed / Active Contract
      │
      ├── Renewal
      │
      ├── Expiry
      │
      └── Termination
      │
      ▼
Audit & History
```

The current implementation focuses on deterministic and explainable business logic.

AI-assisted contract analysis is planned as a subsequent development phase.

---

# Project Status

**Current phase: Production-oriented backend foundation**

Implemented:

- Contract management
- Contract search and filtering
- Risk engine
- Configurable risk rules
- Risk API
- Risk assessment history
- Decision support
- Explainable recommendations
- Legal review classification
- Legal review workflow
- Contract lifecycle management
- Contract expiry
- Contract renewal
- Upcoming deadlines
- Risk dashboard
- Audit trail
- Decision history
- Role-based access control
- Method-level authorization
- PostgreSQL persistence
- Dedicated REST controllers
- OpenAPI documentation
- Centralized error handling
- Automated test coverage

Current test status:

**314 / 314 tests passing**

---

# Roadmap

## Contract Intelligence

- Contract document model
- PDF / text extraction
- Clause analysis
- Rule-based document analysis

## AI / Legal AI

- LLM integration
- Embeddings
- Vector database
- Retrieval-Augmented Generation (RAG)
- AI-assisted contract risk analysis
- AI-assisted clause analysis
- AI Legal Agent

The AI layer will be introduced on top of the existing deterministic architecture rather than replacing it.

---

# Project Structure

The application follows a layered Spring Boot architecture.

```text
src/
├── main/
│   ├── java/
│   │   └── .../
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── model/
│   │       ├── mapper/
│   │       ├── repository/
│   │       ├── risk/
│   │       ├── service/
│   │       ├── security/
│   │       ├── lifecycle/
│   │       └── specification/
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    ├── java/
    │   └── .../
    │
    └── resources/
        └── application.properties
```

The controller layer is separated by business responsibility:

- `ContractController` — contract CRUD and search
- `RiskController` — risk assessment
- `DecisionSupportController` — decision support
- `ContractLifecycleController` — contract lifecycle
- `ReviewController` — review workflow
- `RiskDashboardController` — risk dashboard

---

# Design Principles

LegalFlow is built around several design principles:

- Separation of business concerns
- Explicit domain models
- Type-safe risk factor representation
- Explainable business decisions
- Configurable risk thresholds
- Explicit state transitions
- Separation of lifecycle and workflow states
- Validation at API and business levels
- Role-based authorization
- Security-aware business operations
- Auditability and historical traceability
- Centralized error handling
- Automated testing
- Incremental architecture evolution

The project intentionally separates deterministic business logic from the future AI layer.

This allows AI-assisted functionality to be introduced without making core contractual workflows dependent on an opaque model.

---

# Future Legal AI Architecture

The planned AI layer will extend the existing deterministic risk and workflow architecture.

The intended direction includes:

```text
Contract Document
      │
      ▼
Text Extraction
      │
      ▼
Chunking
      │
      ▼
Embeddings
      │
      ▼
Vector Database
      │
      ▼
Retrieval
      │
      ▼
RAG / LLM
      │
      ▼
AI-Assisted Analysis
      │
      ▼
Legal Risk & Decision Support
```

The AI layer is planned and is not yet part of the current implementation.

The architectural goal is to combine:

**Deterministic Legal Logic**

with

**AI-Assisted Contract Intelligence**

while keeping the final business workflow explainable, auditable, and controllable.