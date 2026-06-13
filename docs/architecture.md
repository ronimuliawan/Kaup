# Architecture Overview

- **Version**: 1.0
- **Date**: 2026-03-14

---

## Table of Contents

- [Module Structure](#module-structure)
- [Module Dependency Rules](#module-dependency-rules)
- [Data Flow — Completing a Sale](#data-flow--completing-a-sale)
- [Sync Lifecycle](#sync-lifecycle)
- [Auth Flow](#auth-flow)
- [Manager Approval Flow](#manager-approval-flow)
- [Backend Tiers](#backend-tiers)
- [Processing Modes](#processing-modes)

---

## Module Structure

```
root/
├── android-app/                  → App shell, navigation host, DI wiring
│
├── shared-kmp/                   → Kotlin Multiplatform shared domain logic
│   └── commonMain/
│       ├── domain/
│       │   ├── TaxResolver.kt
│       │   ├── InventoryEngine.kt
│       │   ├── SalesCalculator.kt
│       │   ├── ConflictResolver.kt
│       │   ├── HOTPGenerator.kt
│       │   └── AnalyticsAggregator.kt
│       ├── models/               → Shared data models, DTOs, enums
│       └── sync-contracts/       → SyncBackend + NotificationBackend interfaces
│
├── ktor-server/                  → Self-hosted server artifact (Tier 1)
│
├── core/
│   ├── core-ui/                  → Shared Compose components, theme, typography
│   ├── core-data/                → Room setup, DAOs, repository interfaces,
│   │                               PrinterService abstraction
│   └── core-network/             → SyncBackend wiring, WorkManager jobs,
│                                   NotificationBackend wiring
│
└── feature/
    ├── feature-pos/              → Sale register, shift management, receipts
    ├── feature-inventory/        → Items, categories, variants, stock movements
    ├── feature-customers/        → Customer database, loyalty, gift cards
    ├── feature-suppliers/        → Supplier database, purchase orders
    ├── feature-expenses/         → Expense logging and categories
    ├── feature-sales/            → Quotations, sales orders, delivery notes
    ├── feature-reports/          → Sales, inventory, expense, financial reports
    ├── feature-auth/             → Lock Screen, session management, RBAC,
    │                               HOTP authorization, onboarding wizard
    ├── feature-settings/         → Backend config, backup, export, feature flags
    └── feature-restaurant/       → Table management, order routing
                                    🔒 restaurantEnabled flag
```

---

## Module Dependency Rules

These rules are strictly enforced. Violations must be caught in code review
before merging.

```
android-app        → depends on ALL feature-* modules (only module that does)
feature-*          → depends on core-* and :shared-kmp ONLY
feature-*          → MUST NOT depend on another feature-* module
core-data          → depends on :shared-kmp ONLY
core-network       → depends on :shared-kmp and core-data
core-ui            → depends on NOTHING (pure Compose components)
shared-kmp         → depends on NOTHING (pure Kotlin, no platform imports)
ktor-server        → depends on :shared-kmp ONLY
```

Cross-feature communication happens exclusively via:
- Shared data models in `:core-data` (e.g., a sale record read by both
  `:feature-pos` and `:feature-reports`)
- Navigation events handled in `:android-app` (e.g., POS navigates to
  customer lookup, which is owned by `:feature-customers`)

---

## Data Flow — Completing a Sale

This is the most critical path in the app. Every step must work with zero
network activity.

```
1. CASHIER ACTION
   Cashier taps "Complete Sale" on the POS screen
   ↓
2. DOMAIN VALIDATION  [:shared-kmp/domain]
   SalesCalculator validates line items, applies tax, computes totals
   ↓
3. LOCAL WRITE  [:core-data → Room]
   Transaction record written with sync_status = PENDING
   StockMovement records written for each line item (direction = OUT)
   denormalized currentStock updated on each affected item
   ↓
4. RECEIPT GENERATION  [:feature-pos]
   Receipt composed from local Room data — no server call
   PrinterService sends to paired Bluetooth printer (if configured)
   Email receipt queued (if configured)
   ↓
5. UI CONFIRMATION  [:feature-pos]
   "Sale complete" shown to cashier — cart cleared, ready for next sale
   ↓
6. BACKGROUND SYNC  [:core-network → WorkManager]  (async, not blocking)
   WorkManager job detects PENDING records
   SyncBackend.pushRecords() called with batched transaction + movements
   On success  → sync_status = SYNCED
   On failure  → WorkManager retries with exponential backoff
                 Local notification fires if retry budget exhausted
```

**Key invariant**: Steps 1–5 complete identically whether or not a network
is present. Step 6 is always async and never blocks the cashier.

---

## Sync Lifecycle

Every record that can be synced carries a `syncStatus` field:

```
PENDING  →  SYNCING  →  SYNCED
                    ↘  FAILED  →  (retry)  →  SYNCED
                                           ↘  CONFLICT
```

**PENDING**: Written locally, not yet pushed to server.
**SYNCING**: WorkManager job is actively pushing this record.
**SYNCED**: Server confirmed receipt. Record is safe to archive.
**FAILED**: Push failed. WorkManager will retry with exponential backoff.
**CONFLICT**: Server detected a conflict (e.g., same item sold on two devices
while offline). ConflictResolver in `:shared-kmp/domain` applies the resolution
strategy — for inventory, events are replayed in timestamp order; negative stock
is flagged but not rejected.

**WorkManager trigger conditions** (all evaluated, first match fires the job):
- Connectivity restored after being offline
- App brought to foreground with PENDING records present
- 15-minute periodic check (minimum WorkManager interval)
- User taps "Sync Now" in Settings

---

## Auth Flow

```
APP LAUNCH
   ↓
SessionManager checks for active session
   ↓
No active session ──────────────────────→  LOCK SCREEN
                                           Staff profile card grid
                                           ↓
                                           Staff taps their card
                                           ↓
                                           PIN entry
                                           ↓
                                           PIN valid?
                                           ├── No  → shake animation, retry
                                           └── Yes → load Set<Permission>
                                                      from Room into
                                                      SessionManager
                                                      ↓
Active session ─────────────────────────→  HOME / POS SCREEN
   ↓
Auto-lock timer (configurable idle timeout)
   ↓
Timer expires OR user taps Lock button
   ↓
SessionManager.clearSession()
   ↓
LOCK SCREEN  (loop)
```

**Session state** is held in memory only — never persisted to disk.
Clearing the session means the `Set<Permission>` is discarded from memory.
Room data is untouched. The next staff member to unlock gets a clean session
with their own permissions loaded.

---

## Manager Approval Flow

Triggered when a staff member attempts an action they do not have permission for.

```
STAFF attempts restricted action (e.g., void transaction)
   ↓
Permission check: session.hasPermission(POS_VOID_TRANSACTION)
   ↓
Permission denied
   ↓
ManagerApprovalOverlay displayed as bottom sheet
Staff session remains active — cart and POS state preserved
   ↓
Overlay shows two paths:

PATH A — HOTP Code Entry
   Manager generates 6-digit HOTP code on their own device
   Manager reads code verbally to staff
   Staff enters code into overlay
   ↓
   HOTPGenerator.validate(code, managerSecret, counter)
   ├── Invalid  → error shown, staff can retry or cancel
   └── Valid    → code marked consumed in Room (cannot be reused)
                  counter incremented
                  OverrideLog record written (action, manager, timestamp)
                  ↓
                  RESTRICTED ACTION PROCEEDS
                  ↓
                  OverrideLog syncs to server when connectivity available

PATH B — NFC Tap  (Could Have — requires NFC hardware)
   Manager taps device against staff device
   Signed authorization token transmitted via NFC HCE
   Same validation and audit trail as Path A
```

**Code scope** (manager selects before generating):
- *Specific action only* — valid for named permission + transaction ID, one use
- *General elevation token* — valid for any one action within time window
  (default 5 min); explicit warning shown before generation

---

## Backend Tiers

```
TIER 0 — No Backend
   NoSyncBackend (no-op implementation of SyncBackend interface)
   All data lives in Room on the device
   WorkManager sync job runs but immediately returns — nothing to push
   Single device only

TIER 1 — Self-Hosted LAN Server
   KtorBackend → Ktor server running on local network via Docker Compose
   JWT authentication between app and server
   HTTPS enforced even on LAN
   Multi-device: all store devices point to same LAN IP
   Manager can connect from their personal device on the same network

TIER 2 — Self-Hosted Cloud
   SupabaseBackend or AppwriteBackend
   User runs their own Supabase/Appwrite instance on a VPS
   Full data ownership — no third party ever touches the data
   Multi-device across locations (different networks)

TIER 3 — Managed Cloud
   SupabaseBackend or AppwriteBackend
   User uses Supabase Cloud or Appwrite Cloud free/paid tier
   Easiest setup — no server administration required
   Data hosted by Supabase/Appwrite — user accepts their terms
```

Switching tiers requires only a Settings change and a one-time bulk sync.
No data migration, no schema changes, no reinstall.

---

## Processing Modes

Processing modes control the **timing and blocking behavior** of the sync call.
They do not affect the data model, conflict resolution, or offline capability.

```
STANDALONE
   Write to Room → show confirmation → WorkManager syncs async
   Server is never in the critical path
   Recommended for: Tier 0, unreliable connectivity, speed-critical stores

ASSISTED
   Write to Room → show confirmation → attempt immediate sync if connected
   If connected: sync completes before next action (fast path)
   If not connected: WorkManager retry applies (same as Standalone)
   Recommended for: Tier 1 LAN stores where server is usually reachable

SERVER-FIRST
   Write to Room → attempt server confirmation → show confirmation
   If connected: waits for server acknowledgment before confirming to user
   If not connected: falls back to local write with "unconfirmed" indicator
   ⚠️ Introduces latency on the POS critical path
   Recommended for: Tier 2–3 high-availability deployments only
   Must display explicit warning when selected in Settings
```
