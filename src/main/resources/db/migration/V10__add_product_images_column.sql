-- Add carousel images column to store JSON array of image URLs/base64 strings
-- Uses information_schema checks so it works on MySQL 5.7+ where
-- `ADD COLUMN IF NOT EXISTS` is not available.

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product'
      AND COLUMN_NAME = 'images'
);

SET @ddl := IF(@column_exists = 0,
               'ALTER TABLE product ADD COLUMN images LONGTEXT NULL COMMENT ''商品轮播图（JSON 数组字符串）'';',
               'SELECT 1;');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
