# Implementation Plan: CHX Claims Platform

Execute the build sequentially across **7 milestones**. Do not skip ahead until each milestone builds cleanly and its verification checks pass.

---

### Milestone 1: Monorepo Foundation, Infrastructure & Parent Maven Setup
- [ ] Initialize directory tree (`infra/`, `docs/`, `backend/claims-service/`, `backend/notification-service/`, `frontend/`).
- [ ] Create `.env.example`, `infra/postgres/init.sql` (creating `claims` and `notifications` DBs), and `docker-compose.yml` with `infra` and default profiles (`postgres`, `kafka`, `mailpit`, `claims-service`, `notification-service`, `frontend`).
- [ ] Create `backend/pom.xml` (Java 21, Spring Boot 3.4.x BOM, Testcontainers, ArchUnit, Springdoc OpenAPI, Lombok/Records config) and module POMs for `claims-service` and `notification-service`.
- [ ] Configure `claims-service` `application.yml` & `application-dev.yml`, plus Flyway migrations `V1__core_schema.sql`, `V2__outbox.sql`, `V3__indexes.sql`, and `V4__dev_seed.sql`.
- [ ] **Verify:** Start `docker compose --profile infra up -d`, boot `ClaimsServiceApplication` with `dev` profile, confirm Flyway migrations V1–V4 apply cleanly to `claims` DB.

### Milestone 2: Core `claim` Domain & State Machine
- [ ] Implement `common` package (`ClockConfig`, `JacksonConfig`, `OpenApiConfig`, `PageResponse`, `GlobalExceptionHandler` with RFC 7807 `ProblemDetail`, custom exceptions).
- [ ] Implement `policy` package (`Policy`, `PolicyRepository`, `PolicyValidator`).
- [ ] Implement `claim.domain` (`Claim`, `ClaimStatus` transition matrix, `ClaimType`, `Market`, `ClaimStatusHistory`, `InfoRequest`, `IllegalTransitionException`).
- [ ] Write and run `ClaimStatusTest` covering all legal and illegal state transitions.
- [ ] Implement `ClaimNumberGenerator`, `ClaimCommandService`, `ClaimQueryService`, `ClaimantClaimController`, and `OfficerClaimController`.
- [ ] **Verify:** `mvn test -Dtest=ClaimStatusTest` passes.

### Milestone 3: Identity, Security, Assignment & Reporting
- [ ] Implement `identity` package (`User`, `Role`, `UserRepository`, `JwtService`, `SecurityConfig`, `CurrentUser`, `AuthController` for `POST /auth/login`).
- [ ] Implement `assignment` package (`ClaimQueueSpecifications`, `AssignmentService` with `@Version` optimistic lock handling -> `409 Conflict`, `QueueController`).
- [ ] Implement `reporting` package (`ReportingRepository` with read-only SQL projections, `DashboardService`, `DashboardController` for `/reports/*`).
- [ ] Write `ArchitectureTest` (ArchUnit) enforcing controller, reporting, and aggregate encapsulation rules.
- [ ] Write integration tests: `RoleAccessIT`, `ConcurrentPickUpIT`, and `ExposureQueryIT`.
- [ ] **Verify:** All unit, ArchUnit, and integration tests pass via `mvn test`.

### Milestone 4: Transactional Outbox & `notification-service`
- [ ] Implement `outbox` package in `claims-service` (`OutboxEvent`, `OutboxRepository` with `FOR UPDATE SKIP LOCKED`, `OutboxRecorder`, `OutboxPublisher` `@Scheduled` poller, event payload records including `claimantEmail` and `claimantName`).
- [ ] Wire `OutboxRecorder` into `ClaimCommandService` transitions (`ClaimSubmitted`, `ClaimAssigned`, `InfoRequested`, `InfoProvided`, `ClaimDecided`).
- [ ] Write `OutboxPublisherIT` and `ClaimLifecycleIT` in `claims-service`.
- [ ] Implement `notification-service`: Flyway `V1__notifications.sql`, `KafkaConsumerConfig` (with retry + `.DLT`), tolerant `EventEnvelope`, `ProcessedEventRepository` idempotency guard, `MessageTemplates`, `EmailSender` (SMTP -> Mailpit), `NotificationService`, and `GET /notifications`.
- [ ] Write `ClaimEventListenerIT` and `NotificationServiceTest` in `notification-service`.
- [ ] **Verify:** `mvn test` passes across both backend modules.

### Milestone 5: Angular Frontend Application
- [ ] Scaffold Angular workspace in `frontend/` with Angular Material, `proxy.conf.json`, `nginx.conf`, and multi-stage `Dockerfile`.
- [ ] Export OpenAPI spec from `claims-service` and run `scripts/generate-api.sh` (`ng-openapi-gen`) to populate `src/app/api/`.
- [ ] Implement `core/auth` (`AuthService` with signals, `authInterceptor`, `authGuard`, `roleGuard`), `core/error` (`errorInterceptor` mapping `ProblemDetail` to snackbars), and `core/layout` (`ShellComponent`).
- [ ] Implement `shared/ui` components (`StatusBadgeComponent`, `StatusTimelineComponent`, `MoneyPipe`, `EmptyStateComponent`, `ConfirmDialogComponent`).
- [ ] Implement features:
  - `auth/login` (with one-click demo user login helpers)
  - `claimant` (`my-claims`, `submit-claim` with dynamic Motor/Property fields, `claim-detail` with info-request response)
  - `staff` (`queue`, `my-work`, `claim-review` + dialogs for request-info, reserve, approve/reject/settle, and manager `dashboard`)
- [ ] Write unit tests for `roleGuard` and `StatusTimelineComponent`.
- [ ] **Verify:** `npm run build` and `npm test` succeed without errors.

### Milestone 6: Full-Stack Docker Verification
- [ ] Build and boot the entire stack via `docker compose up --build -d`.
- [ ] Execute an end-to-end smoke test via API/UI: login as Claimant -> submit claim -> login as Officer -> pick up claim -> request info -> reply as Claimant -> approve claim -> verify email delivery in Mailpit (`http://localhost:8025`).
- [ ] **Verify:** All containers healthy, zero runtime errors in container logs.

### Milestone 7: Documentation & Architecture Decision Records
- [ ] Write `README.md` with quick-start commands, architecture summary, and seeded credentials.
- [ ] Write `docs/architecture.md`, `docs/events.md`, `docs/demo-script.md`, and `docs/ai-journal.md`.
- [ ] Write ADRs `docs/decisions/001-two-services.md` through `004-scope-and-cuts.md`.
- [ ] **Verify:** Review all links, file paths, and commands in `README.md` against the actual codebase.