# ADR-016: Multi-Location Support from Initial Schema Design

- **Date**: 2026-03-22
- **Status**: Accepted
- **Deciders**: Core maintainer

---

## Context

Kaup v1 targets single-location stores. However, a store owner who expands to a
second location will need stock movements, users, reports, and purchase orders to
be scoped to a location. Retrofitting `location_id` into a schema designed for
single-store requires a breaking migration that touches nearly every core table
and risks data corruption for existing users. The cost of adding `location_id`
from the start is minimal — a single foreign key on each affected table and a
seeded default location record on first launch.

## Decision

The Room schema includes `location_id` on all location-aware tables from the
initial schema definition. On first launch, a single `Location` record is
seeded automatically:

```kotlin
@Entity(tableName = "locations")
data class Location(
    @PrimaryKey val id         : String = UUID.randomUUID().toString(),
    val name                   : String,    // e.g. "Main Store"
    val address                : String?,
    val isDefault              : Boolean = true,
    val syncStatus             : SyncStatus = SyncStatus.PENDING
)
```

All location-aware entities carry a non-null `location_id` with a foreign key
to `locations.id`:

```kotlin
// Example — StockMovement
@Entity(tableName = "stock_movements")
data class StockMovement(
    ...
    val locationId : String,    // FK → locations.id, default = seeded location
    ...
)
```

**Location-aware tables** (all carry `location_id`):
`stock_movements`, `items`, `shift_records`, `transactions`,
`transaction_line_items`, `users`, `tables` (restaurant),
`table_orders` (restaurant), `purchase_orders`

**Non-location-aware tables** (shared across locations):
`customers`, `suppliers`, `categories`, `expense_categories`,
`hotp_secrets`, `override_logs`, `backup_log`, `notification_log`

**v1 behavior:**
In v1, the UI never exposes location selection — there is only one location
and all records are automatically assigned to it. The `location_id` foreign key
is present in the schema but invisible to the user. Multi-location UI (location
selector in reports, stock transfer between locations, per-location user
assignment) is a Could Have item unlocked in v1.x.

**Multi-device behavior:**
Each Android device is associated with a location. When a second location is
added in v1.x, devices are reassigned in Settings. The sync engine uses
`location_id` to correctly attribute movements during conflict resolution.

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Single-location schema in v1, migrate later | Breaking migration touches every core table — high data corruption risk |
| Optional `location_id` (nullable) | Nullable foreign keys create ambiguity in queries and conflict resolution |
| Separate database file per location | Cannot query across locations for reports; complex backup and restore |

## Consequences

**Positive:**
- Zero breaking migration when multi-location UI is built in v1.x
- `ConflictResolver` in `:shared-kmp` can use `location_id` from day one
- Reports can be filtered by location from day one even before the UI exposes it
- No data loss risk for existing users when the feature is unlocked

**Negative:**
- Every Room query that touches location-aware tables must include a
  `WHERE location_id = :currentLocationId` filter — this must be enforced
  in all DAO implementations from the start
- Contributors adding new location-aware tables must remember to include
  `location_id` — must be documented in `CONTRIBUTING.md`
- The seeded default location must be created in a Room callback on database
  open, before any other writes occur