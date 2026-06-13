# ADR-006: GPL v3 License Across All Project Artifacts

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

This project is intended as a permanent community contribution. POS and ERP software
has a documented history of open-source codebases being forked into closed commercial
products without improvements being returned to the community. The license must protect
against this while keeping the project freely accessible to all users and contributors.

## Decision

All project artifacts — Android app (`:android-app`), Ktor server (`:ktor-server`), and
the KMP shared module (`:shared-kmp`) — are licensed under **GPL v3 (GNU General Public
License version 3)**.

GPL v3 requires that any software derived from this project — including forks, commercial
deployments, and modified versions — must be distributed under GPL v3 with full source
code made available. GPL v3 also includes explicit patent protection.

LGPL v3 for the `:shared-kmp` module was considered. LGPL allows proprietary software
to link against a library without open-sourcing the host application. This was rejected
because it would allow commercial actors to build closed products on the shared domain
logic without contributing back — directly contradicting the community-first intent of
the project.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| MIT | No copyleft — anyone can fork, close-source, and sell without contributing back |
| Apache 2.0 | Same as MIT for copyleft purposes; adds patent grant but no share-alike |
| LGPL v3 for `:shared-kmp` | Allows proprietary products to use shared logic without reciprocating |
| AGPL v3 | Stronger network copyleft; considered but GPL v3 is sufficient for this scope |

## Consequences

**Positive:**
- All improvements made by any user or organization must be returned to the community
- Prevents commercial actors from building closed POS products on this codebase
- Consistent license across all artifacts — no ambiguity for contributors or users
- Compatible with F-Droid distribution requirements

**Negative:**
- Medium friction for corporate adoption — companies with strict open-source policies
  may avoid GPL v3 dependencies
- All third-party libraries used must be GPL-compatible (MIT, Apache 2.0, LGPL, and
  GPL are all compatible; proprietary SDKs are not permitted)
- Contributors must understand that their contributions are irrevocably licensed under
  GPL v3
