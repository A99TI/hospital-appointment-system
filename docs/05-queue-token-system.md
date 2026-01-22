# Phase 5 — Queue / Token System (Core)

## ✅ Objective
Create a daily queue per doctor where each booked appointment gets a sequential token number.

This is NOT real-time yet; it’s standard REST-based queue management.

---

## 1) Entity

### QueueToken
Fields:
- id
- doctor (FK)
- appointment (FK, one-to-one)
- tokenNumber (int)
- queueDate (LocalDate)
- status: WAITING, ACTIVE, COMPLETED, SKIPPED

Relationships:
- Doctor (1) → (many) QueueToken (per date)
- Appointment (1) ↔ (1) QueueToken

Rules:
- For a doctor and date, tokenNumber increments (1,2,3,...)

---

## 2) Token Creation Rule
When an appointment is BOOKED:
- Create QueueToken for the same doctor + appointmentDate
- tokenNumber = (max tokenNumber for doctor/date) + 1
- status = WAITING

When appointment is CANCELLED:
- Decide policy:
    - Option A: Mark token SKIPPED (recommended)
    - Option B: Delete token (simpler but less auditable)

---

## 3) Queue APIs (Recommended)

### Patient view own token
- GET /api/queue/me?date=YYYY-MM-DD&doctorId=123
  Response:
- tokenNumber
- status
- position in queue (computed)

### Doctor views queue for a date
- GET /api/doctors/me/queue?date=YYYY-MM-DD
  Response:
- ordered list of tokens with statuses + patient info

### Doctor advances queue
- POST /api/doctors/me/queue/{tokenId}/start
    - sets token ACTIVE (only if current ACTIVE none, or enforce one active at a time)

- POST /api/doctors/me/queue/{tokenId}/complete
    - sets COMPLETED
    - optionally auto-activate next WAITING token

- POST /api/doctors/me/queue/{tokenId}/skip
    - sets SKIPPED

---

## 4) Queue Business Rules (Must-Haves)
1. Tokens are ordered by tokenNumber
2. Only doctor can manage their queue
3. One ACTIVE at a time (recommended)
4. Position calculation:
    - count tokens with status WAITING and tokenNumber < my tokenNumber
    - OR include ACTIVE ahead if needed

---

## 5) Data Integrity Suggestions
- Unique constraint: (appointment_id) in queue_token
- Index: (doctor_id, queue_date, token_number)

---

## ✅ Acceptance Criteria
- Booking creates token
- Cancelling affects token cleanly (skip or delete)
- Doctor can view/manage queue
- Patient can view their queue token + position
