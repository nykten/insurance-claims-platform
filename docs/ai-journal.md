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

### 2026-11-20 23:57 Milestone 3 Implementation
- **Asked:** Implement Milestone 3 - Identity, Security, Assignment & Reporting
- **AI proposed:** Various additions and implementation
- **Challenged / overrode:** Tests were not running correctly
- **Why:** Missing import and wrong dependencies
- **Commit:** Not committed

### 00:10 Milestone 3 Test Success
- **Asked:** Verification for Milestone 3 changes and successful test run.
- **AI proposed:** Added identity application-layer separation through `AuthService`, corrected the controller-to-repository architecture violation, and confirmed all 3 backend tests passed with 0 failures and 0 errors.
- **Accepted:** new changes to the AuthService
- **Challenged / overrode:** Ran verification tests
- **Why:** Ensure verification passes
- **Commit:** All changes committed

### 2026-11-21 00:00 Milestone 4 outbox and notifications
- **Asked:** Implement the progress sections of Milestone 4, covering the transactional outbox, claim lifecycle events, notification-service, idempotency, email handling, tests, and event documentation.
- **AI proposed:** Added and wired the claims-service outbox, Kafka event publishing, notification-service consumer, tolerant event handling, duplicate-event protection, email audit persistence, JSONB mapping, unit tests, and `docs/events.md`. The user subsequently confirmed all tests passed.
- **Accepted:** Accepted implementation of services after verification tests passed
- **Challenged / overrode:** Challenged the duplication problem of the same class but it turns out to be a stale directory map detected by intelliJ
- **Why:** Verify everything works
- **Commit:** Accepted all commits after tests passed

### 2026-11-22 02:10 Milestone 5 Frontend building and spinning up docker
- **Asked:** Implement the progress sections of Milestone 5 and to build the frontend services. Verify with tests and docker builds
- **AI proposed:** Created the frontend and docker build instructions.
- **Accepted:** -
- **Challenged / overrode:** There is a persistent bug that returns Error 403 Forbidden upon trying to sign in and we couldnt exactly find out why despite multiple tests.
- **Personal reflection:** At this point I think the project has an issue with scope creep with how complex it is and I am beginning to lose my view and grip of the codebase. The fact that I couldnt debug the 403 error tells a lot, I shouldve given simpler deliverables and smaller scope so that a lot of additionals parts are not added for this assessment.

### 2026-11-22 03:28 Milestone 6 Build validation and smoke tests
- **Asked:** Proceed with the next steps and validate the docker build by running tests for the whole claims process and user roles.
- **AI proposed:** Fix previous bugs and rebuild docker image.
- **Accepted:** Accepted fixes and allowed smoke tests
- **Challenged / overrode:** Insisted on fixing the authentication bug and fixing routes in the frontend side.
- **Why:** Without the auth passing, the latter modules and pages could not be properly tested
- **Commit:** After all tests are verified and completed, commits are accepted

### 2026-11-22 03:42 Milestone 7 Documentation
- **Asked:** Plan out the documentation including how to set up, architecture of the system and provide demo scripts
- **AI proposed:** Provided all the above.
- **Accepted:** All except the README
- **Challenged / overrode:** Asked for additional detail for setup instructions from cloning to running the app.
- **Why:** Provideds more clarity on how to set up the project.
- **Commit:** All changes are committed after review.
- **Personal Reflection:** I'm glad that I'm able to complete this despite the hiccups and underprepared software setups before I started. I learnt first-hand how to run Docker and inspecting their error traces. By the end of this asssessment, I understand how important to plan out what's important and keeping the scope minimal when working with AI.
