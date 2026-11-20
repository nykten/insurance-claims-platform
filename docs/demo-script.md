# Demo script

## Start

```powershell
docker compose up --build -d
docker compose ps
```

Open http://localhost:4200 and Mailpit at http://localhost:8025.

## Claimant flow

1. Sign in as `alice@example.com` with `Password123!`.
2. Submit a new motor claim against Alice's active SG policy, using a valid incident date and an amount below the coverage limit.
3. Confirm the claim appears as `SUBMITTED`.

## Officer flow

1. Sign in as `officer1@chx.com`.
2. Pick up the submitted claim from the queue.
3. Request additional information and confirm `INFO_REQUESTED`.
4. Sign in again as Alice, reply to the open request, and confirm `UNDER_REVIEW`.
5. Sign in as the officer, set a reserve if needed, approve within the policy limit, and settle the claim.

To demonstrate optimistic locking, submit pickup for the same submitted claim from two officer sessions at nearly the same time. One succeeds and the other receives HTTP 409.

## Notification and manager checks

Open Mailpit or query the notification audit:

```powershell
Invoke-RestMethod http://localhost:8081/notifications
```

Sign in as `manager@chx.com` and inspect status summary, workload, and exposure. Their API paths are `/reports/status-summary`, `/reports/workload`, and `/reports/exposure`.
