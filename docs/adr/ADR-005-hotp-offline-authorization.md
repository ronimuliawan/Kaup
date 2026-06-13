# ADR-005: HOTP-Based Offline Manager Authorization

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

The app supports single-device stores where a manager and cashier share one device, and
multi-device stores where the manager may not be physically at the POS terminal. Certain
actions (void transaction, issue refund, add products, override price) must require
manager authorization. A simple PIN re-entry on the same device is insufficient — a
cashier could observe the manager's PIN and reuse it. The solution must work 100%
offline, consistent with ADR-001.

## Decision

Manager authorization for restricted actions uses **HOTP (HMAC-based One-Time Password,
RFC 4226)** — a counter-based, cryptographically signed code that is valid for a single
use. HOTP is chosen over TOTP (time-based) because it requires no clock synchronization
between devices.

**Setup (once per manager account):**
A unique HMAC secret key is generated per manager, stored encrypted in Room using
Android Keystore, and provisioned to other store devices via:
- **QR code scan** — manager's device displays a QR code; staff device scans once
  during onboarding (works Tier 0, no server required)
- **Server-provisioned** — automatic key distribution when a sync backend is present
  (Tier 1–3 only)

**Daily operation:**
1. A restricted action triggers the `ManagerApprovalOverlay`
2. Manager generates a 6-digit HOTP code on their device
3. Code is communicated verbally or shown on screen to staff
4. Staff enters the code; staff device validates it locally using the shared secret
5. Code is immediately consumed and marked used in Room — cannot be reused
6. Counter increments on both devices; the next code will be different

**Code scope (manager chooses at generation time):**
- *Specific action only* — **Recommended.** Code is valid for one named permission
  and one transaction ID. Single use, no expiry.
- *General elevation token* — code grants one action of the staff's choice within a
  configurable time window (default 5 minutes). UI displays an explicit pros/cons
  warning before generation. Admin can disable this option entirely in Settings.

A **look-ahead window of 10** handles counter desynchronization caused by the manager
generating unused codes. This follows the RFC 4226 recommended mitigation.

**Six authorization methods are supported**, selectable and orderable by the admin
in Settings:

| Option | Method | v1 Status |
|---|---|---|
| A | HOTP Verbal Code | Must Have |
| B | Server-Provisioned Secret | Should Have |
| C | NFC Tap-to-Approve | Could Have |
| D | BLE Proximity Approval | Could Have — security caveat required in UI |
| E | Biometric-Gated Code Generation | Should Have |
| F | Printed Backup Codes | Must Have |

All authorization events are written to an audit log in Room and synced to the server
when connectivity is available.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Manager PIN re-entry on staff device | PIN can be observed and reused — insufficient |
| TOTP (time-based codes) | Requires clock synchronization; fragile on offline devices |
| Remote push notification approval only | Requires connectivity — violates ADR-001 |
| Biometric only | Hardware dependent; no fallback for gloves or sensor failure |

## Consequences

**Positive:**
- Works 100% offline across all tiers
- Cryptographically secure — codes cannot be guessed or reused
- Full audit trail of all authorization events
- Six configurable methods cover every store context and budget
- Printed backup codes ensure business continuity even if all devices are unavailable

**Negative:**
- HOTP counter desynchronization is possible if the manager generates unused codes
  repeatedly; mitigated by a look-ahead window of 10
- QR provisioning must be completed before the first authorization is needed — an
  uncompleted provisioning state must be handled gracefully in the UI
- BLE proximity approval (Option D) has known proximity-spoofing vulnerabilities and
  must not be recommended as a primary method
