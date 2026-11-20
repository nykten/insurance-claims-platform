# Technical Specification: CHX Claims Platform

## 1. System Overview & Architecture
The **CHX Claims Platform** is an event-driven insurance claims processing system built as a single monorepo. It enables policyholders (`CLAIMANT`) to submit and track claims, claims adjusters (`CLAIMS_OFFICER`) to pick up, assess, and adjudicate claims, and managers (`CLAIMS_MANAGER`) to monitor operational exposure and workload.

### 1.1 Tech Stack & Versions
- **Java / Backend:** Java 21, Spring Boot 3.4.x (Web, Data JPA, Security, Validation, Kafka, Mail), Flyway, Springdoc OpenAPI 2.x, ArchUnit.
- **Database:** PostgreSQL 16 (Single instance, two logically isolated databases: `claims` and `notifications`).
- **Messaging:** Apache Kafka (KRaft mode, single broker for local/compose) + Transactional Outbox Pattern.
- **Email Sandbox:** Mailpit (SMTP server + Web UI for capturing outbound emails).
- **Frontend:** Angular 19+ (Standalone Components, Signals, Reactive Forms, Angular Material, `ng-openapi-gen`).
- **Containerization:** Docker & Docker Compose (Multi-stage builds).

### 1.2 Service Boundaries & Decoupling Rules
1. **`claims-service` (Port 8080):** Owns identity/auth, policy validation stub, claim lifecycle state machine, queue assignment, reporting read-models, and the transactional outbox. Connects ONLY to the `claims` database.
2. **`notification-service` (Port 8081):** Consumes domain events from Kafka topic `claim-events`, enforces consumer idempotency, and dispatches emails via SMTP. Connects ONLY to the `notifications` database.
3. **Zero Shared Java Modules:** Services do NOT share a common events JAR. Each service defines its own DTOs/records following a tolerant-reader pattern against the contract defined in `docs/events.md`.
4. **Self-Contained Events:** All Kafka event payloads MUST include `claimantEmail` and `claimantName` so `notification-service` never makes synchronous REST callbacks to `claims-service`.

---

## 2. Repository Structure
Create the exact directory layout below:

```text
chx-claims-platform/
├── README.md
├── docker-compose.yml
├── .env.example
├── infra/
│   └── postgres/
│       └── init.sql
├── docs/
│   ├── architecture.md
│   ├── decisions/
│   │   ├── 001-two-services.md
│   │   ├── 002-outbox.md
│   │   ├── 003-optimistic-locking.md
│   │   └── 004-scope-and-cuts.md
│   ├── events.md
│   ├── ai-journal.md
│   └── demo-script.md
├── backend/
│   ├── pom.xml
│   ├── claims-service/
│   └── notification-service/
└── frontend/
```

---

## 3. Infrastructure & Docker Compose (`docker-compose.yml`)
Configure two Compose profiles:
- **`infra` profile:** Starts `postgres`, `kafka`, and `mailpit` (used during local IDE/CLI development).
- **`default` (no profile flag):** Starts `infra` services PLUS `claims-service`, `notification-service`, and `frontend`.

### Services Configuration
1. **`postgres`:** `postgres:16-alpine`. Mount `./infra/postgres/init.sql` to `/docker-entrypoint-initdb.d/init.sql`.
   - `init.sql` must execute:
     ```sql
     CREATE DATABASE claims;
     CREATE DATABASE notifications;
     ```
2. **`kafka`:** `apache/kafka:3.8.0` (or `confluentinc/cp-kafka` in KRaft mode, no Zookeeper). Expose `9092` to host, internal listener `kafka:29092`. Auto-create topics disabled; `claims-service` creates `claim-events` via `NewTopic` bean.
3. **`mailpit`:** `axllent/mailpit:latest`. Expose `1025` (SMTP) and `8025` (Web UI).
4. **`claims-service`:** Multi-stage Dockerfile (`maven:3.9-eclipse-temurin-21` -> `eclipse-temurin:21-jre-alpine`). Runs with `SPRING_PROFILES_ACTIVE=dev` by default in compose so seed data loads. Port `8080`.
5. **`notification-service`:** Multi-stage Dockerfile. Port `8081`.
6. **`frontend`:** Multi-stage Dockerfile (`node:22-alpine` -> `nginx:alpine`). Port `4200:80`. `nginx.conf` proxies `/api/` to `http://claims-service:8080/` (stripping `/api` prefix or matching backend context path—standardize on stripping `/api` in proxy so backend endpoints remain `/auth/login`, `/claims`, etc.) and handles SPA fallback (`try_files $uri $uri/ /index.html;`).

---

## 4. Backend Specification: `claims-service`

### 4.1 Architectural & Package Rules (Enforce via ArchUnit)
Base package: `com.chx.claims`
Layout is **package-by-feature**:
- `common`: `config`, `error`, `web`
- `identity`: `api`, `domain`, `infra`, `security`
- `policy`: `Policy`, `PolicyRepository`, `PolicyValidator`
- `claim`: `api` (`dto`), `application`, `domain`, `infra`
- `assignment`: `api`, `application`, `infra`
- `reporting`: `api`, `application`, `infra`
- `outbox`: `events`

**Mandatory Dependency Rules:**
1. `..api..` classes MUST NOT depend on `..infra..` (repositories). Controllers only call `..application..` services.
2. `reporting..` MUST NOT depend on `claim.domain..` mutating methods or `ClaimCommandService`. It queries the DB directly via native SQL or JPQL projections in `ReportingRepository`.
3. External feature packages (`assignment`, `identity`, etc.) MUST NOT access `ClaimRepository` directly; they must interact through `ClaimCommandService` or `ClaimQueryService`.
4. All `Claim` state transitions MUST happen inside domain methods on `Claim.java`, which validate `ClaimStatus` transitions, append a `ClaimStatusHistory` entity, and return/record an outbox event within the same `@Transactional` boundary.

### 4.2 Database Schema (Flyway Migrations)
- **`V1__core_schema.sql`**:
  - `users`: `id` (UUID PK), `email` (VARCHAR UNIQUE), `password_hash` (VARCHAR), `full_name` (VARCHAR), `role` (VARCHAR: `CLAIMANT`, `CLAIMS_OFFICER`, `CLAIMS_MANAGER`), `created_at` (TIMESTAMPTZ).
  - `policies`: `id` (UUID PK), `policy_number` (VARCHAR UNIQUE), `holder_id` (UUID FK -> users), `claim_type` (VARCHAR: `MOTOR`, `PROPERTY`), `market` (VARCHAR: `SG`, `MY`, `HK`), `coverage_limit` (NUMERIC(15,2)), `currency` (VARCHAR(3)), `valid_from` (DATE), `valid_to` (DATE), `status` (VARCHAR: `ACTIVE`, `EXPIRED`, `CANCELLED`).
  - `claims`: `id` (UUID PK), `claim_number` (VARCHAR UNIQUE, e.g., `CHX-SG-000001`), `policy_id` (UUID FK), `claimant_id` (UUID FK), `assigned_officer_id` (UUID FK NULL), `claim_type` (VARCHAR), `market` (VARCHAR), `status` (VARCHAR), `incident_date` (DATE), `description` (TEXT), `incident_details` (JSONB or structured columns: `vehicle_reg`, `driver_license`, `property_address`, `damage_category`), `claimed_amount` (NUMERIC(15,2)), `reserve_amount` (NUMERIC(15,2) NULL), `approved_amount` (NUMERIC(15,2) NULL), `rejection_reason` (TEXT NULL), `currency` (VARCHAR(3)), `version` (BIGINT NOT NULL DEFAULT 0), `created_at` (TIMESTAMPTZ), `updated_at` (TIMESTAMPTZ).
  - `claim_status_history`: `id` (UUID PK), `claim_id` (UUID FK), `from_status` (VARCHAR NULL), `to_status` (VARCHAR NOT NULL), `changed_by` (UUID FK), `note` (TEXT NULL), `changed_at` (TIMESTAMPTZ).
  - `info_requests`: `id` (UUID PK), `claim_id` (UUID FK), `requested_by` (UUID FK), `question` (TEXT NOT NULL), `response` (TEXT NULL), `requested_at` (TIMESTAMPTZ), `responded_at` (TIMESTAMPTZ NULL).
- **`V2__outbox.sql`**:
  - `outbox_events`: `id` (UUID PK), `aggregate_type` (VARCHAR), `aggregate_id` (UUID), `event_type` (VARCHAR), `payload` (JSONB/TEXT), `status` (VARCHAR: `PENDING`, `PUBLISHED`), `created_at` (TIMESTAMPTZ), `published_at` (TIMESTAMPTZ NULL).
- **`V3__indexes.sql`**:
  - Indexes on `claims(status, market)`, `claims(assigned_officer_id, status)`, `claims(claimant_id)`, `outbox_events(status, created_at)`.
- **`V4__dev_seed.sql`** (Only executed when `spring.flyway.locations` includes `classpath:db/migration` + dev profile config):
  - Seed 2 Claimants (`alice@example.com`, `bob@example.com`), 2 Officers (`officer1@chx.com`, `officer2@chx.com`), 1 Manager (`manager@chx.com`) — all with BCrypt password `Password123!`.
  - Seed active/expired Motor and Property policies for Alice and Bob across `SG` and `MY` markets.
  - Seed 4–5 claims in varied statuses (`SUBMITTED`, `UNDER_REVIEW`, `INFO_REQUESTED`, `APPROVED`) for immediate demo readiness.

### 4.3 Domain State Machine (`ClaimStatus`)
Allowed transitions:
- `SUBMITTED` -> `UNDER_REVIEW` (Officer picks up claim)
- `UNDER_REVIEW` -> `INFO_REQUESTED` (Officer requests more info)
- `INFO_REQUESTED` -> `UNDER_REVIEW` (Claimant submits info response)
- `UNDER_REVIEW` -> `APPROVED` (Officer approves with `approvedAmount <= coverageLimit`)
- `UNDER_REVIEW` -> `REJECTED` (Officer rejects with `rejectionReason`)
- `APPROVED` -> `SETTLED` (Officer marks payment settled)

Any other transition MUST throw `IllegalTransitionException` mapped to HTTP `409 Conflict` via RFC 7807 `ProblemDetail`.

### 4.4 Policy Validation (`PolicyValidator`)
Before accepting a new claim (`POST /claims`), validate:
1. Policy exists and belongs to the authenticated `CLAIMANT`.
2. Policy `status == ACTIVE`.
3. `incidentDate` is between `validFrom` and `validTo` (inclusive) and not in the future.
4. `claimType` matches the policy's `claimType`.
5. `claimedAmount > 0` and `<= coverageLimit`.
Failures return HTTP `422 Unprocessable Entity` or `400 Bad Request` with `ProblemDetail`.

### 4.5 Concurrency & Optimistic Locking
- `Claim` uses JPA `@Version`.
- When an officer calls `POST /officer/claims/{id}/pick-up`, if another officer already claimed it (`status != SUBMITTED` or `OptimisticLockingFailureException`), `GlobalExceptionHandler` catches it and returns HTTP `409 Conflict` (`ProblemDetail` title: `"Claim Already Assigned"`).

### 4.6 Transactional Outbox
- `OutboxRecorder.record(...)` writes to `outbox_events` inside the active business transaction.
- `OutboxPublisher` runs via `@Scheduled(fixedDelay = 1000)` using a native PostgreSQL query with `FOR UPDATE SKIP LOCKED LIMIT 50` to fetch `PENDING` events, sends them via `KafkaTemplate` to topic `claim-events` (key = `aggregateId.toString()`), and marks them `PUBLISHED`.

### 4.7 REST API Endpoints & Security
Stateless JWT Bearer authentication (`Authorization: Bearer <token>`).
All error responses MUST use Spring's `ProblemDetail` (`application/problem+json`).

| Method & Path | Allowed Roles | Description |
|---|---|---|
| `POST /auth/login` | Public | Returns `{ token, user: { id, email, fullName, role } }` |
| `POST /claims` | `CLAIMANT` | Submit new claim. Emits `ClaimSubmitted`. |
| `GET /claims` | `CLAIMANT` | List caller's own claims (`PageResponse`). |
| `GET /claims/{id}` | `CLAIMANT`, `CLAIMS_OFFICER`, `CLAIMS_MANAGER` | Claim detail + status history + info requests (Claimant restricted to own claims). |
| `POST /claims/{id}/info-responses` | `CLAIMANT` | Respond to open `InfoRequest`. Transitions `INFO_REQUESTED` -> `UNDER_REVIEW`. Emits `InfoProvided`. |
| `GET /officer/queue` | `CLAIMS_OFFICER`, `CLAIMS_MANAGER` | Paginated `SUBMITTED` claims. Filters: `market`, `claimType`, `minAgeDays`. |
| `POST /officer/claims/{id}/pick-up` | `CLAIMS_OFFICER` | Assigns claim to caller, transitions `SUBMITTED` -> `UNDER_REVIEW`. Emits `ClaimAssigned`. |
| `GET /officer/claims/mine` | `CLAIMS_OFFICER` | List claims assigned to caller. |
| `POST /officer/claims/{id}/info-requests` | `CLAIMS_OFFICER` | Creates `InfoRequest`, transitions `UNDER_REVIEW` -> `INFO_REQUESTED`. Emits `InfoRequested`. |
| `PUT /officer/claims/{id}/reserve` | `CLAIMS_OFFICER` | Updates `reserveAmount` on assigned claim. |
| `POST /officer/claims/{id}/approve` | `CLAIMS_OFFICER` | Sets `approvedAmount`, transitions `UNDER_REVIEW` -> `APPROVED`. Emits `ClaimDecided`. |
| `POST /officer/claims/{id}/reject` | `CLAIMS_OFFICER` | Sets `rejectionReason`, transitions `UNDER_REVIEW` -> `REJECTED`. Emits `ClaimDecided`. |
| `POST /officer/claims/{id}/settle` | `CLAIMS_OFFICER` | Transitions `APPROVED` -> `SETTLED`. Emits `ClaimDecided` (or `ClaimSettled`). |
| `GET /reports/workload` | `CLAIMS_MANAGER` | Open claims count grouped by assigned officer. |
| `GET /reports/exposure` | `CLAIMS_MANAGER` | Sum of `claimedAmount`, `reserveAmount`, `approvedAmount` grouped by `market` and `claimType`. |
| `GET /reports/status-summary` | `CLAIMS_MANAGER` | Claim counts grouped by `status`. |
| `GET /reports/performance` | `CLAIMS_MANAGER` | Average resolution time (hours) and SLA breach count (> 72h). |

---

## 5. Backend Specification: `notification-service`

### 5.1 Database (`notifications`)
- **`V1__notifications.sql`**:
  - `processed_events`: `event_id` (UUID PK), `event_type` (VARCHAR), `processed_at` (TIMESTAMPTZ).
  - `sent_notifications`: `id` (UUID PK), `event_id` (UUID), `recipient_email` (VARCHAR), `subject` (VARCHAR), `body` (TEXT), `sent_at` (TIMESTAMPTZ).

### 5.2 Kafka Consumer & Idempotency
- Listens to `claim-events` (`groupId = "notification-service"`).
- Uses `DefaultErrorHandler` with `FixedBackOff(1000L, 3)` publishing failed messages to `claim-events.DLT`.
- Deserializes JSON using a tolerant `EventEnvelope` record (`@JsonIgnoreProperties(ignoreUnknown = true)`):
  ```json
  {
    "eventId": "uuid",
    "eventType": "ClaimSubmitted | ClaimAssigned | InfoRequested | InfoProvided | ClaimDecided",
    "occurredAt": "2026-11-20T14:00:00Z",
    "aggregateId": "uuid",
    "payload": {
      "claimNumber": "CHX-SG-000001",
      "claimantEmail": "alice@example.com",
      "claimantName": "Alice Tan",
      "status": "APPROVED",
      "details": "..."
    }
  }
  ```
- **Idempotency Flow (`@Transactional`):**
  1. Check `processedEventRepository.existsById(envelope.eventId())`. If true, log and ack immediately.
  2. Insert `ProcessedEvent` row.
  3. Render email subject/body via `MessageTemplates` and send via `JavaMailSender` to Mailpit (`SMTP_HOST:1025`).
  4. Persist audit row in `sent_notifications`.
- Expose optional `GET /notifications` endpoint returning recent `sent_notifications` for debugging/verification.

---

## 6. Frontend Specification: Angular (`frontend/`)

### 6.1 Architecture & Tooling
- **API Client Generation:** Use `ng-openapi-gen` configured in `scripts/generate-api.sh` reading from `http://localhost:8080/v3/api-docs` (or a saved `openapi.json`) into `src/app/api/`. Commit `src/app/api/` so Docker builds require zero backend runtime dependency.
- **State Management:** Angular Signals (`signal()`, `computed()`) local to feature components. Only `AuthService` maintains global application state (`currentUser`, `token`, `isAuthenticated`).
- **Styling:** Angular Material theme + clean enterprise layout in `ShellComponent` (top toolbar with user badge, market/role tag, logout button, and role-based side/top navigation).

### 6.2 Routing & Guards (`app.routes.ts`)
- `''` or `'login'` -> `LoginComponent` (includes quick-fill demo credential buttons for Alice, Officer 1, and Manager).
- `'claimant'` (lazy-loaded, guarded by `authGuard` + `roleGuard(['CLAIMANT'])`):
  - `''` -> `MyClaimsComponent` (Table with `StatusBadgeComponent`, highlights `INFO_REQUESTED` rows with an "Action Needed" badge).
  - `'new'` -> `SubmitClaimComponent` (Policy dropdown filtered to user's active policies; dynamic form fields switching between `MOTOR` [vehicleReg, driverLicense] and `PROPERTY` [propertyAddress, damageCategory]).
  - `':id'` -> `ClaimDetailComponent` (Claim metadata, `StatusTimelineComponent`, and inline reply form if an open `InfoRequest` exists).
- `'staff'` (lazy-loaded, guarded by `authGuard` + `roleGuard(['CLAIMS_OFFICER', 'CLAIMS_MANAGER'])`):
  - `'queue'` -> `QueueComponent` (Filter by Market/Type, "Pick Up" button handling 409 conflict toast cleanly).
  - `'my-work'` -> `MyWorkComponent` (Officer's assigned claims with age badge).
  - `'claims/:id'` -> `ClaimReviewComponent` (Full detail + timeline + action toolbar opening Material dialogs: `RequestInfoDialog`, `AssessDialog` [reserve], `DecisionDialog` [approve/reject/settle]).
  - `'dashboard'` (guarded by `roleGuard(['CLAIMS_MANAGER'])`) -> `DashboardComponent` (Summary KPI cards + Exposure by Market/Type table + Officer Workload table).

---

## 7. Verification & Testing Requirements
All tests must pass via `mvn test` (backend) and `npm test` (frontend):
1. `ClaimStatusTest`: Unit test verifying every valid and invalid status transition.
2. `ClaimLifecycleIT`: Integration test (Testcontainers Postgres + Kafka) executing Submit -> Pick Up -> Request Info -> Provide Info -> Approve -> Settle.
3. `ConcurrentPickUpIT`: Multi-threaded test (`ExecutorService` with `CountDownLatch`) proving two officers picking up the same claim simultaneously results in one `200 OK` and one `409 Conflict`.
4. `RoleAccessIT`: Verifies `CLAIMANT` cannot access `/officer/queue`, `/reports/*`, or another claimant's claim (`403 Forbidden`).
5. `OutboxPublisherIT`: Verifies DB transaction writes outbox row and scheduler publishes to Kafka topic `claim-events`.
6. `ExposureQueryIT`: Verifies reporting SQL aggregations accurately sum reserves and approved amounts.
7. `ArchitectureTest` (ArchUnit): Enforces the 4 package boundary rules in Section 4.1.
8. `ClaimEventListenerIT` (`notification-service`): Publishes duplicate `eventId` messages to Kafka and asserts exactly 1 email is sent (verified via GreenMail or mocked `JavaMailSender` + `processed_events` count = 1).
9. Frontend Specs: Unit tests for `roleGuard` and `StatusTimelineComponent`.

---

## 8. Documentation Deliverables (`docs/`)
- `README.md`: Architecture summary, prerequisites, `docker compose up` instructions, seeded user credentials table, and Mailpit URL (`http://localhost:8025`).
- `docs/architecture.md`: ASCII/Mermaid system diagram, service boundaries, sync REST vs async Kafka rationale.
- `docs/events.md`: Event schema specification and JSON examples for all 5 event types.
- `docs/decisions/001-two-services.md` through `004-scope-and-cuts.md`: Concise 1-page Architecture Decision Records.
- `docs/demo-script.md`: Step-by-step walkthrough script demonstrating end-to-end flow, optimistic lock conflict, and Mailpit email delivery.