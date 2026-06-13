# ADR-003: Kotlin Multiplatform Shared Domain Module

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

This project has three runtime targets: the Android app, the Ktor server, and the
planned Compose for Web dashboard. All three need to execute the same business logic —
tax calculation, inventory aggregation, sales computation, HOTP code generation, and
conflict resolution. Without a shared module, this logic must be written and maintained
in at least two places (Android + server), creating drift risk and duplication bugs.

## Decision

All business logic lives in a **Kotlin Multiplatform (KMP) shared module** (`:shared-kmp`).
This module compiles to Android (Kotlin/JVM), Ktor server (Kotlin/JVM), and future web
target (Kotlin/Wasm). It contains no platform-specific code — only pure Kotlin.

Module structure:

```
:shared-kmp/
├── commonMain/
│   ├── domain/
│   │   ├── TaxResolver.kt
│   │   ├── InventoryEngine.kt
│   │   ├── SalesCalculator.kt
│   │   ├── ConflictResolver.kt
│   │   ├── HOTPGenerator.kt
│   │   └── AnalyticsAggregator.kt
│   ├── models/           → shared data models, DTOs, enums
│   └── sync-contracts/   → SyncBackend interface + adapter contracts
└── commonTest/           → unit tests that run on all targets
```

Platform-specific implementations (Room for Android, Exposed + PostgreSQL for Ktor)
implement interfaces defined in `:shared-kmp`. The domain module has zero
platform-specific dependencies.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Duplicate logic in Android and server separately | Drift risk, double maintenance, bugs diverge silently over time |
| Server handles all logic; Android is thin-client only | Breaks Tier 0 (no server) and Standalone processing mode |
| REST API contracts only, no shared Kotlin code | No compile-time type safety, no shared validation, high boilerplate |

## Consequences

**Positive:**
- Business logic written once, tested once, runs identically on all three targets
- One unit test suite in `commonTest` validates Android, server, and web simultaneously
- Officially supported by Google and JetBrains as a stable pattern as of 2024–2025
- Opens the path to Compose for Web and a potential iOS target without rewriting logic
- Contributors to any target benefit immediately from domain improvements

**Negative:**
- KMP Gradle configuration has non-trivial setup complexity
- Not all Kotlin libraries have KMP-compatible versions — alternatives must be chosen
- Contributors working only on Android must understand and respect the KMP boundary
