ALTER TABLE `users`
ADD COLUMN `status_tmp` VARCHAR(20) DEFAULT 'ACTIVE';
UPDATE `users`
SET `status_tmp` = CASE
        WHEN `status` IN (1, '1') THEN 'ACTIVE'
        WHEN `status` IN (0, '0') THEN 'INACTIVE'
        ELSE CAST(`status` AS CHAR)
    END
WHERE `status_tmp` IS NULL
    OR `status_tmp` = '';
ALTER TABLE `users` DROP COLUMN `status`;
ALTER TABLE `users` CHANGE COLUMN `status_tmp` `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';