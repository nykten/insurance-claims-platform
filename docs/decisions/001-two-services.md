# ADR 001: Two deployable services

## Status
Accepted

## Decision
Keep exactly two backend deployables: `claims-service` and `notification-service`, with separate logical PostgreSQL databases and no shared Java events module.

## Rationale
Claims owns the business transaction and lifecycle. Notification delivery has separate retry and operational concerns, so it is isolated behind Kafka.

## Consequences
Each service defines its own event DTOs and uses tolerant reading. Notification data needed downstream must be included in the event payload.
