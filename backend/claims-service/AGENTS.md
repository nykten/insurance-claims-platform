# claims-service

Java 21, Spring Boot 3.x, Spring Data JPA, PostgreSQL, Flyway, Spring Kafka.

## Conventions
- Package-by-feature under `com.chx.claims`: identity, policy, claim, assignment,
  reporting, outbox, common. Inside a feature: api / application / domain / infra.
- DTOs are Java records. Entities never leave the `application` layer.
- Validation with Jakarta annotations on request records.
- Errors: throw domain exceptions; `GlobalExceptionHandler` maps them to RFC 7807
  `ProblemDetail`. Illegal transition = 409. Optimistic lock conflict = 409.
  Not yours / not found = 404 (do not leak existence). Wrong role = 403.
- Ownership: a claimant may only read their own claims. Check in `ClaimQueryService`.
- Concurrency: `Claim` has `@Version`. Pick-up relies on optimistic locking;
  catch `ObjectOptimisticLockingFailureException` and return 409.
- Outbox: `OutboxRecorder.record(...)` inside the same `@Transactional` method as
  the state change. The poller uses `FOR UPDATE SKIP LOCKED`.
- Do not use Spring Statemachine. Transitions are an enum plus a map.
- Exposure = sum of `COALESCE(reserve_amount, claimed_amount)` for claims not in
  SETTLED or REJECTED, grouped by market, type and currency.

## Testing
- Unit: `ClaimStatusTest` must cover every legal and illegal transition.
- Integration tests extend `AbstractIntegrationTest` (Testcontainers Postgres + Kafka).
- Use Awaitility for async assertions, never `Thread.sleep`.
- Every new endpoint needs: happy path, wrong-role (403), and a validation failure.
