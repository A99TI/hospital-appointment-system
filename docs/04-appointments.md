# Phase 4 — Appointment Booking & Lifecycle

## ✅ Objective
Enable patients to book/cancel appointments against doctor schedules with correct validation and status handling.

---

## 1) Entity

### Appointment
Fields:
- id
- doctor (FK)
- patient (FK to User)
- appointmentDate (LocalDate)
- appointmentTime (LocalTime)
- status: BOOKED, CANCELLED, COMPLETED, NO_SHOW
- createdAt

Relationships:
- Doctor (1) → (many) Appointment
- User(Patient) (1) → (many) Appointment

---

## 2) APIs (Recommended)

### Patient booking
- POST /api/appointments
  Request:
- doctorId
- appointmentDate
- appointmentTime

Rules:
- Only PATIENT can book
- Doctor active
- Date not in the past
- Time fits schedule for that day
- Slot not already taken for that doctor/date/time
- If maxPatients enabled: do not exceed

### Cancel appointment (patient)
- POST /api/appointments/{appointmentId}/cancel
  Rules:
- Only the owning patient can cancel
- Cannot cancel COMPLETED
- Optional: cannot cancel within X minutes of appointment

### Doctor completes appointment
- POST /api/appointments/{appointmentId}/complete
  Rules:
- Only owning doctor can complete
- Must be BOOKED

### No-show
- POST /api/appointments/{appointmentId}/no-show
  Rules:
- Only doctor/admin
- Must be BOOKED

---

## 3) Queries / Listing Endpoints

- GET /api/appointments/me (patient’s appointments)
- GET /api/doctors/me/appointments?date=YYYY-MM-DD (doctor’s appointments)
- GET /api/admin/appointments?date=... (optional)

---

## 4) Core Validation Logic (Must-Haves)
1. AppointmentDate >= today
2. Schedule exists for (doctor, dayOfWeek)
3. appointmentTime within schedule start/end
4. Prevent double-booking:
    - unique (doctor_id, appointment_date, appointment_time) for BOOKED
5. Consistent status transitions

Recommended DB constraint:
- Unique index on (doctor_id, appointment_date, appointment_time)

---

## 5) Error Responses
- Doctor not found → 404
- Not within schedule → 400
- Slot already booked → 409 Conflict
- Not allowed (wrong user/role) → 403

---

## ✅ Acceptance Criteria
- Patient can book/cancel
- Doctor can complete/no-show
- Double booking prevented reliably
- Lists work for patient and doctor
