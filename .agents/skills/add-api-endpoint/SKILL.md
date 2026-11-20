---
name: add-api-endpoint
description: Add a REST endpoint to claims-service and expose it to the Angular client. Use when a new screen needs backend data or actions.
---

1. Request/response records in the feature's `api/dto`, with validation annotations
   and `@Schema` descriptions so the OpenAPI spec is readable.
2. Controller method calls an application service only. Add method-level role security.
3. Integration test (happy path, wrong role, validation error).
4. Start the backend, then run `frontend/scripts/generate-api.sh` and commit the
   regenerated `frontend/src/app/api/` changes in the same commit as the backend change.
5. Do not hand-write an Angular HTTP service for the endpoint; use the generated one.