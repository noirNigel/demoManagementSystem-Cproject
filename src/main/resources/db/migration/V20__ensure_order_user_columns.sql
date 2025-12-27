-- Ensure orders table has user association columns for point accrual logic
SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'orders' AND column_name = 'user_id'
        ),
        'SELECT 1',
        "ALTER TABLE orders ADD COLUMN user_id BIGINT NULL COMMENT '用户ID' AFTER user_coupon_id"
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.statistics
            WHERE table_schema = DATABASE() AND table_name = 'orders' AND index_name = 'idx_orders_user_id'
        ),
        'SELECT 1',
        'ALTER TABLE orders ADD INDEX idx_orders_user_id (user_id)'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'orders' AND column_name = 'user_openid'
        ),
        'SELECT 1',
        "ALTER TABLE orders ADD COLUMN user_openid VARCHAR(128) NULL COMMENT '小程序openid' AFTER user_id"
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;
