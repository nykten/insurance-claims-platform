# ADR 002: Transactional outbox

## Status
Accepted

## Decision
Record domain events in `outbox_events` in the same transaction as the claim mutation. A scheduled publisher sends pending records to Kafka and marks them published.

## Rationale
This prevents a successful claim change from losing its notification event when Kafka is unavailable.

## Consequences
Publishing is asynchronous and retryable. Consumers must be idempotent, and the publisher uses row locking with `SKIP LOCKED`.
