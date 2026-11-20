---
name: add-flyway-migration
description: Add a database schema change to claims-service or notification-service using Flyway. Use for any table, column or index change.
---

1. Look at the highest existing `V<n>__*.sql` in the service's `db/migration` folder
   and create `V<n+1>__<short_description>.sql`. Never edit an existing migration.
2. Use `NUMERIC(19,4)` for money, `TIMESTAMPTZ` for times, `UUID` or `BIGINT` ids
   consistent with existing tables, and `JSONB` for type-specific incident details.
3. Add indexes for any new filter or join used by the queue or reports.
4. Keep seed data in the dev-only migration, never in schema migrations.
5. Update the JPA entity and confirm the app boots against a fresh Postgres
   (Testcontainers integration test is enough).