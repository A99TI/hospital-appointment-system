# 🏥 Hospital Appointment & Queue Management API — Overview

This project is a backend API for a hospital appointment system where:
- Patients can register/login and manage their account.
- Admins can manage users and system setup.
- Doctors have availability schedules.
- Patients can book/cancel appointments based on doctor availability.
- A queue/token system assigns patients an order for a doctor on a given day.

The system is built as a clean Spring Boot API with layered architecture:

Controller → Service → Repository → MySQL

---

## 🎯 Core Goals

1. **Secure access**
    - JWT auth
    - Role-based access (ADMIN, DOCTOR, PATIENT)
    - Only authorized users can perform actions

2. **Doctor availability**
    - Doctors have weekly schedules (day-of-week + time ranges)
    - Optional capacity limit per day (max patients)

3. **Appointments**
    - Patients book appointments only within doctor schedules
    - Double booking is prevented
    - Status-driven lifecycle: BOOKED → COMPLETED / CANCELLED / NO_SHOW

4. **Queue / Tokens**
    - When an appointment is booked, a token number is assigned
    - Tokens are per doctor per date
    - Queue status updates support doctor workflow (next patient, skip, complete)

---

## 👥 Roles & Permissions (Core)

### PATIENT
- Register/Login
- View/update own profile
- Book/cancel appointments
- View own appointments
- View own queue token & position

### DOCTOR
- View own schedule
- View appointments for self
- View queue for self and advance queue (complete/skip)

### ADMIN
- Manage users (list, delete non-admin, promote)
- (Optional) create doctor accounts / enable doctor role

---

## ✅ Definition of “Core Functionality Done”

Core is considered complete when:
- JWT auth works end-to-end with role-based endpoints
- Doctor schedules exist and are enforceable
- Patients can book/cancel appointments reliably
- Double booking is prevented
- Queue tokens are generated and can be advanced
- APIs are documented (Swagger) and tested manually via Postman
- Errors are consistent and validations are in place
