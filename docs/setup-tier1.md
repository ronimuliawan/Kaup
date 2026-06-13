# Tier 1 — Self-Hosted Ktor Server Setup Guide

Kaup supports optional syncing to a self-hosted server for stores that want
multi-device sync without relying on a third-party provider. Tier 1 uses a
lightweight Kotlin server built with Ktor, containerised with Docker Compose.

---

## Quick Start

```bash
# 1. Clone the Kaup repo
cd kaup

# 2. Start the server
cd ktor-server
docker-compose up

# 3. Server is now running at https://localhost:8080

# 4. Create your first admin user (one-time)
curl -X POST https://localhost:8080/admin/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "your-secure-password",
    "role": "OWNER"
  }'

# 5. Configure Kaup to use this server (in Settings → Sync Backend)
#    - Server URL: https://your-machine-ip:8080
#    - Username: admin
#    - Password: your-secure-password
```

---

## Prerequisites

- Docker Engine 20.10 or later
- Docker Compose 2.0 or later
- Java 17 or later (for local development, not required for Docker run)
- A machine with a public IP or local network access for your Android devices

---

## Server Configuration

Edit `ktor-server/config/application.yml` before starting:

```yaml
ktor:
  application:
    modules:
      - KaupServer.main

deployment:
  port: 8080

kaup:
  server:
    # Basic auth for admin endpoints (change in production)
    admin-user:
      username: admin
      password: change-me-in-production

    # JWT token expiry (in hours)
    jwt-expiry-hours: 24

    # File upload max size (in MB)
    max-upload-size-mb: 10
```

**Security warning**: Change `admin-user.password` before deploying to a
public network. For production, use environment variables instead of
hardcoding credentials.

---

## Environment Variables (Production)

For production deployments, use a `.env` file instead of `application.yml`:

```bash
# ktor-server/.env
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your-secure-password
JWT_EXPIRY_HOURS=24
MAX_UPLOAD_SIZE_MB=10
DATABASE_URL=postgresql://postgres:password@db:5432/kaup
```

Add to `docker-compose.yml`:

```yaml
services:
  kaup-server:
    image: kaup-server:latest
    env_file:
      - .env
    ports:
      - "8080:8080"
    depends_on:
      - db
```

---

## Database Schema

The server uses PostgreSQL with the same schema as the Android Room database:

```sql
-- Core tables
CREATE TABLE locations (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    address TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    sync_status TEXT NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL,
    location_id UUID REFERENCES locations(id),
    sync_status TEXT NOT NULL
);

-- POS tables
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    location_id UUID REFERENCES locations(id),
    cashier_id UUID REFERENCES users(id),
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method TEXT NOT NULL,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE transaction_line_items (
    id UUID PRIMARY KEY,
    transaction_id UUID REFERENCES transactions(id),
    item_id UUID REFERENCES items(id),
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0,
    tax_rate DECIMAL(5,2),
    sync_status TEXT NOT NULL
);

-- Inventory tables
CREATE TABLE items (
    id UUID PRIMARY KEY,
    location_id UUID REFERENCES locations(id),
    name TEXT NOT NULL,
    category_id UUID REFERENCES categories(id),
    price DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2),
    reorder_level INTEGER DEFAULT 0,
    sync_status TEXT NOT NULL
);

CREATE TABLE stock_movements (
    id UUID PRIMARY KEY,
    location_id UUID REFERENCES locations(id),
    item_id UUID REFERENCES items(id),
    movement_type TEXT NOT NULL, -- IN, OUT, ADJUSTMENT
    quantity INTEGER NOT NULL,
    reason TEXT,
    sync_status TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Auth tables
CREATE TABLE hotp_secrets (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    secret_key TEXT NOT NULL,
    counter INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE override_logs (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    reason TEXT NOT NULL,
    hotp_code TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
```

The schema is automatically applied on first server start via Flyway migrations.

---

## API Endpoints

### Authentication

| Endpoint | Method | Description |
|---|---|---|
| `/auth/login` | POST | Authenticate user, return JWT token |
| `/auth/logout` | POST | Invalidate JWT token |

### Sync

| Endpoint | Method | Description |
|---|---|---|
| `/sync/push` | POST | Push PENDING records from Android |
| `/sync/pull` | GET | Pull SYNCED records updated after timestamp |
| `/sync/conflict` | POST | Resolve conflict via ConflictResolver |

### Admin

| Endpoint | Method | Description |
|---|---|---|
| `/admin/users` | POST | Create new user |
| `/admin/users/{id}` | GET | Get user details |
| `/admin/users/{id}` | PUT | Update user role or password |
| `/admin/users/{id}` | DELETE | Delete user |

---

## Running Locally (Development)

```bash
# 1. Start PostgreSQL
cd ktor-server
docker-compose up db

# 2. Run server locally (for debugging)
cd ktor-server
./gradlew run

# 3. Server starts at http://localhost:8080
```

---

## Troubleshooting

### Database connection fails

```bash
# Check PostgreSQL is running
docker-compose ps db

# Restart PostgreSQL
docker-compose restart db
```

### Server won't start

```bash
# Check logs
docker-compose logs kaup-server

# Rebuild image
docker-compose build --no-cache
```

### Android can't reach server

- Ensure your machine's IP is reachable from the Android device
- Check firewall rules allow port 8080
- For local network, use `http://your-local-ip:8080` (not HTTPS)
- For remote deployment, use HTTPS with a valid certificate

---

## Updating the Server

```bash
# 1. Pull latest Kaup changes
cd kaup
git pull origin main

# 2. Rebuild server image
cd ktor-server
docker-compose build kaup-server

# 3. Restart server
docker-compose up -d
```

---

## Security Checklist (Production)

- [ ] Change `admin-user.password` in `application.yml`
- [ ] Use HTTPS with a valid TLS certificate
- [ ] Set `JWT_EXPIRY_HOURS` to 24 or less
- [ ] Enable database password in environment variables (not hardcoded)
- [ ] Restrict firewall to allow only Android devices to reach port 8080
- [ ] Monitor logs for failed login attempts
- [ ] Back up PostgreSQL database regularly

---

## Next Steps

After Tier 1 is running:

1. Configure Kaup in Settings → Sync Backend → Tier 1
2. Enter your server URL, username, and password
3. Test sync by creating a sale and verifying it appears on another device
4. Set up a backup schedule for your PostgreSQL database

See [ROADMAP.md](../../ROADMAP.md) for the full Kaup feature roadmap.