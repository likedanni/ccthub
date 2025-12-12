-- Add created_at and updated_at columns if missing (works on MySQL versions that don't support
-- "ADD COLUMN IF NOT EXISTS" by checking information_schema and performing ALTER TABLE only when needed)

DELIMITER $$
CREATE PROCEDURE add_timestamps_if_missing()
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'created_at'
  ) THEN
    ALTER TABLE `users`
      ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'updated_at'
  ) THEN
    ALTER TABLE `users`
      ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update timestamp';
  END IF;
END$$

DELIMITER ;

CALL add_timestamps_if_missing();
DROP PROCEDURE IF EXISTS add_timestamps_if_missing;
