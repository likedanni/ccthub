# Draft PR: DB migrations to reconcile host schema + JWT/test fixes

## Summary

This draft contains Flyway migrations and related fixes to allow `user-service` to run against the existing host MySQL database (`cct-hub`), plus test/JWT dependency corrections included in the same branch.

Key goals:
- Convert `users.status` from `TINYINT` to `VARCHAR(20)` (V3).
- Add safe defaults to host-only columns blocking JPA inserts: `phone_encrypted` (V4) and `nickname` (V5).
- Keep changes minimal and backwards-safe; a DB backup was taken before applying migrations.

## Files changed (migrations)
- `src/main/resources/db/migration/V3__migrate_status_to_varchar.sql`
  - Add `status_tmp` VARCHAR(20), map integer values (1 → 'ACTIVE', 0 → 'INACTIVE'), drop old `status`, rename `status_tmp` → `status`.
  - Plain SQL only (no DELIMITER / stored-proc fragments) for Flyway parsing and broad MySQL compatibility.
- `src/main/resources/db/migration/V4__phone_encrypted_default.sql`
  - `ALTER TABLE users MODIFY COLUMN phone_encrypted VARCHAR(128) NOT NULL DEFAULT '';`
- `src/main/resources/db/migration/V5__nickname_default.sql`
  - `ALTER TABLE users MODIFY COLUMN nickname VARCHAR(50) NOT NULL DEFAULT '';`

## Backup

Backup of host DB taken prior to applying V3:

`/Users/like/CCTHub/backups/cct-hub-before-v3-20251212125244.sql`

## Verification performed locally

- Ran full build & tests: `SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/cct-hub mvn -DskipTests=false verify`.
- Flyway applied migrations up to `v5` on the host DB during test startup.
- Integration tests: `UserControllerIntegrationTest` and `UserServiceTest` ran successfully. Total tests: 5, failures: 0.

Note: this environment reported MySQL 9.5; Flyway emitted a compatibility warning (Flyway tested against MySQL 8.0). Please validate on staging/production MySQL versions before applying in those environments.

## How to create the PR (local commands)

If you have GitHub CLI (`gh`) installed, you can create a draft PR with:

```bash
cd backend/user-service
git add PR_DRAFT.md
git commit -m "docs: add PR draft describing DB migrations"
git push --set-upstream origin ci/migration-jwt-test
gh pr create --title "fix(migrations): migrate users.status and add DB defaults" --body-file PR_DRAFT.md --draft
```

Or open a PR in the web UI after pushing the branch.

## Staging run / recommended steps before production

1. Restore DB backup to a staging database or clone production to a staging environment.
2. Run the service against staging with the staging DB URL to let Flyway migrate: e.g.

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://staging-db:3306/cct-hub \
SPRING_DATASOURCE_USERNAME=root \
SPRING_DATASOURCE_PASSWORD=12345678 \
mvn -DskipTests=false verify
```

3. Run the full test suite and smoke tests; validate data integrity for `users` table.
4. If any migration fails partially, restore backup and adjust SQL; do not run `repair` without ensuring schema consistency.

## Rollback plan

- Restore backup from `/Users/like/CCTHub/backups/cct-hub-before-v3-20251212125244.sql`.
- If migration runs fail mid-way in Flyway, restore DB and fix SQL, then re-apply.

## Notes / follow-ups

- Consider adding explicit schema migration tests or adding a CI job that runs migrations against a disposable MySQL instance matching production version.
- Optionally add a small post-migration verification script that asserts `users.status` is VARCHAR and a few sample rows map correctly.

-- End of PR draft
