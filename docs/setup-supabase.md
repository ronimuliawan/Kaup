# Tier 2 — Supabase Sync Backend Setup Guide

Kaup supports optional syncing to Supabase for stores that want cloud sync
without managing their own server. Supabase is a free, open-source Firebase
alternative built on PostgreSQL.

---

## Quick Start

```bash
# 1. Create a Supabase project (free tier)
#    Go to https://supabase.com/dashboard
#    Click "Create a new project"
#    Choose a name, database password, and region

# 2. Wait for the database to be ready (~2 minutes)

# 3. Run the schema migration
#    In Supabase Dashboard → SQL Editor
#    Paste the contents of supabase/schema.sql and run

# 4. In Kaup (in Settings → Sync Backend)
#    - Sync Backend: Tier 2 (Supabase)
#    - Supabase URL: https://your-project.supabase.co
#    - Supabase Key: your-anon-key (from Supabase Dashboard → API)
#    - Username: your-email@example.com
#    - Password: your-database-password
```

---

## Prerequisites

- A free Supabase account at https://supabase.com
- No credit card required for free tier

---

## Supabase Project Setup

### Step 1 — Create Project

1. Go to https://supabase.com/dashboard
2. Click **"Create a new project"**
3. Fill in:
   - **Name**: `kaup` (or your store name)
   - **Database password**: `your-secure-password` (remember this)
   - **Region**: Choose closest to your store location
4. Click **"Create new project"**

### Step 2 — Get API Credentials

1. In Supabase Dashboard, go to **Settings → API**
2. Copy these values:
   - **Project URL**: `https://your-project.supabase.co`
   - **anon public key**: `your-anon-key`

### Step 3 — Run Schema Migration

1. In Supabase Dashboard, go to **SQL Editor**
2. Create a new query
3. Paste the contents of `supabase/schema.sql` (below)
4. Click **"Run"**

---

## Schema File: `supabase/schema.sql`

```sql
-- Core tables
CREATE TABLE locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    address TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL, -- OWNER, MANAGER, CASHIER, WAITER
    location_id UUID REFERENCES locations(id),
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- POS tables
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID REFERENCES locations(id),
    cashier_id UUID REFERENCES users(id),
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method TEXT NOT NULL,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE transaction_line_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID REFERENCES transactions(id),
    item_id UUID REFERENCES items(id),
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0,
    tax_rate DECIMAL(5,2),
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Inventory tables
CREATE TABLE items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID REFERENCES locations(id),
    name TEXT NOT NULL,
    category_id UUID REFERENCES categories(id),
    price DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2),
    reorder_level INTEGER DEFAULT 0,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE stock_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID REFERENCES locations(id),
    item_id UUID REFERENCES items(id),
    movement_type TEXT NOT NULL, -- IN, OUT, ADJUSTMENT
    quantity INTEGER NOT NULL,
    reason TEXT,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Auth tables
CREATE TABLE hotp_secrets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    secret_key TEXT NOT NULL,
    counter INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE override_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    reason TEXT NOT NULL,
    hotp_code TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Customer tables
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    loyalty_points INTEGER DEFAULT 0,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Supplier tables
CREATE TABLE suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    contact TEXT,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Expense tables
CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID REFERENCES locations(id),
    expense_category_id UUID REFERENCES expense_categories(id),
    amount DECIMAL(10,2) NOT NULL,
    date DATE NOT NULL,
    notes TEXT,
    receipt_file_path TEXT,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE expense_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Restaurant tables (v1.x)
CREATE TABLE tables (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID REFERENCES locations(id),
    name TEXT NOT NULL,
    status TEXT NOT NULL, -- EMPTY, OCCUPIED, RESERVED
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE table_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    table_id UUID REFERENCES tables(id),
    item_id UUID REFERENCES items(id),
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Sync row-level security (RLS)
ALTER TABLE locations ENABLE ROW LEVEL SECURITY;
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE items ENABLE ROW LEVEL SECURITY;
ALTER TABLE stock_movements ENABLE ROW LEVEL SECURITY;

-- Policy: Allow anon access for sync endpoints
CREATE POLICY "allow_sync_access" ON locations
    FOR ALL USING (true);

CREATE POLICY "allow_sync_access" ON users
    FOR ALL USING (true);

CREATE POLICY "allow_sync_access" ON transactions
    FOR ALL USING (true);

CREATE POLICY "allow_sync_access" ON items
    FOR ALL USING (true);

CREATE POLICY "allow_sync_access" ON stock_movements
    FOR ALL USING (true);
```

---

## Security Policy

Supabase's default RLS policy blocks all access. The `allow_sync_access` policy
above allows any authenticated Kaup user to sync data. This is acceptable for
small stores but not for multi-tenant setups. For production, implement user
scoped policies:

```sql
-- Policy: Users can only access their location's data
CREATE POLICY "location_scoped_access" ON transactions
    FOR ALL USING (
        location_id = (SELECT location_id FROM users WHERE id = auth.uid())
    );
```

---

## Kaup Configuration

In Kaup Settings → Sync Backend:

| Field | Value |
|---|---|
| Sync Backend | Tier 2 (Supabase) |
| Supabase URL | `https://your-project.supabase.co` |
| Supabase Key | `your-anon-key` |
| Username | `your-email@example.com` |
| Password | `your-database-password` |

---

## Troubleshooting

### Sync fails — "Invalid API key"

- Ensure you're using the `anon public` key, not the `service_role` key
- Check the key is copied correctly (no extra spaces)

### Sync fails — "RowCount mismatch"

- The server expects a JSON array response — Supabase returns an array by default
- This is a known issue with Supabase's `json` response format; ensure your
  sync endpoint returns `SELECT * FROM transactions` not `SELECT * FROM transactions LIMIT 1`

### Database connection timeout

- Check your Supabase project is active (not paused)
- Free tier projects pause after 1 week of no activity — wake them up by
  making any API call

---

## Cost

Supabase free tier includes:

- 500 MB database
- 2 GB/month file storage
- 2 GB/month bandwidth
- Unlimited API requests

Most small stores stay within free tier limits for 6–12 months before needing
the Pro plan ($25/month).

---

## Next Steps

After Supabase is configured:

1. Create your first sale
2. Verify it syncs to the Supabase dashboard
3. Test multi-device sync by creating a sale on a second device
4. Set up a backup schedule using Supabase's automated backups

See [ROADMAP.md](../../ROADMAP.md) for the full Kaup feature roadmap.