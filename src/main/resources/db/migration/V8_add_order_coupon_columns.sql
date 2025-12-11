-- Add missing coupon-related columns on orders without relying on MySQL 8+ syntax
-- Each block checks for the column before attempting to add it so the migration
-- remains idempotent on older MySQL versions.

-- goods_amount
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND COLUMN_NAME = 'goods_amount'
);
SET @ddl := IF(@column_exists = 0,
               'ALTER TABLE orders ADD COLUMN goods_amount DECIMAL(12,2) DEFAULT 0 AFTER total_amount;',
               'SELECT 1;');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- discount_amount
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND COLUMN_NAME = 'discount_amount'
);
SET @ddl := IF(@column_exists = 0,
               'ALTER TABLE orders ADD COLUMN discount_amount DECIMAL(12,2) DEFAULT 0 AFTER goods_amount;',
               'SELECT 1;');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- coupon_id
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND COLUMN_NAME = 'coupon_id'
);
SET @ddl := IF(@column_exists = 0,
               'ALTER TABLE orders ADD COLUMN coupon_id BIGINT NULL AFTER discount_amount;',
               'SELECT 1;');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- user_coupon_id
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND COLUMN_NAME = 'user_coupon_id'
);
SET @ddl := IF(@column_exists = 0,
               'ALTER TABLE orders ADD COLUMN user_coupon_id BIGINT NULL AFTER coupon_id;',
               'SELECT 1;');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- coupon_discount_amount
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND COLUMN_NAME = 'coupon_discount_amount'
);
SET @ddl := IF(@column_exists = 0,
               'ALTER TABLE orders ADD COLUMN coupon_discount_amount DECIMAL(12,2) DEFAULT 0 AFTER user_coupon_id;',
               'SELECT 1;');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
