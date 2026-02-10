# Changelog

## Unreleased

### Removed
- RBAC role/permission system removed (entities, mappers and role tables deleted). All users are currently granted the same role set (USER, ADMIN, SUPER_ADMIN) for development convenience.

### Notes
- The RBAC tables (`role`, `permission`, `role_permission`, `user_role`) were dropped from the database by applying a migration script. If you require a rollback, restore from backup prior to the migration.
- This change reduces complexity for testing and early-stage development but is **not** suitable for production; reintroduce a proper RBAC implementation before deploying to production environments.
