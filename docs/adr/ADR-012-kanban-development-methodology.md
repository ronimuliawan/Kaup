# ADR-012: Kanban Development Methodology

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

This project is developed by a solo maintainer with community contributions. Scrum's
ceremonies — sprint planning, daily standups, sprint reviews, retrospectives — are
designed for team coordination and produce overhead without value in a solo context.
Two-week sprint boundaries also create artificial pressure that leads to scope-cutting
shortcuts in a complex, long-horizon project. A methodology is needed that supports
focused work, flexible reprioritization, and open contribution without forcing a
fixed cadence.

## Decision

The project adopts **Kanban** as its development methodology, managed via
**GitHub Projects**.

**Board structure:**

```
Backlog → Ready → In Progress (WIP ≤ 2) → In Review → Done
```

- **Backlog** — all items from the MoSCoW not yet started; labeled by priority
  (`must-have`, `should-have`, `could-have`) and module (`:feature-pos`, etc.)
- **Ready** — fully defined items with written acceptance criteria; ready to pull
- **In Progress** — actively being built; hard WIP limit of 2 items at any time
- **In Review** — code complete, CI passing, self-review done, tests written
- **Done** — merged, tested, documented

The **WIP limit of 2** is the most important rule. The most common solo developer
failure mode is starting multiple features simultaneously, losing context on all of
them, and shipping none of them cleanly. A WIP limit enforces finishing before starting.

**Release cadence — milestone-based, not time-boxed:**

| Milestone | Scope |
|---|---|
| `v0.1-alpha` | Must Have core — POS, Inventory, Auth, Sync Engine |
| `v0.2-alpha` | Should Have complete — first public release, IzzyOnDroid submission |
| `v1.0` | All Must Have + Should Have stable and documented — Play Store + F-Droid |
| `v1.x` | Could Have — community contributions, incremental releases |
| `v2.0` | Post Year 1 — Web Dashboard, ERP deep layer |

Releases ship when the milestone is genuinely done — not on a calendar date.

**Community contribution flow:**
- Issues labeled `good first issue` are pre-defined, well-scoped tasks for new contributors
- A contributor comments on an issue to claim it before starting work
- PRs reference the issue they close (`Closes #42`)
- GitHub Projects board updates automatically on PR merge

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Scrum with two-week sprints | Ceremony overhead without value for a solo developer |
| No methodology, ad-hoc development | Context switching, no visibility for contributors, unpredictable |
| Linear / time-boxed releases | Artificial deadline pressure leads to quality shortcuts |

## Consequences

**Positive:**
- WIP limit enforces focused delivery — features are finished before new ones start
- Milestone-based releases mean v1.0 ships when it is ready, not when a date arrives
- GitHub Projects board gives community contributors full visibility into project status
- Flexible reprioritization — community feedback or a critical bug can move a card
  without breaking any sprint commitment

**Negative:**
- Without time pressure, scope creep is a risk — the MoSCoW must be respected
  as the boundary for each milestone
- A solo maintainer bottleneck on PR reviews can slow community contributions —
  clear review turnaround expectations must be set in `CONTRIBUTING.md`
