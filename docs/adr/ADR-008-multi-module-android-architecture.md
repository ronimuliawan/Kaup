# ADR-008: Multi-Module Android App Architecture

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

The app encompasses a large feature surface — POS, inventory, customers, suppliers,
expenses, sales, auth, reports, settings, and optional ERP modules. Building this as a
single monolithic module creates several problems: long build times as the codebase
grows, no enforced boundaries between features (a POS screen can accidentally depend on
HR code), difficulty for community contributors to work on one feature without
understanding the entire codebase, and no path to independent feature flag toggling
(see ADR-007).

## Decision

The project is structured as a **multi-module Android monorepo**. Each feature is an
isolated Gradle module with explicit, enforced dependencies.

```
root/
├── android-app/              → app shell, navigation host, DI wiring
├── shared-kmp/               → KMP domain logic (see ADR-003)
├── ktor-server/              → self-hosted server (Tier 1)
├── core/
│   ├── core-ui/              → shared Compose components, theme, typography
│   ├── core-data/            → Room setup, DAOs, repository interfaces
│   └── core-network/         → SyncBackend wiring, WorkManager, notifications
└── feature/
    ├── feature-pos/
    ├── feature-inventory/
    ├── feature-customers/
    ├── feature-suppliers/
    ├── feature-expenses/
    ├── feature-sales/
    ├── feature-reports/
    ├── feature-auth/
    ├── feature-settings/
    └── feature-restaurant/   → 🔒 restaurantEnabled flag
```

**Dependency rules (strictly enforced via module build.gradle):**
- `feature-*` modules may depend on `core-*` and `:shared-kmp`
- `feature-*` modules must **never** depend on another `feature-*` module directly —
  cross-feature communication happens via shared data models in `:core-data` or
  navigation events in `:android-app`
- `core-*` modules may depend on `:shared-kmp` only
- `:android-app` depends on all `feature-*` modules — it is the only module that does

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Single monolithic module | No build caching benefit, no enforced boundaries, scales poorly |
| Feature modules without core layer | Shared UI and data code duplicated across features |
| Separate repositories per feature | Too much overhead for a project at this stage |

## Consequences

**Positive:**
- Gradle build caching — only changed modules recompile on each build
- Hard boundary enforcement — a contributor cannot accidentally couple POS to HR
- Community contributors can work on a single `feature-*` module with a narrow
  blast radius — changes cannot silently break unrelated features
- Each module carries its own unit and integration tests (see ROADMAP testing section)
- Aligns naturally with the feature flag system in ADR-007

**Negative:**
- Initial Gradle configuration is more complex than a single-module project
- Refactoring a concept that spans multiple features (e.g., a shared entity model)
  requires coordinated changes across modules
- New contributors must understand the module boundary rules before making their
  first PR — must be clearly documented in `CONTRIBUTING.md`
