# LegalFlow

**Contract Lifecycle & Legal Risk Management Platform**

## Overview

LegalFlow is a backend Legal Tech platform for managing contracts,
assessing contractual risks, providing explainable decision support,
and managing legal review workflows.

The project demonstrates how legal and business rules can be translated
into deterministic and explainable software logic and integrated into
contract-related workflows.

LegalFlow is built with Java and Spring Boot and is being extended
toward AI-assisted contract analysis.

## Why LegalFlow?

Legal departments often need to evaluate contractual risks,
determine the appropriate level of legal review, and document
decision-making processes.

LegalFlow models these processes as explicit business rules,
risk factors, decision-support logic, and workflow state transitions.

The goal is to demonstrate the practical intersection of:

- Legal expertise
- Legal Tech
- Backend Engineering
- Business Rule Engineering
- Contract Risk Management

## Key Features

- Explainable contractual risk assessment
- Configurable risk rules
- Decision support based on identifiable risk factors
- Legal review classification
- Contract review workflow management
- Contract lifecycle management
- Type-safe risk factor model
- Contract CRUD, search, filtering and pagination
- REST API
- OpenAPI documentation
- Centralized API error handling
- Automated testing

## Architecture

```text
                        REST API
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
 Contract Management   Risk Assessment    Decision Support
        │                   │                   │
        │                   ▼                   │
        │              Risk Engine              │
        │                   │                   │
        │                   ▼                   │
        │             Risk Assessment           │
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                            ▼
                  Legal Review Decision
                            │
                            ▼
                    Review Workflow
                            │
                            ▼
                     Human Decision                       
```

The application separates several business concerns:

Risk Assessment — identifies and explains contractual risks.
Decision Support — translates identified risks into recommended organizational actions.
Legal Review Decision — determines the required level of legal review.
Contract Lifecycle — manages the contractual lifecycle state.
Review Workflow — manages the legal review process and its state transitions.

## Risk Assessment

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

The resulting assessment contains a risk score and risk level.

Risk thresholds are externalized through application configuration,
while legal and business decision rules remain implemented in Java.

## Decision Support

The decision-support layer transforms risk assessment results
into actionable recommendations.

It can provide:

- recommended action
- rationale
- priority
- required approval roles
- next action

The model distinguishes between:

**Risk Assessment**

What risks exist?

**Decision Support**

What organizational action should be considered based on those risks?

The recommendations are explainable and based on identifiable
risk factors rather than opaque scores.

## Review Workflow

Legal review is modeled as a separate workflow from the
contract lifecycle.

Current review states:

```text
PENDING
   │
   ▼
IN_REVIEW
   │
   ├──────► APPROVED
   │
   └──────► REJECTED
```

Available review actions:

- Start review
- Approve review
- Reject review

The system validates allowed state transitions.

The project also distinguishes between:

- contract lifecycle state
- legal review state
- legal review type

This separation prevents workflow state from being confused
with the contractual lifecycle.

## Contract Lifecycle

The contract lifecycle is modeled independently from the
review workflow.

Current lifecycle states include:

```text
DRAFT
  │
  ▼
SIGNED
  │
  ▼
ACTIVE
  ├──────► EXPIRED
  │
  └──────► TERMINATED

EXPIRED ──────► ARCHIVED
TERMINATED ───► ARCHIVED
```

Invalid lifecycle transitions are rejected by the application.

## Technology Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Bean Validation
- H2
- Maven
- Lombok
- MapStruct
- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- OpenAPI / Swagger

## API
Main API areas include:

| Area | Endpoint |
|---|---|
| Contracts | `/contracts` |
| Risk Assessment | `/contracts/{id}/risk-assessment` |
| Decision Support | `/contracts/{id}/decision-support` |
| Contract Lifecycle | `/contracts/{id}/status` |
| Review Workflow | `/contracts/{id}/review/*` |

OpenAPI documentation is available through Swagger UI
when the application is running.

## Running the Application

### Requirements

- Java 21
- Maven

### Start the application
```bash
mvn spring-boot:run
```

Or build and run the application:

```bash
mvn clean package
java -jar target/legalflow-*.jar
```

## Example Workflow

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
Legal Review
      │
      ├── Standard Review
      │
      └── Enhanced Review
      │
      ▼
Review Decision
      │
      ▼
Contract Lifecycle
      │
      ▼
Signed / Active Contract
```

The current implementation focuses on deterministic,
explainable business logic.

AI-assisted contract analysis is planned as a subsequent
development phase.

## Testing

The project uses automated tests at several levels:

- Unit tests
- Service tests
- Repository tests
- Controller tests
- API validation tests
- Error handling tests
- State transition tests

Current test suite: **220 tests, 0 failures**

## Swagger / OpenAPI

When the application is running, Swagger UI is available at:

`http://localhost:8080/swagger-ui/index.html`

## Project Status

**Current phase:** Core Risk, Decision Support and Review Workflow

**Implemented:**

- Contract management
- Risk engine
- Configurable risk rules
- Risk API
- Decision support
- Explainable recommendations
- Legal review classification
- Contract lifecycle
- Review workflow
- Dedicated REST controllers
- OpenAPI documentation
- Centralized error handling
- Automated test coverage

The next development phase focuses on application security
and access control.

## Roadmap

### Security & Access Control

- Spring Security
- Authentication
- Role-Based Access Control
- Method-level authorization
- Security tests

### Governance
- Audit trail
- Review history
- Approval governance
- Security-aware decision workflows

### Contract Intelligence
- Contract document model
- PDF/text extraction
- Clause analysis
- Rule-based document analysis

### AI / Legal AI
- LLM integration
- Chunking
- Embeddings
- Vector database
- Retrieval-Augmented Generation (RAG)
- AI-assisted contract risk analysis
- AI Legal Agent

## Project Structure

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
│   │       └── specification/
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── .../
```

The controller layer is separated by business responsibility:

- `ContractController` — contract CRUD and search
- `RiskController` — risk assessment
- `DecisionSupportController` — decision support
- `ContractLifecycleController` — contract lifecycle
- `ReviewController` — review workflow

## Design Principles

LegalFlow is built around several design principles:

- Separation of business concerns
- Explicit domain models
- Type-safe risk factor representation
- Explainable business decisions
- Configurable risk thresholds
- Explicit state transitions
- Validation at API and business levels
- Centralized error handling
- Automated testing
- Incremental architecture evolution

The project intentionally separates deterministic business
logic from the future AI layer.

This allows AI-assisted functionality to be introduced without
making core contractual workflows dependent on an opaque model.

## Future Legal AI Architecture

The planned AI layer will extend the existing deterministic
risk and workflow architecture.

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

The AI layer is planned and is not yet part 
of the current implementation.