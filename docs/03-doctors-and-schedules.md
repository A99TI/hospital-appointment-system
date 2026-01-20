# Phase 3 — Doctors & Scheduling

## ✅ Objective
Add doctor identity + weekly availability rules so appointments can be validated.

---

## 1) Entities

### Doctor
Suggested fields:
- id
- user (FK to User) — doctor account
- fullName
- specialization
- roomNumber
- active

Relationship:
- Doctor (1) ↔ (1) User
- Doctor (1) → (many) Schedule entries

### Schedule
Weekly recurring schedule:
- id
- doctor (FK)
- dayOfWeek (MONDAY…SUNDAY)
- startTime
- endTime
- maxPatients (optional but recommended)

Rules:
- startTime < endTime
- no overlapping schedule windows for the same doctor/day (important!)

---

## 2) APIs (Recommended)

### Admin / Doctor creation
Option A (simple):
- Admin creates doctor user account, role=DOCTOR, then creates Doctor profile.

Endpoints:
- POST /api/admin/doctors
- PUT  /api/admin/doctors/{doctorId}/activate

Option B:
- Admin promotes an existing user to DOCTOR and then creates Doctor profile.

---

## 3) Schedule APIs

### Create/Update schedule
- POST /api/doctors/{doctorId}/schedules
- PUT  /api/doctors/{doctorId}/schedules/{scheduleId}
- DELETE /api/doctors/{doctorId}/schedules/{scheduleId}

### View schedule
- GET /api/doctors/{doctorId}/schedules
- GET /api/doctors/me/schedules (for logged-in doctor)

---

## 4) Scheduling Rules to Implement
These rules must exist before appointments:

1. **Doctor must be active**
2. **Schedule must exist for the given dayOfWeek**
3. **Time must fall within at least one schedule window**
4. **No overlapping schedule windows for doctor/day**
5. Optional: enforce `maxPatients` per day

---

## 5) Testing Scenarios
- Create doctor
- Add schedule Monday 09:00–12:00
- Reject schedule Monday 11:00–13:00 (overlap)
- Get schedule list
- Deactivate doctor → appointments should fail

---

## ✅ Acceptance Criteria
- Doctor profile exists and is linked to a DOCTOR user
- Schedules can be CRUD’d
- Overlaps are prevented
- APIs are secured by role rules
