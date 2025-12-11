CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'User ID',
  `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT 'Phone number',
  `password` VARCHAR(255) NOT NULL COMMENT 'Encrypted password',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'User status',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update timestamp',
  INDEX `idx_phone` (`phone`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User core information table';
