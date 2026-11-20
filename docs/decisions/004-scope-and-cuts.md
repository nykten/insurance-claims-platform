# ADR 004: Scope and deliberate cuts

## Status
Accepted

## Decision
Deliver claimant tracking, officer assessment and decisions, manager operational reports, notification delivery, and Docker deployment. Payments, fraud scoring, a real policy administration system, registration, password reset, i18n, FX conversion, appeals, and reopening remain out of scope.

## Rationale
This demonstrates the core lifecycle and the important boundaries: authorization, domain transitions, concurrency, reporting, transactional events, and idempotent notifications.

## Consequences
Policy validation is a seeded-data stub. There is no payment integration, and future capabilities must preserve the two-service boundary and domain transition methods.
