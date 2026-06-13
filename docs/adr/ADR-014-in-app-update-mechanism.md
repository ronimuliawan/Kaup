# ADR-014: In-App Update Mechanism for GitHub and Sideload Builds

- **Date**: 2026-03-18
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

Kaup is distributed across three channels: Google Play Store, F-Droid/IzzyOnDroid,
and direct GitHub APK download (see ADR-013). Play Store and F-Droid both provide
automatic update delivery through their own client infrastructure — no in-app work
is needed for those channels. However, users who download the APK directly from
GitHub Releases have no automatic update mechanism. Without an in-app check, these
users may run outdated versions indefinitely without knowing a new release exists.
This is a meaningful gap, particularly during the alpha and beta phases when the
direct GitHub APK is the primary distribution channel.

## Decision

A three-flavor build system is adopted, expanding the two-flavor system implied in
ADR-013:

| Flavor | Update Mechanism | FCM |
|---|---|---|
| `playstore` | Google Play in-app update API | Optional enhancement |
| `fdroid` | F-Droid / IzzyOnDroid client (external) | Excluded |
| `github` | `kmp-app-updater` checking GitHub Releases API | Excluded |

The `fdroid` and `github` flavors share a `foss` base flavor. The only difference
between them is the update mechanism injected at build time. This keeps the flavor
matrix clean and avoids duplicating the F-Droid compliance logic.

**In-app update checker for the `github` flavor:**

`kmp-app-updater` (released February 2026) is used as the update checker for the
`github` flavor. It is a Kotlin Multiplatform library that:

- Calls the GitHub Releases API to fetch the latest release tag
- Compares the latest tag against the installed `versionName`
- If a newer version is available, fires a local notification and shows an
  in-app banner on the home screen
- Downloads the APK asset and uses Android `PackageInstaller` to prompt the user
- Requires zero server infrastructure — reads from the public GitHub API only

Implementation in `:core-network`:

```kotlin
// Injected only in the `github` build flavor via Hilt
class GitHubUpdateChecker @Inject constructor(
    private val context: Context
) : UpdateChecker {
    private val updater = AppUpdater.github(
        context = context,
        owner   = "your-username",
        repo    = "kaup"
    )

    override suspend fun checkForUpdate(): UpdateResult {
        return updater.check()
    }
}

// No-op implementation for playstore and fdroid flavors
class NoOpUpdateChecker @Inject constructor() : UpdateChecker {
    override suspend fun checkForUpdate(): UpdateResult = UpdateResult.UpToDate
}
```

The `UpdateChecker` interface is defined in `:shared-kmp/sync-contracts` alongside
`SyncBackend` and `NotificationBackend`, following the same pluggable pattern.

**Update check is triggered:**
- Once on app foreground, at most once every 24 hours (WorkManager periodic job)
- Never on the critical POS path — check is always async and non-blocking
- User can dismiss the update banner and the app never nags again until the
  next version is released

**User-facing update flow (github flavor):**
WorkManager fires update check job (once per 24 hours)
↓
GitHub Releases API called
↓
Latest tag > installed versionName?
├── No → silent, no UI shown
└── Yes → local notification fired
in-app banner shown on home screen:
"Kaup v1.x.x is available — What's new | Update | Dismiss"
↓
User taps "Update"
↓
APK downloaded to cache
↓
PackageInstaller prompts user to install
↓
App restarts to new version


**Obtainium** is recommended in `README.md` as an alternative for technically
savvy users who prefer an external manager to handle updates across all their
FOSS apps. Obtainium requires zero integration work — it reads the GitHub
Releases page directly.

**Play Store prohibition compliance:**
The `kmp-app-updater` dependency and `GitHubUpdateChecker` implementation are
strictly excluded from the `playstore` flavor via Gradle flavor-specific
source sets. The Play Store build cannot and does not download or install APKs
outside of the Google Play update mechanism.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| No in-app update check — rely on users following GitHub | Users on direct APK silently run outdated versions; unacceptable during alpha |
| Rely entirely on Obtainium | Requires users to install and configure a separate app — too much friction for non-technical store owners |
| Custom GitHub API polling from scratch | Unnecessary — `kmp-app-updater` solves this cleanly with a KMP-compatible library |
| Single APK with runtime flavor detection | Play Store prohibits APK self-installation — must be excluded at build time, not runtime |

## Consequences

**Positive:**
- GitHub/sideload users are notified of updates without needing a separate app
- Non-technical store owners on the direct APK path are kept up to date
- Obtainium remains the recommended path for technically savvy users — zero
  friction for them
- The `UpdateChecker` interface follows the same pluggable pattern as
  `SyncBackend` and `NotificationBackend` — consistent, contributor-friendly
- Play Store and F-Droid compliance is maintained by excluding the checker
  at build time

**Negative:**
- Three build flavors increase CI/CD pipeline complexity — three APK variants
  must be built, tested, and distributed on each release
- GitHub Releases API has a rate limit (60 unauthenticated requests per hour
  per IP) — the 24-hour check interval comfortably avoids this, but stores
  with many devices on the same public IP should be aware
- `kmp-app-updater` is a young library (released February 2026) — the
  maintainer should monitor it for breaking changes and have a fallback plan
  if it is abandoned (the fallback is a lightweight GitHub API call written
  directly in `:core-network`, which is straightforward)

## Files Affected

**`ROADMAP.md`** — add to Must Have under Settings:
In-app update notification for GitHub/sideload builds
(Play Store and F-Droid builds excluded — updates handled by their
respective stores)

**`ADR-013`** — update the distribution table to reflect three build flavors:
| Flavor | Channel | Update Mechanism |
|---|---|---|
| playstore | Google Play Store | Play in-app update API |
| fdroid | F-Droid / IzzyOnDroid | F-Droid client |


**`CONTRIBUTING.md`** — add a note under Development Setup:
Build flavors:
- ./gradlew :android-app:assembleGithubDebug → GitHub/sideload build
- ./gradlew :android-app:assembleFdroidDebug → F-Droid build
- ./gradlew :android-app:assemblePlaystoreDebug → Play Store build
