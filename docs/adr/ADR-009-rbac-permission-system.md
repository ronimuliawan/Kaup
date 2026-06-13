# ADR-009: Role-Based Access Control and Manager Approval Overlay

- **Date**: 2026-03-14
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

The app is used by multiple staff members with different responsibilities — owners,
managers, and cashiers — often sharing a single device. Without access control, any
staff member could void transactions, access financial reports, delete products, or
change tax settings. Access control must be simple enough for a non-technical store
owner to configure, and must not block legitimate urgent operations (e.g., a cashier
needing a void when no manager is present).

## Decision

Access control uses a **two-layer model: Roles + Permission Overrides**.

**Layer 1 — Roles (broad defaults):**

| Role | Default Access |
|---|---|
| `OWNER` | Full access to everything; cannot be restricted |
| `MANAGER` | Full access except user management (configurable) |
| `CASHIER` | POS operations only |
| `WAITER` | Table management + order entry (🔒 `restaurantEnabled`) |

**Layer 2 — Permission overrides (granular, per-user):**
A manager can grant up (give a cashier `POS_VOID_TRANSACTION`) or restrict down
(prevent a manager from accessing `SETTINGS_BACKEND`) for any individual user,
overriding their role defaults.

Permissions are grouped by module:

- **POS**: `POS_OPEN_SHIFT`, `POS_CLOSE_SHIFT`, `POS_APPLY_DISCOUNT`,
  `POS_DISCOUNT_ABOVE_X`, `POS_VOID_TRANSACTION`, `POS_ISSUE_REFUND`,
  `POS_OVERRIDE_PRICE`
- **Inventory**: `INVENTORY_VIEW`, `INVENTORY_ADD_ITEM`, `INVENTORY_EDIT_ITEM`,
  `INVENTORY_DELETE_ITEM`, `INVENTORY_RECEIVE_STOCK`, `INVENTORY_TRANSFER_STOCK`
- **Customers**: `CUSTOMERS_VIEW`, `CUSTOMERS_ADD`, `CUSTOMERS_EDIT`,
  `CUSTOMERS_DELETE`
- **Reports**: `REPORTS_VIEW_SALES`, `REPORTS_VIEW_INVENTORY`,
  `REPORTS_VIEW_FINANCIAL`, `REPORTS_EXPORT`
- **Users**: `USERS_VIEW`, `USERS_ADD`, `USERS_EDIT`, `USERS_DELETE`
- **Settings**: `SETTINGS_BACKEND`, `SETTINGS_FEATURE_FLAGS`,
  `SETTINGS_HOUSEKEEPING`, `SETTINGS_TAX`

Permissions are loaded into memory when a session starts and cached as a
`Set<Permission>` in `SessionManager`. The UI **hides** restricted features entirely
— a cashier never sees the "Add Product" button. Greyed-out buttons are not used, as
they create confusion and increase training time.

**Manager Approval Overlay ("Break Glass"):**
When a cashier attempts an action they do not have permission for, a
`ManagerApprovalOverlay` appears as a bottom sheet. The cashier's session remains
active. A manager enters their PIN (or uses an HOTP code per ADR-005) to authorize
the single action. This avoids a full session switch for quick approvals and maintains
a complete audit trail of all elevated actions.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Role-only access (no per-user overrides) | Too rigid — real stores have staff with mixed responsibilities |
| Greyed-out buttons for restricted features | Creates confusion; staff try to tap disabled buttons and ask why |
| Full session switch for manager approval | Too slow for busy POS environments; disrupts cashier workflow |
| Server-side permission checks only | Requires connectivity — violates ADR-001 |

## Consequences

**Positive:**
- Non-technical store owners can configure access using familiar role concepts
- Advanced stores can fine-tune per-user permissions without creating new roles
- Manager Approval Overlay enables fast in-flow authorization without session disruption
- Hiding restricted UI reduces training time and eliminates accidental access attempts
- Permission checks work 100% offline — `Set<Permission>` is loaded from Room at
  session start

**Negative:**
- Permission catalogue must be kept in sync with every new feature added to the app —
  a new restricted action must always ship with a corresponding permission constant
- Per-user overrides add complexity to the user management UI
- If a manager's session secret (HOTP key) is not provisioned before an approval is
  needed, the overlay cannot complete — provisioning state must be clearly surfaced
  in the user management screen
