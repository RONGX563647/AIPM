-- Migration: remove RBAC related tables (role, permission, role_permission, user_role)
-- WARNING: This will permanently drop RBAC schema and data. Apply to your Postgres DB only if you intend to remove RBAC entirely.

DROP TABLE IF EXISTS role_permission CASCADE;
DROP TABLE IF EXISTS role CASCADE;
DROP TABLE IF EXISTS permission CASCADE;
DROP TABLE IF EXISTS user_role CASCADE;

-- Optional: remove any leftover seed data related to roles from sys_user (if you stored role ids there)
-- ALTER TABLE sys_user DROP COLUMN IF EXISTS role_id;