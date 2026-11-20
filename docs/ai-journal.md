# AI Working Journal

Start time: 21:40 GMT+8, 20/11/2026

## Initialisation @ 21:40
Initialise git repository on GitHub.

## 22:13 - planning
Discussed with AI tools to plan out system architecture and project structure after deciding tech stack: Java + Spring Boot, deployed on docker

### 2026-11-20 22:37 Milestone 1 Foundation
- **Asked:** Implement Milestone 1 of the CHX Claims Platform according to `SPEC.md` and `plan.md`.
- **AI proposed:** Created the monorepo structure, Docker Compose infrastructure, PostgreSQL initialization, Maven parent and service POMs, claims-service configuration, Flyway migrations V1–V4 with development seed data, Dockerfiles, frontend scaffold, and recorded progress. Docker Compose configuration passed validation; Maven verification was unavailable because Java and Maven were not installed.
- **Accepted:** basic project scaffold as outlined per `plan.md` and followed specs
- **Challenged / overrode:** Override AI journal and integrated new skill to format writing.
- **Why:** More structured journal writing. Project scaffold is accurate to specifications.
- **Commit:** All is committed after reviewing and editing AI journal.

### 2026-11-20 23:05 Milestone 2 Core Claim Domain
- **Asked:** Implement Milestone 2 using Spring Boot 3 guidance and document the planned versus actual changes.
- **AI proposed:** Added the initial claims-service application, common configuration and error handling, policy validation, claim entities and state machine, application services, initial claimant/officer controllers, transition tests, and `docs/interim-docs.md`.
- **Accepted:** Claims module and tests
- **Challenged / overrode:** Apparently my machine did not have Java (recently formatted device), so tests would not run. So I am installing Java just now.
- **Why:** Infrastructure to run code has not been set up properly
- **Commit:** Not committed

### 2026-11-20 23:46 Milestone 2 Verification
- **Asked:** Debug test problems successfully running the backend tests.
- **AI proposed:** Confirmed that `ClaimStatusTest` passed with 2 tests, 0 failures, 0 errors, and a successful Maven build.
- **Accepted:** First Claims module and tests
- **Challenged / overrode:** None
- **Why:** All test validated and infrastructure now runs
- **Commit:** Committed all new changes.