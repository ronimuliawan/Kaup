# ADR-004: Pluggable SyncBackend Interface

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

Different users have fundamentally different infrastructure budgets, technical expertise,
and privacy requirements. A single-device store owner needs zero infrastructure. A
multi-device store wants a local server. A growing business may want managed cloud.
Forcing all users onto one backend would exclude large segments of the target audience
and contradict the open-source philosophy of user control.

## Decision

The sync layer is built around a **`SyncBackend` interface** defined in
`:shared-kmp/sync-contracts`. The Android app never talks to a backend directly — it
only calls the interface. Concrete implementations are swapped based on the user's
selection in Settings.

```kotlin
interface SyncBackend {
    suspend fun pushRecords(records: List<PendingRecord>): SyncResult
    suspend fun pullUpdates(since: Instant): List<RemoteUpdate>
    suspend fun uploadFile(localPath: String): String // returns remote URL
    fun isConfigured(): Boolean
}
```

Four tiers are supported via concrete implementations:

| Tier | Implementation | Description |
|---|---|---|
| **0** | `NoSyncBackend` | Fully local, data stays on device, zero setup |
| **1** | `KtorBackend` | Self-hosted Ktor server on LAN via Docker Compose |
| **2** | `SupabaseBackend` / `AppwriteBackend` | Self-hosted BaaS on user's own VPS |
| **3** | `SupabaseBackend` / `AppwriteBackend` | Managed cloud, free tier available |

Only the Ktor server is built and maintained as a first-party project artifact.
Supabase and Appwrite are supported through their official Kotlin SDKs as Android-side
adapter implementations only. Setup documentation is provided in
`/docs/setup-supabase.md` and `/docs/setup-appwrite.md`.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Single hardcoded backend (e.g., Supabase only) | Vendor lock-in; contradicts open-source user-control philosophy |
| Firebase | Cloud-only, no self-hosting, proprietary, F-Droid incompatible |
| CouchDB / PouchDB sync protocol | No first-class Kotlin Android SDK; adds unfamiliar tech to the stack |

## Consequences

**Positive:**
- Users start free (Tier 0) and graduate to a server when ready — zero migration
  friction since the sync engine picks up all `PENDING` records when a backend
  is configured
- Community can contribute new backend adapters (e.g., ERPNext adapter, custom
  Postgres) without touching core app code
- F-Droid compliant — no proprietary backend is ever a hard dependency

**Negative:**
- Four backend paths must be tested and documented independently
- Breaking changes to the `SyncBackend` interface affect all adapter implementations
- Users moving from Tier 0 to Tier 1–3 will trigger a bulk sync on first connection —
  a progress indicator is required in the UI to communicate this clearly
