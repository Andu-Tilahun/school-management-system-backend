# School Management System — Backend

Spring Boot microservices backend for the School Management System (SMS). Provides authentication, user management, and a policy-based RBAC platform. School domain features (students, classes, grades, etc.) are being built in `core-service`.

## Tech stack

| Layer | Technology |
|-------|------------|
| Runtime | Java 17 |
| Framework | Spring Boot 3.4.5, Spring Cloud Gateway |
| Database | PostgreSQL 15 + Flyway |
| Cache | Redis |
| Messaging | Kafka |
| Auth | JWT (access + refresh tokens) |

## Modules

| Module | Port | Status | Description |
|--------|------|--------|-------------|
| `api-gateway` | 8080 | Partial | HTTP entry point, JWT validation, CORS |
| `user-service` | 8081 | **Ready** | Auth, users, RBAC admin, navigation menu |
| `core-service` | 8086 | In progress | School domain (e.g. students) |
| `common/common-security` | — | Shared | JWT, `@RequiresPermission`, token blacklist |
| `common/common-application` | — | Shared | `ApiResponse`, exceptions, shared DTOs |

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose (for infrastructure)

## Quick start

### 1. Start infrastructure

```bash
docker compose up -d postgres redis kafka
```

On first start, Postgres creates the `user_db` database automatically via `docker/postgres/init/01-create-databases.sql`.

If Postgres already has an existing volume, create the database manually:

```sql
CREATE DATABASE user_db;
```

### 2. Build common modules

Install shared libraries before building services:

```bash
cd common/common-application
../../user-service/mvnw -DskipTests install

cd ../common-security
../../user-service/mvnw -DskipTests install
```

### 3. Run user-service

```bash
cd user-service
./mvnw -DskipTests spring-boot:run
```

Service starts on **http://localhost:8081**.

Swagger UI: **http://localhost:8081/swagger-ui.html**

### 4. Default admin credentials

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `ChangeMe123!` |
| Email | `admin@example.com` |

### 5. Login example

```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "ChangeMe123!"
}
```

## API overview (user-service)

| Prefix | Description |
|--------|-------------|
| `/api/auth/*` | Login, refresh, logout, forgot/reset password |
| `/api/users/*` | User CRUD, profile, navigation menu, UI actions |
| `/api/users/scopes` | Scope admin |
| `/api/users/resources` | Resource admin |
| `/api/users/permissions` | Permission admin |
| `/api/users/policies` | Policy admin |
| `/api/users/groups` | Group admin |
| `/api/internal/users/*` | Service-to-service user APIs |

## RBAC model

Authorization uses a policy-based model:

```
User → Groups → Policies → Permissions → Resource + Scopes
  └──→ Policies (direct)
```

- **READ** — API access without sidebar menu entry
- **VIEW** — Menu visibility (implies READ)
- Controllers use `@RequiresPermission(resource = "USERS", scope = "READ")`
- Admin policy: `SUPER_ADMIN_FEATURES`

Seeded resources: `HOME`, `USERS`, `SCOPES`, `RBAC_RESOURCES`, `PERMISSIONS`, `POLICIES`, `GROUPS`.

## Database migrations (user-service)

Flyway migrations run automatically on startup (`user_db`):

| Version | Description |
|---------|-------------|
| V1 | Users table |
| V2 | Password reset tokens |
| V3 | Default admin user |
| V4 | RBAC schema (scopes, resources, permissions, policies, groups) |
| V5 | Navigation menu table |
| V6 | Seed scopes, resources, admin policy |
| V7 | Seed menu, admin group membership |
| V8 | Seed permissions and policy links |
| V15 | Rename `ADMIN_ALL_FEATURES` policy to `SUPER_ADMIN_FEATURES` |

> **Note:** If you previously ran older migration versions, drop and recreate `user_db` (or wipe the Postgres Docker volume) before starting fresh.

## Configuration

Key settings in `user-service/src/main/resources/application.yml`:

| Setting | Default |
|---------|---------|
| Server port | `8081` |
| Database | `jdbc:postgresql://localhost:5432/user_db` |
| DB user / password | `postgres` / `postgres` |
| Redis | `localhost:6379` |
| Kafka | `localhost:9092` |
| JWT secret | Set via `JWT_SECRET` env var in production |

## Docker Compose services

| Service | Port | Purpose |
|---------|------|---------|
| postgres | 5432 | PostgreSQL |
| redis | 6379 | Token blacklist, user status cache |
| kafka | 9092 | Notification events |
| kafka-ui | 8090 | Kafka admin UI |
| pgAdmin | 5050 | Database admin UI |
| minio | 9000 / 9001 | Object storage (future use) |

## Project structure

```
school-management-system-backend/
├── api-gateway/          # Spring Cloud Gateway
├── core-service/         # School domain (students, etc.)
├── user-service/         # Auth + users + RBAC
├── common/
│   ├── common-application/
│   └── common-security/
├── docker/
│   └── postgres/init/    # DB init scripts
└── docker-compose.yml
```

## Roadmap

- [ ] School domain APIs in `core-service` (students, teachers, classes, grades)
- [ ] Gateway routes for `core-service`
- [ ] SMS-specific RBAC resources and menu items
- [ ] Frontend (Angular, port 4200)
