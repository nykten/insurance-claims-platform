# CHX Claims Platform

Motor and property claims platform for CHX Insurance APAC (6 markets).
Claimants report and track claims; claims staff pick up, assess and decide them.

## Layout
- `backend/claims-service`: Spring Boot modular monolith (all core domain logic, REST API, Postgres)
- `backend/notification-service`: Kafka consumer that sends emails; own database
- `frontend/`: Angular SPA used by both claimants and staff
- `docs/`: architecture, ADRs, event catalogue, AI journal

## Commands
- Start dependencies only: `docker compose --profile infra up -d`
- Start everything: `docker compose up --build`
- Backend tests: `cd backend && ./mvnw -pl claims-service verify`
- Frontend: `cd frontend && npm ci && npm test && npm run build`
- Regenerate API client: `cd frontend && ./scripts/generate-api.sh`

## Architecture rules (do not break)
1. Two deployables only. Do not add services.
2. REST for commands and queries; Kafka only for facts that already happened.
   Never use Kafka for request/response.
3. Every claim state change goes through `Claim` transition methods. The allowed
   transitions live in `ClaimStatus`. Never set `status` directly.
4. A state change, its `claim_status_history` row and its outbox event are written
   in ONE transaction. Never publish to Kafka directly from a service method.
5. Consumers must be idempotent (dedupe on `eventId`).
6. Package-by-feature. Controllers call application services, never repositories.
   `reporting` is read-only.
7. Money is `BigDecimal` / `NUMERIC(19,4)` with an explicit currency. Never `double`.
8. Use an injected `Clock`. Never call `Instant.now()` in domain code.

## Working agreement
- Plan first for anything touching more than 3 files: reply with a short plan
  and wait for approval before editing.
- Make small, focused changes that can be committed individually.
- Add or update tests in the same change as the code. Run them before saying done.
- Do not add dependencies, change the architecture, or rename packages without asking.
- Use any relevant skills (SKILL.md) related to the tasks.
- Never edit a Flyway migration that has already been committed; add a new one.
- Never edit `frontend/src/app/api/` by hand (generated).
- Do not edit `docs/ai-journal.md`. The human owns it (see `log-ai-journal` skill).
- If a requirement is ambiguous, state your assumption explicitly and ask.
- Say what you did NOT do or could not verify.

## Out of scope (deliberately cut)
Payments, fraud scoring, a real policy system (stubbed), registration and password
reset, i18n, FX conversion, appeals/reopening. Do not build these.

You are building the **CHX Claims Platform** from scratch in the current workspace.

Read `SPEC.md` and `plan.md` in the repository root thoroughly before writing any code. Those two files are the authoritative specification and execution roadmap.

# Execution Instructions

1. **Follow `plan.md` strictly milestone by milestone (Milestone 1 through Milestone 7).** 
Complete all files and tests for a milestone and verify that the build/tests pass before moving to the next milestone.

2. **Adhere strictly to `SPEC.md`:**
   - Maintain the exact monorepo directory layout, package-by-feature structure, and endpoint paths.
   - Enforce all architectural boundaries in `claims-service` (controllers only call application services; `reporting` is read-only and never touches `claim.domain` mutators; external packages never call `ClaimRepository` directly) and verify them with an ArchUnit test.
   - Keep `claims-service` and `notification-service` completely decoupled: separate Postgres databases (`claims` and `notifications`), no shared events Maven module, tolerant JSON deserialization, and self-contained event payloads containing `claimantEmail` and `claimantName`.
   - Use RFC 7807 `ProblemDetail` for all backend error responses, including HTTP `409 Conflict` on optimistic locking failures during concurrent claim pick-ups.
   - Build the Angular frontend using standalone components, Angular Signals, Angular Material, and a generated OpenAPI client in `frontend/src/app/api/`.
3. **Log progress:** As you complete architectural milestones or make implementation trade-offs, refer the $log-ai-journal skill to record concise entries in `docs/ai-journal.md`.