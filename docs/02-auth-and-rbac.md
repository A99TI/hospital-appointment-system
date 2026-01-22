# Phase 2 — Authentication & Role-Based Access (JWT)

## ✅ Objective
Implement secure authentication:
- Register
- Login
- JWT generation + validation
- Role-based access to endpoints

You already have:
- /api/auth/register
- /api/auth/login
- /api/users/info, /api/users/password, /api/users (delete)
- /api/admin/users, promote, delete

This phase ensures the implementation is solid and consistent.

---

## 1) Entities & Security Model

### User
Must include:
- id
- email (unique)
- password (hashed)
- role (ADMIN, DOCTOR, PATIENT)
- enabled
- createdAt

### Password Storage
- BCryptPasswordEncoder
- Never store plaintext passwords

---

## 2) JWT Requirements
Token should include:
- subject: user email or id
- role claim (optional but useful)
- expiry time

JWT flow:
1. Login returns token
2. Client uses Authorization: Bearer <token>
3. JwtFilter validates token and sets SecurityContext

---

## 3) RBAC (Role-based access)

### Route Security Examples
- /api/admin/** → ADMIN only
- /api/doctors/** → ADMIN, DOCTOR (depending on endpoint)
- /api/appointments/** → PATIENT for booking; DOCTOR for viewing own list
- /api/users/** → authenticated users

---

## 4) Current Controllers — Suggested Rules

### AuthenticationController
- register: creates PATIENT by default (unless admin creates others)
- login: returns token + basic user info

### UserController
- info: returns current user data
- delete: deletes current user (soft delete recommended; hard delete acceptable early)
- password update: verify old password then update to new hash

### AdminController
- list users
- promote to admin (guard carefully)
- delete non-admin user only

---

## 5) Quality Checks
- Duplicate email on register returns 409 Conflict
- Bad credentials returns 401 Unauthorized
- Disabled user cannot login
- Authenticated endpoints reject missing/invalid token

---

## ✅ Acceptance Criteria
- Register + login works
- Token secures endpoints
- Admin endpoints restricted properly
- Swagger shows security scheme
- Postman collection can demo:
    - register → login → call /api/users/info
    - admin login → call /api/admin/users
