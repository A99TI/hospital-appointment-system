# Phase 1 — Foundation Setup (Project + MySQL + Conventions)

## ✅ Objective
Create a clean Spring Boot project with:
- MySQL persistence working
- Base folder structure
- Shared conventions for DTOs, errors, validation, and security integration

---

## 1) Dependencies
Spring Initializr:
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL Driver
- Validation
- Lombok
- Springdoc OpenAPI (Swagger)

---

## 2) Package Structure
Recommended:

com.hospital.system
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── exception
├── repository
├── security
├── service
└── HospitalApplication

---

## 3) MySQL Configuration

### application.yml (example)
- Create a MySQL DB (e.g. hospital_db)
- Configure datasource + JPA

Checklist:
- App starts successfully
- Tables generate (if using ddl-auto=update)
- You can create and fetch a record

---

## 4) Naming & API Conventions

### Endpoints
- Use nouns for resources: /doctors, /appointments, /schedules, /queue
- Use HTTP verbs correctly:
    - GET: read
    - POST: create/action
    - PUT/PATCH: update
    - DELETE: remove

### DTO Rules
- Controllers accept/return DTOs only (no Entities in API responses)
- Separate request and response DTOs

### Error Format (Recommended)
Consistent JSON structure:
- timestamp
- status
- error
- message
- path

---

## 5) Global Exception Handling
Add:
- @RestControllerAdvice
- Custom exceptions:
    - NotFoundException
    - BadRequestException
    - ForbiddenException
    - ConflictException

Map them to proper HTTP statuses.

---

## 6) Validation Standards
Use jakarta validation:
- @NotNull, @NotBlank, @Email, @Size
- @Min(1) for ids
- Validate request DTOs in controllers with @Valid

---

## ✅ Acceptance Criteria
- Project runs
- MySQL connects
- Basic entity persists
- Swagger UI opens and lists endpoints
- Standard error responses exist
