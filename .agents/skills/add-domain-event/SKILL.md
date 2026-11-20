---
name: add-domain-event
description: Add a new Kafka event published via the outbox and handled by notification-service. Use when a state change must notify another party.
---

1. Define the payload record in `claims-service/.../outbox/events/`. Wrap it in
   `EventEnvelope(eventId, type, version, occurredAt, payload)`.
2. Include everything the consumer needs, including claimant email and name and the
   claim number. The consumer must not call back into claims-service.
3. Record it with `OutboxRecorder` in the SAME transaction as the state change.
   Use the claim id as the Kafka message key.
4. In notification-service, add a handler case in `NotificationService` and a message
   template. Unknown event types must still be skipped safely.
5. Test: duplicate delivery of the same `eventId` results in exactly one email.
6. Document the event with an example JSON in `docs/events.md`.