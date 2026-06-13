# ADR-010: Processing Modes (Standalone / Assisted / Server-First)

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

Stores using a sync backend (Tier 1–3) have different expectations about how the app
should behave when the server is reachable versus when it is not. A small café running
a local LAN server may want the app to behave identically whether the server is up or
down. A larger store with a managed cloud backend may want the app to defer certain
operations to the server when possible for consistency. A single-device store with no
server needs the app to never attempt any network calls at all.

## Decision

Three **processing modes** are available, configurable in Settings by users holding
the `SETTINGS_BACKEND` permission:

**Standalone**
The app behaves as if no server exists regardless of backend configuration. All
operations are completed locally. WorkManager sync runs silently in the background but
is never in the critical path of any user action. This is the default for Tier 0 and
the recommended mode for stores that prioritize speed above all else.

**Assisted**
The app completes all operations locally first, then attempts to sync. If the server
is reachable at the time of a write, the sync happens immediately after the local write
completes. If not, the standard WorkManager retry applies. This is the recommended mode
for Tier 1 (LAN) stores where the server is usually available.

**Server-First**
The app attempts to confirm writes with the server before marking an operation complete.
If the server is unreachable, the operation falls back to local-only with a visible
indicator that the record is unconfirmed. This mode is only recommended for Tier 2–3
stores where the server is a managed cloud instance with high availability. It should
never be used in environments with unreliable connectivity.

All three modes use the same `SyncBackend` interface (ADR-004) and the same Room
database (ADR-001). The mode only controls the **timing and blocking behavior** of the
sync call — not the data model or conflict resolution logic.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Single fixed mode for all users | Cannot serve both offline-only and high-availability store needs |
| Server-First as default | Violates offline-first requirement for stores with poor connectivity |
| Mode selection hidden from user | Store owners need explicit control over this behavior |

## Consequences

**Positive:**
- Stores with high-availability servers get near-real-time consistency when desired
- Stores in poor-connectivity environments are fully protected from performance issues
- All three modes degrade gracefully to local-only — no mode can break the app
- Mode is configurable at any time without data migration

**Negative:**
- Server-First mode introduces a latency dependency on the critical POS path — must
  display a clear warning in the UI when this mode is selected
- Three modes must be tested independently for correct fallback behavior
- The mode label and description in Settings must be written clearly enough for a
  non-technical store owner to make an informed choice
