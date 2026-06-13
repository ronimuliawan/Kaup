# Kaup — Product Roadmap & MoSCoW Prioritization

- **Version**: 1.0
- **Date**: 2026-03-18
- **Status**: Active
- **Methodology**: Kanban — see [ADR-012](docs/adr/ADR-012-kanban-development-methodology.md)

---

## Table of Contents

- [Milestone Overview](#milestone-overview)
- [Must Have — v0.1-alpha](#must-have--v01-alpha)
- [Should Have — v0.2-alpha](#should-have--v02-alpha)
- [Could Have — v1.x](#could-have--v1x)
- [Won't Have — Post Year 1](#wont-have--post-year-1)
- [Testing Strategy](#testing-strategy)
- [Open Questions](#open-questions)

---

## Milestone Overview

| Milestone | Scope | Distribution |
|---|---|---|
| `v0.1-alpha` | Must Have core — POS, Inventory, Auth, Sync Engine | GitHub Releases only |
| `v0.2-alpha` | Should Have complete — first public release | GitHub Releases + IzzyOnDroid |
| `v1.0` | All Must Have + Should Have stable and documented | GitHub + IzzyOnDroid + F-Droid + Play Store |
| `v1.x` | Could Have — community contributions, incremental releases | All channels |
| `v2.0` | Post Year 1 — Web Dashboard, deep ERP layer | All channels |

Releases ship when the milestone is genuinely complete — not on a calendar date.
See [ADR-012](docs/adr/ADR-012-kanban-development-methodology.md).

---

## Must Have — v0.1-alpha

These items are non-negotiable for the app to be minimally functional.
Nothing in v0.2-alpha is started until every Must Have is stable.

### Foundation (all modules depend on this — built first)

- [ ] `:shared-kmp` module scaffold — domain models, DTOs, enums
- [ ] `SalesCalculator` — line items, discounts, tax (inclusive/exclusive), totals
- [ ] `TaxResolver` — per-item tax rate application
- [ ] `InventoryEngine` — current stock computed from movement event log
- [ ] `ConflictResolver` — event replay for multi-device conflict resolution
- [ ] `HOTPGenerator` — HOTP code generation and validation (RFC 4226)
- [ ] `SyncBackend` interface + `NoSyncBackend` implementation
- [ ] `NotificationBackend` interface + `LocalNotificationBackend` implementation
- [ ] `UpdateChecker` interface + `NoOpUpdateChecker` + `GitHubUpdateChecker`
- [ ] `:core-data` — Room database setup, all entity definitions, all DAOs
- [ ] `:core-ui` — Material 3 Expressive theme, typography, shape system,
      base Compose components, `ManagerApprovalOverlay` shell
- [ ] `:core-network` — WorkManager scaffold, sync queue management,
      `NoSyncBackend` wired via Hilt
- [ ] `:android-app` shell — navigation host, Hilt setup, three build flavors
      (`github`, `fdroid`, `playstore`), `NavigationSuiteScaffold`

### feature-auth

- [ ] Lock Screen UI — staff profile card grid, adaptive layout
- [ ] PIN entry and validation
- [ ] Session management — `SessionManager` singleton, `Set<Permission>` loading
- [ ] Auto-lock idle timeout
- [ ] Role defaults — Owner, Manager, Cashier, Waiter
- [ ] RBAC permission check helpers — `session.hasPermission()`
- [ ] Basic onboarding wizard — store name, currency, first Owner account
      (Steps 1–3 of 7; remaining steps unlocked in Should Have)
- [ ] HOTP key generation and QR provisioning UI (manager side)
- [ ] Override code generation UI (manager side)
- [ ] Override code entry and validation UI (staff side)
- [ ] `ManagerApprovalOverlay` — full implementation, HOTP Path A
- [ ] Audit log write on every authorization event
- [ ] Printed backup codes for HOTP fallback

### feature-pos

- [ ] Sale register UI — cart, line items, quantity, per-item discount
      (adaptive: compact and expanded layouts)
- [ ] Item search and barcode scan to cart (CameraX)
- [ ] Payment method selection — cash, split payment
- [ ] Change calculation display
- [ ] Transaction write to Room — `sync_status = PENDING`
- [ ] Stock movement write — `OUT` per line item on sale completion
- [ ] Receipt composition — itemised, tax breakdown, store name, date
- [ ] Bluetooth ESC/POS thermal printer integration (`PrinterService`)
- [ ] Shift open and close UI — cash-up, variance summary
- [ ] Shift record write to Room
- [ ] Void transaction — requires `POS_VOID_TRANSACTION` + `ManagerApprovalOverlay`
- [ ] Refund flow — requires `POS_ISSUE_REFUND` + `ManagerApprovalOverlay`
- [ ] Fulfillment status display for negative-stock pre-orders

### feature-inventory

- [ ] Item list UI — search, filter by category, adaptive layout
- [ ] Item detail and edit UI
- [ ] Category management UI
- [ ] Item variant and attribute management
- [ ] Barcode scan for item lookup (CameraX)
- [ ] Stock receiving UI — log incoming stock, write `IN` movement to Room
- [ ] Manual stock adjustment UI — with reason field
- [ ] Reorder level configuration per item
- [ ] Low stock local notification — WorkManager periodic check
- [ ] Barcode label printing trigger (`PrinterService`)

### feature-settings (Must Have subset)

- [ ] Backend tier selection UI — Tier 0 through Tier 3
- [ ] Processing mode selection UI — Standalone / Assisted / Server-First
  (with explicit warning for Server-First)
- [ ] Full database backup — AES-encrypted, SAF destination picker
- [ ] Restore from backup — file picker, validation summary
- [ ] Backup schedule configuration
- [ ] Manual "Sync Now" trigger
- [ ] In-app update notification for `github` flavor
  (Play Store and F-Droid builds excluded — updates handled by their
  respective stores)
- [ ] Language selection

### Sync Engine (Tier 0 complete, Tier 1 scaffold)

- [ ] WorkManager sync job — detects PENDING records, calls `SyncBackend`
- [ ] Exponential backoff retry on sync failure
- [ ] Sync failure local notification when retry budget exhausted
- [ ] `sync_status` lifecycle — PENDING → SYNCING → SYNCED / FAILED / CONFLICT
- [ ] Ktor server project scaffold — routes, JWT auth, HTTPS, Docker Compose
- [ ] `KtorBackend` Android adapter — `pushRecords()`, `pullUpdates()`,
      `uploadFile()`
- [ ] Basic conflict resolution on server via `ConflictResolver`

---

## Should Have — v0.2-alpha

These items make Kaup production-ready for real store use.
Work begins after all Must Have items are stable.

### feature-auth (Should Have additions)

- [ ] Full user management UI — create, edit, delete staff accounts
- [ ] Role assignment UI
- [ ] Per-user permission override UI — grant up or restrict down
- [ ] Biometric enrollment — Android BiometricPrompt, Keystore-backed
- [ ] Biometric-gated HOTP code generation (Option E)
- [ ] Server-provisioned HOTP key distribution (Option B, requires Tier 1+)
- [ ] Onboarding wizard — Steps 4–7 (backend selection, feature flags,
      printer pairing, HOTP provisioning for first staff account)

### feature-pos (Should Have additions)

- [ ] Card payment method — field capture only (no payment gateway in v1)
- [ ] Receipt email trigger
- [ ] Per-item note field on cart line items
- [ ] Price override — requires `POS_OVERRIDE_PRICE` + `ManagerApprovalOverlay`
- [ ] Discount above threshold — requires `POS_DISCOUNT_ABOVE_X` +
      `ManagerApprovalOverlay`
- [ ] Refund evidence — optional photo/video capture, compressed,
      stored in app-private filesystem

### feature-inventory (Should Have additions)

- [ ] Item kit / bundle configuration
- [ ] Item thumbnail image — capture or gallery pick, stored locally
- [ ] Stock valuation display — FIFO cost per item

### feature-customers

- [ ] Customer list and detail UI — adaptive layout
- [ ] Customer search UI
- [ ] Customer assignment to a sale in `:feature-pos`
- [ ] Customer purchase history view
- [ ] Loyalty program — points accrual and redemption
- [ ] Gift card issuance and redemption

### feature-suppliers

- [ ] Supplier list and detail UI
- [ ] Purchase order creation and status tracking
- [ ] Link stock receiving in `:feature-inventory` to a purchase order

### feature-expenses

- [ ] Expense entry UI — amount, category, date, notes, optional receipt photo
- [ ] Expense category management UI
- [ ] Expense list and filter UI

### feature-sales

- [ ] Quotation creation and management
- [ ] Sales order creation and status tracking
- [ ] Delivery note creation and fulfillment tracking
- [ ] Sales order → POS handoff when customer pays

### feature-reports

- [ ] Sales report — by date range, by cashier, by item (adaptive layout)
- [ ] Inventory / stock report — current levels, movement history
- [ ] Payments summary — breakdown by payment method per shift
- [ ] Expense report
- [ ] Gross margin / cost price report
- [ ] CSV export for all report types
- [ ] PDF export for all report types

### feature-settings (Should Have additions)

- [ ] Housekeeping menu — synced file list with size summary, safe delete
- [ ] Local file retention policy configuration
- [ ] Video compression and duration settings
- [ ] Analytics and crash reporting opt-in — explicit pros/cons warning
- [ ] `SupabaseBackend` Android adapter (Tier 2–3)
- [ ] `AppwriteBackend` Android adapter (Tier 2–3)
- [ ] ntfy remote notification setup UI (`NtfyBackend`)

### Notifications (Should Have)

- [ ] Shift open reminder — AlarmManager, user-configured time
- [ ] Backup reminder — fires after user-configured days without backup
- [ ] Manager override request notification — via ntfy for Tier 1–3

### IzzyOnDroid Submission

- [ ] `fastlane/` metadata — store listing, screenshots, changelogs
- [ ] `github` flavor APK signed and attached to GitHub Release tag
- [ ] IzzyOnDroid metadata file committed to repo
- [ ] Submission PR opened to IzzyOnDroid repo

---

## Could Have — v1.x

These items are genuine improvements but do not block any store from
operating fully without them. Ordered loosely by community value.

### feature-pos
- [ ] Loyalty points redemption at checkout
- [ ] Multi-currency display (display only — store currency is fixed)
- [ ] Customer-facing display output (second screen or cast)

### feature-inventory
- [ ] Stock transfer between locations
- [ ] Stock valuation methods — FIFO, weighted average, selectable
- [ ] Supplier quotation comparison

### feature-customers
- [ ] Mailchimp export integration

### feature-sales
- [ ] Commission tracking per staff member

### feature-reports
- [ ] Advanced analytics dashboard with interactive charts
- [ ] Scheduled report export via email or ntfy

### feature-settings
- [ ] Receipt template and footer editor
- [ ] FCM push notification support (`FcmBackend`, `playstore` flavor only)
- [ ] `AppwriteBackend` self-hosted setup wizard

### feature-auth
- [ ] NFC tap-to-approve manager authorization (Option C)
- [ ] BLE proximity approval (Option D — with explicit security caveat in UI)

### feature-restaurant
- [ ] Table grid UI — visual layout, status indicators
- [ ] Table creation, renaming, and merge
- [ ] Order assignment to table
- [ ] Split bill UI
- [ ] Waiter role with restricted navigation

### Ktor Server
- [ ] Server-side RBAC enforcement (post-v1 security hardening)
- [ ] Multi-location stock transfer API
- [ ] Admin web UI for server management

---

## Won't Have — Post Year 1

These are explicitly out of scope until v2.0 or later.
Do not design for, prototype, or partially implement these in v1.

- Full double-entry accounting (General Ledger, AP/AR, bank reconciliation)
- Financial statements (P&L, Balance Sheet, Cash Flow)
- HR and payroll management
- Manufacturing (BOM, MRP, work orders)
- Quality control, asset management, project management
- CRM pipeline and e-commerce integration
- Web dashboard (Compose for Web)
- iOS app
- Kitchen Display System (KDS)
- Country-specific tax or payment defaults
- Hardware peripherals beyond Bluetooth ESC/POS printers
  (cash drawers, weight scales, customer-facing displays)

---

## Testing Strategy

### Unit Tests (`:shared-kmp/commonTest`)
Run on all targets — Android, JVM (server), and future Wasm.
Every class in `:shared-kmp/domain` must have unit test coverage before
the corresponding feature module is considered done.

Priority test cases per domain class:

| Class | Priority Cases |
|---|---|
| `SalesCalculator` | Inclusive tax, exclusive tax, mixed rates, zero-rate items, discount before and after tax, rounding at £0.005 |
| `TaxResolver` | Multiple rates in one cart, tax-exempt items, zero-rate correctly distinguished from no-tax |
| `InventoryEngine` | Normal IN/OUT, negative stock result, simultaneous writes from two devices, look-ahead window edge case |
| `ConflictResolver` | Two devices sell the last unit offline, receiving + sale overlap, timestamp tie |
| `HOTPGenerator` | Valid code, consumed code rejected, counter drift within window, drift outside window, printed backup code path |

### Integration Tests (`:feature-*`)
Each feature module runs integration tests against an in-memory Room database.
No emulator required. Focus on repository layer — DAO writes, sync status
transitions, permission gate enforcement.

### UI Tests (`:android-app`)
End-to-end flows tested on emulator using Espresso + Compose Test:
- Complete a sale → receipt generated → stock decremented
- Cashier attempts void → ManagerApprovalOverlay → HOTP validates → void completes
- Sync failure → retry → SYNCED status confirmed
- Backup → wipe app data → restore → data intact

### Manual Test Checklist (before every release tag)
- [ ] Cold start to Lock Screen in ≤ 2 seconds on a 2 GB RAM device
- [ ] Complete sale with airplane mode enabled — full offline confirmation
- [ ] Barcode scan to cart in ≤ 500 ms
- [ ] Receipt prints on Bluetooth ESC/POS thermal printer
- [ ] Backup and restore roundtrip — data verified intact
- [ ] All three build flavors compile and launch cleanly

---

## Open Questions

These are unresolved decisions that will need a new ADR before implementation:

| Question | Blocks |
|---|---|
| Card payment — capture-only in v1, or integrate a payment gateway? If gateway, which one and how to keep it non-mandatory? | feature-pos Should Have |
| Multi-location support — single store only in v1, or design Room schema to support multiple locations from the start? | core-data foundation |
| Receipt email — use Android intent (no dependency) or an SMTP library? SMTP requires credentials management | feature-pos Should Have |
| Database schema versioning — Room migration strategy for alpha → beta → v1 schema changes | core-data foundation |