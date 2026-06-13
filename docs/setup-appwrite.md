# Tier 3 — Appwrite Sync Backend Setup Guide

Kaup supports optional syncing to Appwrite for stores that want cloud sync
with full self-hosting control. Appwrite is a free, open-source Firebase
alternative with a self-hosted option.

---

## Quick Start

```bash
# 1. Install Appwrite (self-hosted)
docker run -it --rm \
  --volume appwrite:/var/lib/appwrite \
  --volume appwrite-certificates:/certificates \
  --publish 80:80 \
  --publish 443:443 \
  --add-host host.docker.internal:host-gateway \
  appwrite/appwrite

# 2. Wait for setup to complete (~2 minutes)

# 3. Create a new project in Appwrite Console
#    Go to https://host.docker.internal:80
#    Create project named "kaup"

# 4. Run the schema migration
#    In Appwrite Console → Database → Create Database
#    Then run the SQL in appwrite/schema.sql

# 5. In Kaup (in Settings → Sync Backend)
#    - Sync Backend: Tier 3 (Appwrite)
#    - Appwrite Endpoint: http://host.docker.internal:80
#    - Project ID: kaup
#    - API Key: your-server-key
#    - Username: your-email@example.com
#    - Password: your-password
```

---

## Prerequisites

- Docker Engine 20.10 or later
- A machine with at least 2 GB RAM (4 GB recommended)
- For remote deployment: a domain name and HTTPS certificate

---

## Appwrite Installation

### Option A — Local (Development)

```bash
# Run Appwrite in Docker
docker run -it --rm \
  --volume appwrite:/var/lib/appwrite \
  --volume appwrite-certificates:/certificates \
  --publish 80:80 \
  --publish 443:443 \
  --add-host host.docker.internal:host-gateway \
  appwrite/appwrite

# Access at http://host.docker.internal:80
```

### Option B — Remote (Production)

For production, use a real domain with HTTPS:

```bash
# 1. Install Docker on your server
# 2. Copy the same command above
# 3. Replace host.docker.internal with your domain
# 4. Configure HTTPS with a valid certificate
```

---

## Appwrite Project Setup

### Step 1 — Create Project

1. Go to http://host.docker.internal:80 (or your domain)
2. Click **"Create a new project"**
3. Fill in:
   - **Name**: `kaup`
   - **Services**: Enable Database, Auth, Storage
4. Click **"Create"**

### Step 2 — Create Database

1. In Appwrite Console, go to **Database**
2. Click **"Create Database"**
3. Fill in:
   - **Name**: `kaup_db`
   - **ID**: `kaup_db` (use this exact ID)
4. Click **"Create"**

### Step 3 — Run Schema Migration

1. In Appwrite Console, go to **Database → kaup_db → Create Collection**
2. Create the following collections (one per table):

| Collection | ID | Permissions |
|---|---|---|
| locations | `locations` | Everyone (read + write) |
| users | `users` | Everyone (read + write) |
| transactions | `transactions` | Everyone (read + write) |
| transaction_line_items | `transaction_line_items` | Everyone (read + write) |
| items | `items` | Everyone (read + write) |
| categories | `categories` | Everyone (read + write) |
| stock_movements | `stock_movements` | Everyone (read + write) |
| hotp_secrets | `hotp_secrets` | Everyone (read + write) |
| override_logs | `override_logs` | Everyone (read + write) |
| customers | `customers` | Everyone (read + write) |
| suppliers | `suppliers` | Everyone (read + write) |
| expenses | `expenses` | Everyone (read + write) |
| expense_categories | `expense_categories` | Everyone (read + write) |
| tables | `tables` | Everyone (read + write) |
| table_orders | `table_orders` | Everyone (read + write) |

3. For each collection, add the same attributes as in the SQL schema below

### Step 4 — Get API Credentials

1. In Appwrite Console, go to **Project Settings → API**
2. Create a new server key:
   - **Name**: `kaup-android`
   - **Permissions**: Unlimited
3. Copy the **Key ID** — this is your `API Key`

---

## Schema Attributes

For each collection, add these attributes (matching the SQL schema):

### locations

| Attribute | Type | Required | Default |
|---|---|---|---|
| id | String | ✓ | UUID |
| name | String | ✓ | — |
| address | String | ✗ | null |
| is_default | Boolean | ✓ | false |
| sync_status | String | ✓ | PENDING |
| created_at | DateTime | ✓ | NOW |

### users

| Attribute | Type | Required | Default |
|---|---|---|---|
| id | String | ✓ | UUID |
| username | String | ✓ | — |
| password_hash | String | ✓ | — |
| role | String | ✓ | CASHIER |
| location_id | String | ✓ | null |
| sync_status | String | ✓ | PENDING |
| created_at | DateTime | ✓ | NOW |

*(Repeat for all other collections using the same pattern as the SQL schema below)*

---

## SQL Schema Reference: `appwrite/schema.sql`

```sql
-- Same schema as Supabase and Tier 1 — see supabase/schema.sql
-- Appwrite uses JSON documents instead of SQL, so you create collections
-- via the Console instead of running SQL.
```

---

## Kaup Configuration

In Kaup Settings → Sync Backend:

| Field | Value |
|---|---|
| Sync Backend | Tier 3 (Appwrite) |
| Appwrite Endpoint | `http://host.docker.internal:80` (or your domain) |
| Project ID | `kaup` |
| API Key | `your-server-key` |
| Username | `your-email@example.com` |
| Password | `your-password` |

---

## Troubleshooting

### Appwrite won't start

```bash
# Check Docker containers
docker ps

# Restart Appwrite
docker-compose restart
```

### Sync fails — "Invalid API key"

- Ensure you're using a server key with **Unlimited** permissions
- Check the key ID is copied correctly (no extra spaces)

### Sync fails — "Collection not found"

- Ensure the collection ID matches exactly (case-sensitive)
- Check the collection has **Everyone** read + write permissions

---

## Cost

Appwrite is free to self-host. You only pay for:

- Your own server hosting ($5–20/month for a small instance)
- Your own domain and HTTPS certificate ($0–15/year)

No Appwrite licensing fees.

---

## Next Steps

After Appwrite is configured:

1. Create your first sale
2. Verify it syncs to the Appwrite database
3. Test multi-device sync by creating a sale on a second device
4. Set up automated backups using your hosting provider's backup tools

See [ROADMAP.md](../../ROADMAP.md) for the full Kaup feature roadmap.