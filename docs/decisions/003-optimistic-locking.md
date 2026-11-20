# ADR 003: Optimistic locking for pickup

## Status
Accepted

## Decision
Use a JPA `@Version` column on claims and map a losing concurrent pickup to HTTP 409 Conflict.

## Rationale
The officer queue is contested work. Optimistic locking avoids a coarse application-wide lock while making the losing request explicit.

## Consequences
Clients refresh after a conflict. The domain transition also rejects pickup once the claim is no longer `SUBMITTED`.
