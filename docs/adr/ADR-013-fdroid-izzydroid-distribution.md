# ADR-013: F-Droid and IzzyOnDroid Distribution Strategy

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

This project is GPL v3 licensed and built for the open-source community. Distribution
exclusively through the Google Play Store would contradict the open-source philosophy,
exclude users on de-Googled Android builds, and create a dependency on Google's
approval process. F-Droid and IzzyOnDroid are the two primary FOSS Android distribution
channels and represent a meaningful segment of the privacy-conscious and technically
sophisticated users most likely to adopt this project early.

## Decision

The project targets **three distribution channels** in release order:

**1. GitHub Releases (immediate, from v0.1-alpha)**
Every tagged release produces a signed APK attached to the GitHub Release. This is the
primary distribution method during alpha and beta. Users install via direct download or
via Obtainium (an F-Droid-compatible app that tracks GitHub releases directly).

**2. IzzyOnDroid (from v0.2-alpha)**
IzzyOnDroid reads directly from GitHub Releases via a metadata file in the repository.
Every tagged release is automatically available to IzzyOnDroid users within hours of
publishing. The developer signs the APK — the same signature as the GitHub release.
This is the fastest path to F-Droid ecosystem availability and the recommended channel
before official F-Droid inclusion.

**3. F-Droid (from v1.0)**
F-Droid performs a full source review and builds the APK themselves from source,
signing it with their own key. The F-Droid signature differs from the GitHub and Play
Store signatures — users who switch channels must uninstall and reinstall. F-Droid
submission is pursued once the project is stable at v1.0 to ensure a clean, reviewed
first impression.

**4. Google Play Store (from v1.0)**
Play Store distribution uses a separate build flavor that includes the optional
`FcmBackend` for push notifications (see ADR-011). All other behavior is identical
to the F-Droid build.

**Architectural constraints imposed by this decision:**

| Constraint | Reason |
|---|---|
| Zero proprietary trackers | IzzySoft scans for and flags Firebase Analytics, Crashlytics, Adjust, etc. |
| No hard FCM dependency | FCM is a proprietary service — apps requiring it are rejected by F-Droid |
| No hardcoded API keys in source | All keys must be user-supplied at runtime |
| No non-free network services as hard dependencies | Google Maps, proprietary payment APIs cannot be required |
| Fully buildable from source | No binary blobs, no proprietary libraries in the repo |
| GPL v3 compatible dependencies only | All third-party libraries must be MIT, Apache 2.0, LGPL, or GPL |

All of these constraints are already satisfied by decisions made in ADR-001 through
ADR-012. The project is F-Droid compliant by architecture, not by retrofit.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Google Play Store only | Excludes de-Googled users; contradicts open-source philosophy |
| F-Droid only (no Play Store) | Excludes the majority of Android users; reduces adoption |
| IzzyOnDroid only, skip F-Droid | F-Droid provides greater long-term community trust and visibility |

## Consequences

**Positive:**
- Users on de-Googled builds (GrapheneOS, CalyxOS) are fully supported
- IzzyOnDroid provides immediate availability without waiting for F-Droid review
- GitHub Releases + Obtainium provides instant access for early adopters during alpha
- Play Store distribution maximizes general audience reach without compromising FOSS users
- The architectural constraints imposed are beneficial independently of distribution

**Negative:**
- Two APK build flavors (F-Droid and Play Store) must be maintained and tested
- F-Droid signs the APK with their own key — users migrating from GitHub/IzzyOnDroid
  to F-Droid must uninstall and reinstall, losing no data (backup/restore covers this)
- F-Droid review and build pipeline can be slow — submission must be planned well
  ahead of the desired v1.0 availability date on that channel
