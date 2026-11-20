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
