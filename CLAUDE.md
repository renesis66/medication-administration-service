# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a medication administration service designed to handle the secure tracking, scheduling, and administration of medications in healthcare settings. This is a safety-critical system that requires strict adherence to healthcare regulations and security standards.

## Development Setup

This project uses Gradle with Kotlin and Micronaut framework.

### Development Commands
- `./gradlew test` - Run all tests (should work without external dependencies)
- `./gradlew build` - Full build including tests
- `./gradlew run` - Run the application locally
- `./gradlew clean` - Clean build artifacts

## Required Dependencies

### Core Runtime Dependencies
```gradle
// YAML configuration support (required for application.yml)
runtimeOnly("org.yaml:snakeyaml")

// Kotlin coroutines (required for suspend functions)
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

// Logging implementation
runtimeOnly("ch.qos.logback:logback-classic")
```

### Why These Are Needed
- **snakeyaml**: Micronaut requires this for YAML configuration parsing
- **kotlinx-coroutines-core**: Required when using suspend functions in repositories
- **logback-classic**: Provides logging implementation for SLF4J

### Test Dependencies
```gradle
// JUnit 5 testing framework
testImplementation("org.junit.jupiter:junit-jupiter-api")
testImplementation("org.junit.jupiter:junit-jupiter-engine")
testImplementation("io.micronaut.test:micronaut-test-junit5")

// Kotlin-friendly testing utilities
testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
testImplementation("ch.qos.logback:logback-classic")
```

## Technology Stack Guidance

### Recommended Backend Technologies
- **Runtime**: JVM with Kotlin for type safety and healthcare application reliability
- **Framework**: Micronaut for dependency injection and microservice architecture
- **Database**: DynamoDB for production, in-memory implementations for testing
- **Data Access**: Custom repository pattern with environment-specific implementations
- **Authentication**: JWT with proper healthcare-grade security measures
- **Validation**: Built-in Micronaut validation with custom healthcare rules

### Recommended Frontend Technologies (if applicable)
- **Framework**: React with TypeScript for component-based UI
- **State Management**: Redux Toolkit or Zustand for complex medication workflows
- **UI Library**: Material-UI or Ant Design with healthcare accessibility standards

## Security Considerations

This service handles sensitive healthcare data and must implement:

### Data Protection
- Encrypt all PII and PHI data at rest and in transit
- Use environment variables for all secrets and API keys
- Implement proper role-based access control (RBAC)
- Log all medication administration events for audit trails

### Authentication & Authorization
- Multi-factor authentication for healthcare staff
- Session management with proper timeout policies
- Principle of least privilege for all user roles

### Compliance Requirements
- HIPAA compliance for US healthcare data
- Audit logging for all medication-related actions
- Data retention policies according to healthcare regulations

## Data Model Guidance

### Core Entities
- **Patient**: Demographics, allergies, medical conditions
- **Medication**: Drug information, dosage forms, contraindications
- **Prescription**: Doctor orders with specific dosing instructions
- **Administration Record**: When, who, what, how much was administered
- **Healthcare Staff**: Credentials, roles, permissions

### Critical Data Relationships
- Patient-to-prescription (one-to-many)
- Prescription-to-administration (one-to-many)
- Staff-to-administration (many-to-many with timestamps)

### Data Integrity Requirements
- Immutable administration records once created
- Versioning for prescription modifications
- Soft deletes to maintain audit trails

## API Design Principles

### RESTful Endpoints
- Use proper HTTP methods (GET, POST, PUT, DELETE)
- Consistent error response formats
- Pagination for large datasets
- Rate limiting to prevent abuse

### Critical Endpoints
- `POST /patients/{id}/medications/administer` - Record medication administration
- `GET /patients/{id}/medications/due` - Get upcoming medication schedule
- `GET /administrations/{id}/verify` - Verify administration record
- `POST /medications/reconcile` - Medication reconciliation process

### Response Standards
- Always include timestamps in ISO 8601 format
- Use proper HTTP status codes
- Include correlation IDs for request tracing

## Testing Framework Decision

### Chosen: JUnit 5 with Kotlin
- Better Kotlin integration and syntax
- More familiar to Kotlin developers
- Simpler setup for Kotlin projects

### Not Using: Spock 2
- Groovy-based (less optimal for Kotlin projects)
- Additional complexity for mixed-language projects
- Team preference for Kotlin-first testing

## Testing Guidelines

### Test Environment Setup
- Always exclude production dependencies (DynamoDB, external services) from test environment using `@Requires(notEnv = ["test"])`
- Create test-specific implementations with `@Requires(env = ["test"])`
- Use `application-test.yml` for test-specific configuration
- Include proper logging configuration (`logback-test.xml`) to reduce test noise

### Repository Testing Pattern
- Production repositories should use `@Requires(notEnv = ["test"])`
- Test repositories should use `@Requires(env = ["test"])` instead of `@Replaces`
- Factory configurations should also exclude test environment to avoid bean conflicts

### Test Architecture
- Tests use in-memory implementations
- No Docker/external services required for testing
- Environment-specific bean configuration prevents conflicts
- DDD patterns with proper domain isolation in tests

### Required Test Types
- **Unit Tests**: All business logic, especially dosage calculations
- **Integration Tests**: Repository operations with in-memory implementations
- **End-to-End Tests**: Critical medication administration workflows
- **Security Tests**: Authentication, authorization, input validation

### Testing Healthcare Logic
- Test medication interaction checking
- Validate dosage calculation accuracy
- Test allergy checking algorithms
- Verify proper error handling for safety-critical scenarios

## Compliance and Regulatory Guidance

### Healthcare Standards
- Follow HL7 FHIR standards for healthcare data interchange
- Implement proper medication coding (NDC, RxNorm)
- Ensure medication administration follows "Five Rights" principle

### Audit Requirements
- Log all medication administrations with WHO, WHAT, WHEN, WHERE, WHY
- Maintain immutable audit trails
- Implement proper data backup and recovery procedures

## Architecture Patterns

### Recommended Patterns
- **Repository Pattern**: For data access abstraction
- **Service Layer**: For business logic encapsulation
- **Event Sourcing**: For audit trail requirements
- **CQRS**: For read/write operation separation if needed

### Error Handling
- Never fail silently on medication-related operations
- Implement circuit breakers for external service calls
- Use structured logging for debugging and compliance

## Gradle Best Practices

### Always Include These Base Dependencies
```gradle
// Configuration support
runtimeOnly("org.yaml:snakeyaml")

// Kotlin async support
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

// Logging implementation
runtimeOnly("ch.qos.logback:logback-classic")
```

### Testing Framework Selection
- Use JUnit 5 for Kotlin projects
- Include MockK for Kotlin-friendly mocking when needed
- Avoid mixing Groovy (Spock) with Kotlin projects

### Dependency Management
- Always declare YAML parser explicitly (snakeyaml)
- Include coroutines support for async repository operations
- Ensure logging implementation is available at runtime

## Common Issues

### Missing Runtime Dependencies
- **snakeyaml missing**: Application fails to parse YAML configuration files
- **coroutines missing**: Suspend functions fail at runtime
- **logback missing**: No logging output or SLF4J warnings

### Dependency Injection Conflicts
- If tests fail with dependency injection errors, check `@Requires` annotations
- Ensure test environment properly isolates from production beans
- Verify logging configuration exists for both main and test
- Use `@Requires(notEnv = ["test"])` for production beans and `@Requires(env = ["test"])` for test beans

### Environment Configuration
- Always use environment-specific bean configuration to prevent conflicts
- Test implementations should never interfere with production code
- Micronaut's dependency injection requires explicit environment isolation

### DynamoDB Integration
- Exclude DynamoDB configuration from test environment
- Provide in-memory repository implementation for tests
- Ensure tests can run without external dependencies

## Project Generation Guidelines

When generating or extending this project, ensure:
- **All necessary runtime dependencies included**: snakeyaml, kotlinx-coroutines-core, logback-classic
- **JUnit 5 for testing** (not Spock) with Kotlin integration
- Proper test environment isolation from production dependencies
- In-memory test implementations for repositories
- Complete test configuration including logging setup
- Environment-specific bean configuration to avoid injection conflicts
- DDD principles with clear domain boundaries
- Micronaut dependency injection best practices

### Better Initial Prompt Example
"Generate a Micronaut project with:
- Kotlin as primary language
- JUnit 5 for testing (not Spock)
- All necessary runtime dependencies including YAML support
- Kotlin coroutines support for async operations
- Service-specific API prefix (e.g., `/medication`) instead of generic `/api/v1`
- CORS configuration for Vite (5173), React (3000), Angular (4200), and Vue (8080)
- Complete dependency list with no missing runtime requirements"

## Design Decisions

### API Design
- **Service-specific prefix**: Use `/medication` instead of `/api/v1` to avoid routing conflicts with other services
- **Frontend compatibility**: Patient service uses `/patients`, medication service uses `/medication/patients/{id}/administrations`
- **Clear separation**: Each microservice has its own URL namespace for better service isolation

### CORS Configuration
- **Vite support**: Added `http://localhost:5173` for Vite development server
- **Complete frontend coverage**: Supports React (3000), Angular (4200), Vite (5173), and Vue (8080)
- **Development-friendly**: All common frontend development ports included by default

### Environment Configuration
- **Strict test isolation**: Production beans use `@Requires(notEnv = ["test"])`
- **Test-specific implementations**: Test beans use `@Requires(env = ["test"])`
- **No external dependencies in tests**: All tests run with in-memory implementations

### DynamoDB Access Patterns
- **Primary access**: Query by patient ID using main table
- **GSI1**: Query administrations by prescription ID
- **GSI2**: Query administrations by date for reporting
- **Proper key design**: Follows single-table design patterns for optimal performance

### Healthcare Compliance
- **Audit logging**: Separate audit appender for compliance requirements
- **Immutable records**: Administration records cannot be deleted, only status updated
- **Type safety**: Strong typing with value classes for all domain identifiers

## Implementation Lessons Learned

### API Routing Conflicts
- **Problem**: Using generic `/api/v1` prefix can cause routing conflicts when multiple services run on same port
- **Solution**: Use service-specific prefixes (e.g., `/medication`, `/patients`) for clear service boundaries
- **Benefit**: Each service has its own namespace, preventing route collisions

### Frontend Development Support
- **Problem**: Default CORS configurations often miss modern development servers like Vite
- **Solution**: Proactively include all major frontend development ports in CORS configuration
- **Ports to include**: React (3000), Angular (4200), Vite (5173), Vue (8080)

### Test Environment Isolation
- **Problem**: DynamoDB and other production dependencies cause test failures without external services
- **Solution**: Use `@Requires(env = ["test"])` for test implementations and `@Requires(notEnv = ["test"])` for production
- **Result**: Tests run completely isolated with in-memory implementations

### Essential Dependencies
- **snakeyaml**: Required for YAML configuration parsing, often forgotten
- **kotlinx-coroutines-core**: Needed for suspend functions in repositories
- **logback-classic**: Provides actual logging implementation for SLF4J

## Notes

- This repository uses Micronaut with Kotlin and DDD principles
- All medication-related features must be implemented with safety as the highest priority
- Test isolation is critical - never allow production dependencies in test environment
- Consider healthcare interoperability standards from the beginning
- API endpoints use `/medication` prefix to avoid conflicts with other healthcare services
- CORS is configured for all major frontend development frameworks
- Update this file as development progresses to reflect actual project structure and commands