# Medication Administration Service API

## Overview
A healthcare-focused REST API for managing medication administrations with DynamoDB persistence, built with Micronaut and Kotlin following DDD principles.

**Base URL:** `http://localhost:8080`

**API Base Path:** `/medication`

Complete base URL for all API calls:
```
http://localhost:8080/medication
```

### Example Endpoint URLs:
- **Get patient administrations:** `http://localhost:8080/medication/patients/{patientId}/administrations`
- **Create administration:** `http://localhost:8080/medication/administrations`
- **Get administration by ID:** `http://localhost:8080/medication/administrations/{id}`
- **Update status:** `http://localhost:8080/medication/administrations/{id}/status`
- **Record administration:** `http://localhost:8080/medication/administrations/{id}/record`
- **Daily administrations:** `http://localhost:8080/medication/administrations/daily?date=2024-01-15`
- **Prescription administrations:** `http://localhost:8080/medication/prescriptions/{prescriptionId}/administrations`

## Endpoints

### Patient Administrations
- **GET** `/medication/patients/{patientId}/administrations`
  - Get all administrations for a specific patient

### Administration Management
- **POST** `/medication/administrations`
  - Create a new medication administration record
  - Body: `CreateAdministrationRequest`

- **GET** `/medication/administrations/{id}`
  - Get administration by ID

- **PUT** `/medication/administrations/{id}/status`
  - Update administration status
  - Body: `UpdateStatusRequest`

- **POST** `/medication/administrations/{id}/record`
  - Record completed administration
  - Body: `RecordAdministrationRequest`

### Reporting
- **GET** `/medication/administrations/daily?date={date}`
  - Get all administrations for a specific date

- **GET** `/medication/prescriptions/{prescriptionId}/administrations`
  - Get all administrations for a prescription

## Data Models

### CreateAdministrationRequest
```json
{
  "patientId": "123e4567-e89b-12d3-a456-426614174000",
  "prescriptionId": "456e7890-e89b-12d3-a456-426614174001",
  "scheduledTime": "2024-01-15T08:00:00Z",
  "dosageGiven": 500.0,
  "unit": "mg",
  "notes": "Take with food"
}
```

### AdministrationResponse
```json
{
  "administrationId": "789e0123-e89b-12d3-a456-426614174002",
  "patientId": "123e4567-e89b-12d3-a456-426614174000",
  "prescriptionId": "456e7890-e89b-12d3-a456-426614174001",
  "scheduledTime": "2024-01-15T08:00:00Z",
  "actualTime": "2024-01-15T08:05:00Z",
  "dosageGiven": 500.0,
  "unit": "mg",
  "administeredBy": "Nurse Johnson",
  "status": "COMPLETED",
  "notes": "Patient tolerated well",
  "createdAt": "2024-01-15T08:05:00Z"
}
```

### Administration Status
- `SCHEDULED` - Administration is scheduled
- `IN_PROGRESS` - Administration is in progress
- `COMPLETED` - Administration completed successfully
- `MISSED` - Administration was missed
- `CANCELLED` - Administration was cancelled
- `DELAYED` - Administration was delayed

## Features
- ✅ Complete CRUD operations for medication administrations
- ✅ Proper input validation with Jakarta Validation
- ✅ DynamoDB integration with proper access patterns
- ✅ Test environment isolation (in-memory repositories)
- ✅ CORS configuration for frontend integration
- ✅ Healthcare-specific audit logging
- ✅ Domain-driven design architecture
- ✅ Comprehensive test coverage (19 passing tests)

## Getting Started

### Prerequisites
- Docker (for DynamoDB-local)
- Java 17+
- AWS CLI (for table creation)

### Quick Start
```bash
# Set up local development environment (starts DynamoDB-local and creates tables)
./scripts/setup-local-dev.sh

# Run the application
./gradlew run
```

### Manual Setup
If you prefer to set up manually:

```bash
# Start DynamoDB-local
docker-compose up -d dynamodb-local

# Create the administrations table
./scripts/create-dynamodb-table.sh

# Run the application
./gradlew run
```

### Run Tests
```bash
./gradlew test
```

### Build
```bash
./gradlew build
```

### Stop Local Services
```bash
docker-compose down
```

The service will start on port 8080 with CORS enabled for frontend development.

**DynamoDB-local** runs on port 8000 and persists data in `./docker/dynamodb/` directory.