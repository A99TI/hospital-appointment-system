# Phase 6 — Notifications & Audit (Core-Ready, Optional to Implement Immediately)

## ✅ Objective
Create structures to support messaging + security tracking.
Even if you don’t send real emails/SMS yet, you can log notification requests.

---

## 1) Notification

### Relationship
- Many notifications belong to one user:
    - notification.user_id → user.id (FK)

Recommended:
- @ManyToOne(fetch = LAZY)
- user_id not null

Suggested usage:
- On appointment booked → create notification record “Appointment confirmed”
- On cancel → “Appointment cancelled”
- On queue activation → “It’s your turn”

You can later replace "save record" with real sending.

---

## 2) AuditLog

### Relationship
- Many logs belong to one user (nullable):
    - audit_log.user_id → user.id (FK)

Make user nullable because:
- You might want to log failed logins or anonymous access attempts.

Track:
- action (LOGIN_SUCCESS, LOGIN_FAIL, BOOK_APPOINTMENT, CANCEL_APPOINTMENT)
- ipAddress
- timestamp

---

## 3) APIs
You usually don’t expose these to normal users.

Admin-only:
- GET /api/admin/audit?from=...&to=...
- GET /api/admin/notifications?userId=...

---

## ✅ Acceptance Criteria
- Tables exist
- Service methods create logs/notifications on key events
- Admin can query logs (optional)
