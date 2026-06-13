# ADR-017: Receipt Email — Android Intent Default with Optional SMTP

- **Date**: 2026-03-22
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

Customers increasingly expect a digital receipt option. Two approaches exist for
sending receipts by email from an Android app: firing a standard Android share
intent (the OS handles delivery) or integrating an SMTP library (the app sends
directly). Both have meaningful trade-offs and serve different store contexts.
A single approach cannot optimally serve both a non-technical solo store owner
and a larger store with a configured business email system.

## Decision

A `ReceiptEmailSender` interface is defined in `:shared-kmp/sync-contracts` with
two concrete implementations, selectable in Settings:

```kotlin
interface ReceiptEmailSender {
    suspend fun send(
        toAddress  : String,
        receiptPdf : ByteArray,
        subject    : String
    ): EmailResult
    fun isConfigured(): Boolean
}
```

**`IntentEmailSender` — Default, zero configuration**
Composes the receipt as a PDF attachment and fires `ACTION_SEND` with
`type = "message/rfc822"`. Android opens the system share sheet. The cashier
selects the customer's preferred email app and taps Send. No credentials
required, no setup, works on every device, fully F-Droid compliant.

This is the active implementation when no SMTP credentials are configured.

**`SmtpEmailSender` — Optional, store-configured**
Connects to a store-configured SMTP server and sends the receipt silently
in the background immediately after sale completion. The customer receives
the email automatically without any cashier action. SMTP credentials
(host, port, username, encrypted password) are stored in Android Keystore.

Configuration UI is in `:feature-settings`. A test email button confirms
the connection before saving credentials. WorkManager retries failed sends
with exponential backoff.

Supported SMTP configurations:
- Gmail (via App Password — store owner must enable 2FA and create an
  App Password; standard Gmail OAuth is not used to avoid Google dependency)
- Any SMTP server (host + port + TLS + credentials)
- Self-hosted mail server (pairs naturally with Tier 1–2 store operators)

**UI flow for receipt email at checkout:**
1. If customer record has an email address — email option shown automatically
2. If no customer record — cashier can type or scan an email address
3. `IntentEmailSender` active → share sheet opens
4. `SmtpEmailSender` active → receipt sends silently, confirmation shown

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| SMTP only | Requires credentials setup — too much friction for non-technical users |
| Intent only | No seamless background sending — adds cashier steps for every email receipt |
| Transactional email API (SendGrid, Mailgun) | Proprietary dependency, API key required, F-Droid risk, ongoing cost |
| No email receipts | Misses a genuine user expectation in 2026 |

## Consequences

**Positive:**
- Every store gets receipt email from day one with zero setup via Intent
- Stores that invest in SMTP configuration get a seamless, professional flow
- No proprietary email API dependency in the core app
- SMTP credentials stored in Android Keystore — never in plaintext

**Negative:**
- `IntentEmailSender` adds 3–4 cashier taps per email receipt — acceptable
  for low-volume stores, annoying for high-volume — motivates SMTP setup
- Gmail App Passwords require 2FA to be enabled — an extra step some store
  owners may find confusing; must be documented clearly in setup guide
- SMTP send failures during a busy shift are retried in the background but
  the cashier may not notice — a failed-send notification via
  `LocalNotificationBackend` is required