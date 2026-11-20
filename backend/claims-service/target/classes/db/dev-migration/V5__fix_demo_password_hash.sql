-- Generate the development password with PostgreSQL's BCrypt implementation.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

UPDATE users
SET password_hash = crypt('Password123!', gen_salt('bf', 10))
WHERE email IN ('alice@example.com', 'bob@example.com', 'officer1@chx.com', 'officer2@chx.com', 'manager@chx.com');
