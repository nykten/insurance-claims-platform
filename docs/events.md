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

The envelope is intentionally tolerant: consumers ignore unknown fields so the services can evolve independently.

Supported `eventType` values are `ClaimSubmitted`, `ClaimAssigned`, `InfoRequested`, `InfoProvided`, and `ClaimDecided`. Every payload is self-contained and includes the claimant email and name; notification-service never calls claims-service.

## Event catalog

| Event | Produced when | Typical status |
|---|---|---|
| `ClaimSubmitted` | A claimant submits a valid claim | `SUBMITTED` |
| `ClaimAssigned` | An officer picks up a claim | `UNDER_REVIEW` |
| `InfoRequested` | An officer asks for information | `INFO_REQUESTED` |
| `InfoProvided` | The claimant answers an open request | `UNDER_REVIEW` |
| `ClaimDecided` | An officer approves, rejects, or settles | `APPROVED`, `REJECTED`, or `SETTLED` |

All events use the same envelope and payload fields. `details` contains human-readable context and `amount` is used when relevant:

```json
{
  "eventType": "InfoRequested | InfoProvided | ClaimDecided | ClaimSubmitted",
  "payload": {
    "claimNumber": "CHX-SG-100001",
    "claimantEmail": "alice@example.com",
    "claimantName": "Alice Tan",
    "status": "INFO_REQUESTED | UNDER_REVIEW | APPROVED | REJECTED | SETTLED | SUBMITTED",
    "details": "Event-specific description",
    "amount": 1250.00
  }
}
```

The claims service publishes to Kafka topic `claim-events`. The notification consumer uses group ID `notification-service`; processed IDs are stored in `processed_events`, and email audit rows are stored in `sent_notifications`.
