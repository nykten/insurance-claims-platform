# notification-service

- Consumes `claim-events`. It must NEVER call claims-service over HTTP; everything it
  needs (claimant email, name, claim number) is in the event payload.
- Own database (`notifications`). Never read or write the claims database.
- Tolerant reader: ignore unknown JSON fields; unknown event types are logged and skipped.
- Idempotency: insert `eventId` into `processed_events` in the same transaction as the
  send record. A duplicate event must result in exactly one email.
- Failures: retry with backoff, then dead-letter topic. Never block the partition forever.
- Emails go to Mailpit (SMTP) locally.
