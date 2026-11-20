# Milestone 2 Interim Documentation

## Initially planned

Milestone 2 was planned to establish the shared `common` foundation, policy validation, the package-by-feature claim domain, the claim state machine, application services, initial claimant/officer controllers, and `ClaimStatusTest`. The intended rules were RFC 7807 errors, domain-owned transitions, transactional application services, and no direct controller-to-repository access.

## Actually changed

- Added the Spring Boot application entry point and UTC clock configuration.
- Added Jackson unknown-field tolerance, OpenAPI metadata, pagination, and basic `ProblemDetail` exception handling.
- Added `Policy`, `PolicyRepository`, `PolicyValidator`, `ClaimType`, and `Market`.
- Added `ClaimStatus` with the specified transition matrix.
- Added `Claim`, `ClaimStatusHistory`, and `InfoRequest` entities with domain behavior for pickup, information requests/responses, approval, rejection, and settlement.
- Added `ClaimCommandService`, `ClaimQueryService`, claim number generation, repository access, request DTOs, and initial REST controllers.
- Added `ClaimStatusTest` covering valid transitions and all invalid source/target pairs.

## Known follow-up work

Identity/security and role enforcement remain Milestone 3 work. Outbox recording is intentionally not wired yet because it belongs to Milestone 4. The current controllers use a temporary `X-User-Id` header until JWT authentication is implemented. Full Maven verification remains pending an environment with JDK 21 and Maven.

## Milestone 3 changes

- Added bearer authentication filtering and current-user access support.
- Added performance/SLA reporting through a read-only JDBC query.
- Added a performance reporting service.

## Milestone 5 planned changes

Milestone 5 is planned to complete the Angular frontend with standalone components, Signals, Angular Material styling, authentication/role guards, generated API integration, claimant workflows, staff workflows, manager dashboard views, frontend tests, and Docker/nginx verification.

## Milestone 5 implemented changes

- Added Angular workspace and TypeScript configuration with Angular Material dependencies.
- Added signal-based login/session state with JWT persistence, demo-user quick-fill buttons, logout, and an authenticated workspace shell.
- Added role-aware navigation for claimant, officer, and manager views.
- Added initial claims list, claim submission form, officer queue, and manager dashboard views using Angular `HttpClient`.
- Added responsive enterprise styling, status badges, action-needed highlighting, empty states, and KPI cards.
- Added reusable auth state, role guard, problem-detail interceptor, claims API service, claim models, status timeline, and claimant claim-detail components under `src/app`.
- Added staff queue, claim-review action panel, manager dashboard, frontend smoke specs, and an OpenAPI client generation script.
- Added `ng-openapi-gen` tooling and frontend run/build/test instructions.

## Milestone 5 verification

Completed verification with Node.js/npm available:

- `npm install` completed successfully after correcting `ng-openapi-gen` to the published `1.1.0` release.
- `npm test` completed successfully with headless Chrome/Karma.
- `npm run build` completed successfully.
- Docker frontend image built and served through nginx on port `4200`.

Historical note: the earlier Docker login `403` was resolved in Milestone 6 by permitting `/error` for RFC 7807 responses and adding the development BCrypt password migration. Authenticated end-to-end verification is now covered by the Milestone 6 smoke flow.

## Milestone 6 development update

- Added the development-only V6 seed migration with additional active policies and claims across `SUBMITTED`, `UNDER_REVIEW`, `INFO_REQUESTED`, `APPROVED`, `REJECTED`, and `SETTLED` statuses.
- Added representative claim status histories and an open information request so claimant, officer, and manager demonstrations have richer data.
- Login authentication is now returning `401` rather than the misleading security `403`; the dev password migration is in place and the next verification step is the full Docker smoke flow.

## Milestone 6 planned changes

Milestone 6 covers full-stack Docker verification, service readiness, claimant/officer lifecycle smoke testing, Kafka notification delivery, Mailpit verification, and runtime log review.

## Milestone 6 implemented changes

- Added Docker healthchecks for claims-service, notification-service, and frontend.
- Bound the frontend healthcheck to `127.0.0.1` so Alpine `wget` does not resolve `localhost` to an address nginx is not listening on.
- Updated frontend startup ordering to wait for both backend services to become healthy.
- Added `scripts/smoke-test.ps1` for readiness checks, claimant login/claim retrieval, officer login/queue retrieval, and notification inspection.
- Expanded development seed data so the smoke flow has claims across multiple lifecycle states and markets.
- Added explicit Hibernate JSON mapping for the outbox `payload` column so PostgreSQL receives `jsonb` values.
- Corrected the bidirectional `Claim`/`ClaimStatusHistory` mapping so every history row writes its non-null `claim_id`.
- Made claim-number generation restart-safe by initializing from the persisted maximum claim sequence.
- Initialized lazy claim collections before mutation responses are serialized, preventing detached-entity response failures.
- Fixed the smoke fixture date so host and Docker/Postgres clock differences around midnight do not invalidate policy coverage.

## Milestone 6 verification

Verified in the Docker environment:

- `docker compose up --build -d` completed successfully and claims-service became healthy.
- `scripts/smoke-test.ps1` passed the complete lifecycle: login, retrieve claims, submit, pick up, request information, provide information, approve, and notification persistence.
- The final smoke output returned 10 claimant claims, created a new claim, returned 5 queue items, completed the lifecycle, and observed 13 notifications.
- Docker healthchecks for claims-service, notification-service, frontend, Mailpit, and Postgres passed.
- Mailpit's HTTP API confirmed 13 delivered messages; its browser UI was not inspected directly.
