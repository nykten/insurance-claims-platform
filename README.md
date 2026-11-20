# Insurance Claims Platform
Made for **Chubb Insurance** take home assessment.

Start time: **21:40 GMT+8, 20/11/2026**

Motor and property claims platform for '**CHX Insurance APAC**'. The repository contains two Spring Boot deployables, an Angular frontend, PostgreSQL, Kafka, and Mailpit.

## Architecture

- `backend/claims-service` owns identity, policies, claim lifecycle, reporting, and the transactional outbox on the `claims` database.
- `backend/notification-service` consumes `claim-events`, deduplicates events in the `notifications` database, and records email delivery through Mailpit.
- `frontend` is an Angular SPA served by nginx. `/api/` is proxied to claims-service.
- REST handles commands and queries. Kafka carries facts that have already been committed.

## Quick start from a fresh clone

Prerequisite: Docker Desktop with Compose.

```powershell
git clone <repository-url>
cd insurance-claims-platform
Copy-Item .env.example .env
docker compose up --build -d
docker compose ps
```

Replace `<repository-url>` with the repository's Git URL. The `.env` file is optional; the checked-in defaults are suitable for local development. The first startup builds the application images and may take several minutes.

Wait until the `claims-service`, `notification-service`, and `frontend` containers report `healthy`, then open http://localhost:4200. If startup fails, inspect the service logs with:

```powershell
docker compose logs -f claims-service notification-service frontend
```

To stop the platform while preserving the database volume:

```powershell
docker compose down
```

For local development, also install Java 21, Maven 3.9+, Node.js 22+, and npm.

To start only infrastructure for IDE/CLI development:

```powershell
docker compose --profile infra up -d
```

Local verification commands:

```powershell
cd backend
mvn -pl claims-service verify
cd ..\frontend
npm ci
npm test
npm run build
```

The Milestone 6 smoke flow runs from the repository root after Docker startup:

```powershell
.\scripts\smoke-test.ps1
```

## URLs

| Component | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Claims API | http://localhost:8080 |
| OpenAPI document | http://localhost:8080/v3/api-docs |
| Notification debug API | http://localhost:8081/notifications |
| Mailpit UI | http://localhost:8025 |

## Demo credentials

All development users use `Password123!`.

| Email | Role |
|---|---|
| `alice@example.com` | Claimant |
| `bob@example.com` | Claimant |
| `officer1@chx.com` | Claims officer |
| `officer2@chx.com` | Claims officer |
| `manager@chx.com` | Claims manager |

Development seed data is enabled by the `dev` profile in Docker and must not be used as production data.

## Implemented API surface

Authentication is `POST /auth/login`. Claimant endpoints are under `/claims`; officer queue and work endpoints are under `/officer`; manager reports currently expose `/reports/status-summary`, `/reports/workload`, and `/reports/exposure`. The notification service exposes `GET /notifications` for delivery verification.

See `docs/demo-script.md` for the end-to-end walkthrough and `docs/architecture.md` for service boundaries.
