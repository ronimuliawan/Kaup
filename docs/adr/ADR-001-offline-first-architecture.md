# ADR-001: Offline-First Architecture

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

This app targets small and medium businesses operating in environments where internet
connectivity cannot be guaranteed — outdoor markets, areas with poor signal, or stores
that choose to run fully isolated local networks. A POS system that fails mid-transaction
due to lost connectivity is a critical business failure. Uninterrupted operation is a
non-negotiable requirement.

## Decision

The app is designed **offline-first**. Room (SQLite) is always the single source of
truth. The app never reads from a remote server directly — all reads come from the local
Room database. Network connectivity is treated as a bonus, not a requirement.

All writes (transactions, stock movements, customer records) are saved to Room
immediately and tagged with `sync_status = PENDING`. A background WorkManager job
monitors for connectivity and pushes `PENDING` records to the configured backend when
available. On success, records are marked `SYNCED`. On failure, WorkManager retries
with exponential backoff automatically.

A manual "Sync Now" button is available for users who want to force a push before
closing a shift.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Online-first with offline cache | App degrades when offline — unacceptable for a POS |
| Sync on app open only | Too infrequent; data loss risk if app is not reopened regularly |
| Real-time sync only (e.g., Firebase live listeners) | Requires constant connectivity — violates core requirement |

## Consequences

**Positive:**
- 100% operational regardless of internet availability
- App loads instantly — no waiting on server responses
- Works across Tier 0 (no server) through Tier 3 (managed cloud) without code changes
- Naturally supports the Standalone, Assisted, and Server-First processing modes

**Negative:**
- Conflict resolution is required when multiple devices write the same data offline
- Local database grows over time — an archival and housekeeping strategy is required
- Initial architecture is more complex than a simple REST-first approach
