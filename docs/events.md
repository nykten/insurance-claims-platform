# Claim events

The claims service writes these events to the transactional outbox and publishes them to Kafka topic `claim-events`. The notification service is a tolerant reader: unknown fields and unknown event types are ignored safely.

```json
{
  "eventId": "00000000-0000-0000-0000-000000000001",
  "eventType": "ClaimSubmitted",
  "version": 1,
  "occurredAt": "2026-11-21T00:00:00Z",
  "aggregateId": "00000000-0000-0000-0000-000000000002",
  "payload": {
    "claimNumber": "CHX-SG-100001",
    "claimantEmail": "alice@example.com",
    "claimantName": "Alice Tan",
    "status": "SUBMITTED",
    "details": "Claim submitted",
    "amount": 1250.00
  }
}
```

Supported `eventType` values are `ClaimSubmitted`, `ClaimAssigned`, `InfoRequested`, `InfoProvided`, and `ClaimDecided`. Every payload is self-contained and includes the claimant email and name; notification-service never calls claims-service.
