# Phase 7 — Finish Core Quality (Polish + Stability)

## ✅ Objective
Make the core API stable, consistent, and presentable.

---

## 1) Swagger / OpenAPI
- Add security scheme so Swagger can accept JWT
- Group endpoints by tags (you already do this)

---

## 2) Consistent Response DTOs
Decide whether you return:
- entity-like DTOs
OR
- wrapper response: { data, message, timestamp }

Keep it consistent.

---

## 3) Logging
- Log key actions:
  - register/login
  - booking/cancel
  - queue advance
- Avoid logging passwords/tokens

---

## 4) Seed Data (Optional)
Add an initializer for local dev:
- admin user
- sample doctor + schedule

---

## 5) Postman Collection
Create:
- Register patient
- Login patient
- Admin login
- Create doctor
- Create schedule
- Book appointment
- View queue
- Advance queue

---

## 6) Minimum Manual Test Cases
- Cannot book outside schedule
- Cannot book same slot twice
- Cancelling updates token policy
- Doctor cannot access other doctor queue
- Patient cannot cancel others’ appointment
- Unauthorized calls rejected

---

## ✅ Acceptance Criteria
- Core flows run end-to-end using Postman
- Errors are clean and predictable
- Swagger is accurate
- Repo looks professional and readable
