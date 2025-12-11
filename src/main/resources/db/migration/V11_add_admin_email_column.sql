-- Add email column for admins with a safe check for MySQL 5.7+
-- Default value matches registration fallback requirement

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'admin'
      AND COLUMN_NAME = 'email'
);

SET @ddl := IF(
    @column_exists = 0,
    'ALTER TABLE admin ADD COLUMN email VARCHAR(255) NOT NULL DEFAULT ''xxx@xxx.com'' AFTER password;',
    'SELECT 1;'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Ensure existing rows have a value if column already existed without data
UPDATE admin SET email = 'xxx@xxx.com' WHERE email IS NULL OR email = '';
