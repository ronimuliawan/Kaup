# User Personas

- **Version**: 1.0
- **Date**: 2026-03-14

---

## Persona 1 — The Solo Store Owner

**Name**: Budi
**Age**: 38
**Location**: Small town, Indonesia
**Store type**: General goods retail, single location
**Devices**: One mid-range Android phone (2 GB RAM)
**Tech comfort**: Can install apps, use WhatsApp, basic smartphone navigation.
Cannot configure servers or read error logs.

### A day in Budi's life
Budi opens the shop at 8 AM. He unlocks the app with his PIN, opens the shift,
and starts selling. Customers pay cash mostly. He prints receipts on a small
Bluetooth thermal printer he bought for under $20. At closing time, he wants
to know how much he made today and whether he needs to restock anything.
He has no time for anything complicated.

### What makes Budi succeed
- Getting to his first sale in under 10 minutes on day one
- The POS screen is fast and does not require internet
- Receipts print reliably without setup friction
- End-of-day summary is one tap away
- Backup is automatic or at most one tap — he will not remember to do it manually

### What makes Budi abandon the app
- A connectivity error blocking a sale
- Setup that requires technical decisions he does not understand
- An unexpected screen that is not relevant to his workflow
- Losing a week of sales data because backup was not configured

### Features that matter most
Must Have: POS core, receipt printing, basic sales report, onboarding wizard,
local backup

---

## Persona 2 — The Store with Staff

**Name**: Sari
**Age**: 44
**Location**: Mid-size city, Indonesia
**Store type**: Clothing boutique, one location
**Devices**: One Android tablet at the counter, one Android phone (Sari's own)
**Staff**: Sari (owner/manager) + 2 cashiers
**Tech comfort**: Comfortable with Android apps and basic business software.
Has used a POS app before. Cannot self-host a server but would consider a
managed cloud option.

### A day in Sari's life
Sari is not always at the counter. Her cashiers handle sales. She checks in
on reports from her phone during the day. She wants to know her cashiers cannot
void transactions or change prices without her approval. She configures what
her staff can and cannot do. If a customer demands a refund and the cashier
needs to process it, the cashier asks Sari, Sari gives a one-time code, done.

### What makes Sari succeed
- She can set up staff accounts and permissions without reading a manual
- She receives a daily sales summary she can check from anywhere
- Her cashiers are never blocked mid-sale, even if they need a manager action
- She can see exactly what each cashier sold in a given shift
- If a cashier tries something they should not, she gets a notification

### What makes Sari abandon the app
- A cashier accidentally voiding transactions without her knowledge
- Reports that are too complex or too slow to read quickly
- Setting up permissions taking more than 30 minutes the first time
- Staff being confused by features they do not need

### Features that matter most
Must Have: RBAC permission system, Manager Approval Overlay, shift management,
sales report per cashier
Should Have: ntfy remote notifications, server-provisioned HOTP

---

## Persona 3 — The F&B Operator

**Name**: Dian
**Age**: 31
**Location**: Major city, Indonesia
**Store type**: Small café, 12 tables
**Devices**: One Android tablet at the counter, one Android phone (Dian's own)
**Staff**: Dian (owner) + 2 crew members + 1 part-time barista
**Tech comfort**: Runs the café with a mix of WhatsApp orders and walk-ins.
Has tried a few POS apps but none handled tables well.

### A day in Dian's life
A crew member takes an order at a table on the tablet, assigns it to Table 7.
The barista sees the order and prepares it. The customer asks for the bill —
the crew member prints it from Table 7 in one tap. A group at Table 3 wants to
split their bill. This needs to work fast during a Saturday morning rush with
no time to learn a complex interface.

### What makes Dian succeed
- Enabling restaurant mode takes one toggle in settings
- Table assignment is visual and instant
- The crew role only sees what a crew member needs — no inventory screens, no reports
- Split bill and table merge work without asking for manager approval
- The app survives a full Saturday morning rush without a single crash

### What makes Dian abandon the app
- A crew member accidentally opening a report screen during a busy service
- Table state not updating correctly across the two devices
- Having to configure roles from scratch because the defaults are wrong
- Kitchen staff unable to see orders without a full manager account

### Features that matter most
Must Have: Restaurant module (table management, order assignment), Crew role,
RBAC permission system
Should Have: Multi-device sync (Tier 1 LAN), order routing per table

---

## Persona 4 — The IT-Savvy Operator

**Name**: Reza
**Age**: 27
**Location**: Any city
**Background**: Freelance developer. Sets up POS systems for small business
clients. Uses open-source software exclusively. Runs a personal home server.
**Devices**: Multiple Android devices, a home server running Docker
**Tech comfort**: Full — comfortable with Docker, Kotlin, Git, and Linux CLI

### A day in Reza's life
Reza evaluates this project for a client deployment. He forks the repo, reads
the ADRs to understand the architecture decisions, runs the Ktor server in
Docker in 10 minutes, and connects the Android app to it. He finds a bug in
the inventory module, writes a fix, and opens a PR. He wants to contribute a
payment integration for his region and needs a clear module contribution guide.

### What makes Reza succeed
- ADRs explain every major decision so he does not need to reverse-engineer intent
- Docker Compose setup for the Ktor server works in one command
- Module architecture is clean and contribution guide is thorough
- The `SyncBackend` interface lets him build a custom adapter without touching
  core code
- GPL v3 means his client deployments are legally straightforward

### What makes Reza abandon the project
- Undocumented architectural decisions that waste his time
- Module boundaries violated in the codebase
- No response to PRs within a reasonable window
- Proprietary dependencies that complicate client deployments

### Features that matter most
Must Have: Ktor server (Tier 1), pluggable SyncBackend, ADR documentation,
module guide, CONTRIBUTING.md
Should Have: Clean test coverage, CI/CD pipeline, self-hosting documentation
