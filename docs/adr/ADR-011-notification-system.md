# ADR-011: Notification System (Local + ntfy, No Hard FCM Dependency)

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

The app needs to notify users of operationally significant events: low stock, sync
failures, shift reminders, and backup reminders. For stores with a server (Tier 1–3),
remote push notifications to an off-site manager's device are also desirable. Firebase
Cloud Messaging (FCM) is the standard Android push solution but is a proprietary Google
service — apps that require FCM as a hard dependency are rejected by F-Droid and do not
function on de-Googled Android builds (GrapheneOS, CalyxOS).

## Decision

The notification system is built around a **`NotificationBackend` interface**, following
the same pluggable pattern as `SyncBackend` (ADR-004). Three concrete implementations
are provided:

```kotlin
interface NotificationBackend {
    fun scheduleLocalAlert(event: NotificationEvent)
    fun cancelAlert(eventId: String)
    fun isRemoteCapable(): Boolean
}
```

**`LocalNotificationBackend` — Always available, zero dependencies**
Uses Android `NotificationManager` for immediate alerts and `AlarmManager` for
scheduled reminders (shift open, backup reminder). WorkManager fires local
notifications when background conditions are met (low stock threshold crossed, sync
retries exhausted). This backend works on every Android build including F-Droid and
de-Googled devices.

**`NtfyBackend` — Self-hosted remote push, F-Droid compliant**
ntfy is an open-source push notification server the user self-hosts (or uses the
free tier at ntfy.sh). The Ktor server sends a single HTTP POST to the ntfy instance
when a remote alert is needed. The manager's Android device runs the ntfy app
(available on F-Droid) to receive notifications. No Google Services required.
Available for Tier 1–3 stores.

**`FcmBackend` — Play Store build only, optional enhancement**
FCM is included only in the Play Store build variant and is never a hard dependency.
F-Droid and sideloaded builds omit this implementation entirely at compile time via
a build flavor. If Google Play Services are not present at runtime, the app silently
falls back to `NtfyBackend` or `LocalNotificationBackend`.

**Notification events covered:**
- Low stock alert — WorkManager periodic check, fires when item falls below threshold
- Sync failure alert — fires when WorkManager retry budget is exhausted
- Shift open reminder — daily, user-configured time, AlarmManager
- Backup reminder — fires after user-configured days without a completed backup
- Manager override request — fires on manager's device when staff requests elevation
  (on-device for Tier 0; via ntfy/FCM for Tier 1–3)

**TrustedTime API** is used where available to improve scheduled notification accuracy.
On builds without Google Play Services, `System.currentTimeMillis()` is used as
fallback — clock drift over the relevant reminder windows (hours, not weeks) is not
a practical concern on modern Android devices.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| FCM as hard dependency | F-Droid incompatible; fails on de-Googled builds |
| No remote notifications at all | Eliminates off-site manager approval and cross-device alerts |
| WebSocket persistent connection to server | Battery drain; unreliable on Android Doze mode |
| Polling for remote events | Excessive battery and data usage; poor UX latency |

## Consequences

**Positive:**
- Local notifications work on 100% of Android builds including F-Droid
- ntfy provides genuine remote push without any Google dependency
- FCM is available as an enhancement for Play Store users without compromising others
- The `NotificationBackend` interface allows community contributions of new push
  providers (e.g., Gotify, Unified Push) without touching core app code

**Negative:**
- ntfy requires the user to either self-host a server or trust ntfy.sh — adds a
  setup step for remote notifications that must be clearly documented
- Three backend paths must be tested independently
- FCM build flavor adds complexity to the CI/CD pipeline (two APK variants)
