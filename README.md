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
- Docker or Rancher Desktop (for DynamoDB-local)
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
If you prefer to set up manually or encounter Docker issues:

#### Step 1: Start Docker/Rancher Desktop
If using Rancher Desktop:
```bash
open -a "Rancher Desktop"
# Wait for Rancher Desktop to fully start (may take 30-60 seconds)
```

#### Step 2: Start DynamoDB Local
```bash
# Remove any existing containers if needed
docker rm dynamodb-local

# Start DynamoDB-local
docker-compose up -d

# Verify DynamoDB is running
curl -s http://localhost:8000 | head -5
```

#### Step 3: Create Database Table
```bash
# Create the administrations table with GSI indexes
./scripts/create-dynamodb-table.sh
```

#### Step 4: Start the Service
```bash
# If port 8080 is in use, kill the process first
lsof -i :8080  # Check what's using port 8080
kill <PID>     # Kill the process if needed

# Run the application
./gradlew run
```

### Troubleshooting Docker Issues
If you encounter Docker daemon connection errors:

1. **Check Docker Context:**
   ```bash
   docker context ls
   docker context use desktop-linux  # If using Rancher Desktop
   ```

2. **Verify Docker is Running:**
   ```bash
   docker ps  # Should show running containers
   ```

3. **Alternative DynamoDB Setup:**
   If Docker continues to have issues, you can run DynamoDB Local directly:
   ```bash
   # Download DynamoDB Local JAR and run directly
   java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
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