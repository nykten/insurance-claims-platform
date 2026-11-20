# Architecture

```text
Angular + nginx :4200 --> claims-service :8080 --> claims database
                                      |
                                      +--> Kafka claim-events --> notification-service :8081
                                                                        |
                                                notifications database +--> Mailpit :8025
```

`claims-service` is the source of truth for users, policies, claims, status history, information requests, reports, and outbox events. `notification-service` owns notification processing and delivery audit data. The services do not share a Java module or call each other synchronously. PostgreSQL is one local server with two logical databases: `claims` and `notifications`.

REST handles immediate commands and queries: login, claim submission, queue access, pickup, assessment, decisions, and reports. Kafka carries facts after the claims transaction commits, so email delivery is independent of the claim transaction.

Claim mutations, status history, and outbox rows are written together. A scheduled publisher sends pending outbox rows to `claim-events`; the notification consumer persists processed event IDs before recording email delivery, making duplicate events safe.

The claim lifecycle is:

```text
SUBMITTED -> UNDER_REVIEW -> INFO_REQUESTED -> UNDER_REVIEW
                         \-> APPROVED -> SETTLED
                         \-> REJECTED
```

The claim `@Version` column protects concurrent officer pickup. A losing pickup is returned as HTTP 409 Conflict. The frontend strips `/api` before nginx proxies requests to the claims API.
