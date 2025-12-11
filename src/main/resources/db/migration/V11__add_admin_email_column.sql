
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

-- Re-check the column before updating existing rows to avoid failures if the
-- DDL branch above was skipped (e.g. variable reuse during manual execution).
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'admin'
      AND COLUMN_NAME = 'email'
);

SET @update_sql := IF(
    @column_exists > 0,
    'UPDATE admin SET email = ''xxx@xxx.com'' WHERE email IS NULL OR email = '''';',
    'SELECT 1;'
);
PREPARE stmt FROM @update_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
