# ADR-007: Runtime Feature Flag System for Optional Modules

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

The app serves both general retail stores (no restaurant needs) and F&B operators (need
table management). Post-v1, additional modules will be added (accounting, HR,
manufacturing). Loading every module for every user wastes memory, increases cognitive
load for non-relevant users, and increases staff training time. A non-F&B cashier should
never see a table management screen. A retail store owner should never be prompted to
configure a Bill of Materials.

## Decision

Optional modules are included in the APK but **never loaded or executed** until
explicitly enabled by an admin. This is implemented as a **runtime feature flag system**
backed by DataStore.

```kotlin
object FeatureFlags {
    var restaurantEnabled    : Boolean  // default: false
    var accountingEnabled    : Boolean  // default: false
    var hrEnabled            : Boolean  // default: false
    var manufacturingEnabled : Boolean  // default: false
}
```

Flags are read from DataStore on app startup and cached in memory for the session.
The navigation graph conditionally registers module routes based on flag state:

```kotlin
if (FeatureFlags.restaurantEnabled) {
    include(restaurantNavGraph)
}
```

UI elements, Room DAO initialization, and WorkManager tasks belonging to a module are
only activated when the corresponding flag is `true`. Disabled modules consume zero
runtime resources.

Flags are configured during onboarding (Step 5) and adjustable at any time in Settings
by users holding the `SETTINGS_FEATURE_FLAGS` permission. Post-v1 modules are visible
in onboarding with a "Coming soon" badge to communicate the roadmap without cluttering
the current experience.

**Android Dynamic Feature Modules (Play Feature Delivery)** were evaluated as an
alternative. They were rejected as the primary approach because they require Google Play
distribution and do not function for F-Droid or sideloaded builds. DFM may be explored
as a future Play Store download-size optimization layered on top of this system without
changing the feature flag logic.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Android Dynamic Feature Modules only | Requires Google Play — incompatible with F-Droid and sideloading |
| Separate APK builds per feature set | Fragmented distribution, unsustainable maintenance overhead |
| All modules always loaded | Memory waste, UI clutter, increased training time for irrelevant features |
| Build-time flags (BuildConfig) | Requires separate APK per configuration — same problem as above |

## Consequences

**Positive:**
- Non-F&B stores never see restaurant UI — zero confusion, zero training overhead
- Post-v1 ERP modules ship as flagged additions without disrupting existing users
- Community contributors can build a new module without touching core app code —
  add a flag, register a nav graph, done
- Works identically on Play Store, F-Droid, and sideloaded builds

**Negative:**
- All module code is compiled into the APK regardless of enabled state, increasing
  base APK size incrementally as modules are added
- Feature flag state must be included in backup and restore to preserve configuration
- Enabling a module mid-session requires careful navigation stack management to avoid
  inconsistent back-stack behavior
