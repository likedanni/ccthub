-- V3__migrate_status_to_varchar.sql
-- Safely convert `users.status` from TINYINT to VARCHAR(20) when needed.
-- Mapping: 1 -> 'ACTIVE', 0 -> 'INACTIVE', other values cast to text.
DELIMITER $$ CREATE PROCEDURE migrate_status_to_varchar() BEGIN
DECLARE coltype VARCHAR(64) DEFAULT NULL;
DECLARE tmp_exists INT DEFAULT 0;
SELECT DATA_TYPE INTO coltype
FROM information_schema.columns
WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'status'
LIMIT 1;
IF coltype = 'tinyint' THEN
SELECT COUNT(*) INTO tmp_exists
FROM information_schema.columns
WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'status_tmp';
IF tmp_exists = 0 THEN
ALTER TABLE `users`
ADD COLUMN `status_tmp` VARCHAR(20) DEFAULT 'ACTIVE';
END IF;
UPDATE `users`
SET `status_tmp` = CASE
        WHEN `status` IN (1, '1') THEN 'ACTIVE'
        WHEN `status` IN (0, '0') THEN 'INACTIVE'
        ELSE CAST(`status` AS CHAR)
    END;
ALTER TABLE `users` DROP COLUMN `status`;
ALTER TABLE `users` CHANGE COLUMN `status_tmp` `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
END IF;
END $$ DELIMITER;
-- V3__migrate_status_to_varchar.sql
-- Convert `users.status` from TINYINT to VARCHAR(20) in a way Flyway can parse.
-- Mapping: 1 -> 'ACTIVE', 0 -> 'INACTIVE', other values cast to text.
-- Add temporary text column if it doesn't already exist (MySQL 8+ supports IF NOT EXISTS)
ALTER TABLE `users`
ADD COLUMN IF NOT EXISTS `status_tmp` VARCHAR(20) DEFAULT 'ACTIVE';
-- Populate mapping values into the temporary column for existing rows
UPDATE `users`
SET `status_tmp` = CASE
        WHEN `status` IN (1, '1') THEN 'ACTIVE'
        WHEN `status` IN (0, '0') THEN 'INACTIVE'
        ELSE CAST(`status` AS CHAR)
    END
WHERE `status_tmp` IS NULL
    OR `status_tmp` = '';
-- Drop the old column if present, then rename temporary column
ALTER TABLE `users` DROP COLUMN IF EXISTS `status`;
ALTER TABLE `users` CHANGE COLUMN `status_tmp` `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
-- End of migration V3