---
name: add-claim-transition
description: Add or change a claim status transition or officer action (approve, reject, request info, etc.) in claims-service. Use for any change to the claim lifecycle.
---

Follow these steps in order. Stop and ask if any step conflicts with the existing code.

1. Update the allowed-transitions map in `ClaimStatus`. Never bypass it.
2. Add a behaviour method on `Claim` (e.g. `approve(amount, officerId, clock)`) that:
   validates preconditions, calls the shared transition check, appends a
   `ClaimStatusHistory` row, and updates fields. No setter for `status`.
3. Add a use case in `ClaimCommandService` annotated `@Transactional` that loads the
   claim, calls the behaviour method, and calls `OutboxRecorder.record(...)`.
4. Add the endpoint to `OfficerClaimController` with role `CLAIMS_OFFICER`; only the
   assigned officer may act on a claim.
5. Tests:
   - `ClaimStatusTest`: add the legal and illegal cases for the new transition.
   - Integration test: endpoint happy path, 409 on illegal transition, 403 wrong role.
6. If a new event is needed, use the `add-domain-event` skill.
7. Update the endpoint list in `docs/architecture.md`.

Report: files changed, tests run and their result, anything left undone.