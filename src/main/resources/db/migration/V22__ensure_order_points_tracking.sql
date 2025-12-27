-- Ensure orders captures points discount and earned points for ongoing accrual

-- points_discount_amount
SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'orders' AND column_name = 'points_discount_amount'
        ),
        'SELECT 1',
        "ALTER TABLE orders ADD COLUMN points_discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '积分抵扣金额' AFTER discount_amount"
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

-- earned_points
SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'orders' AND column_name = 'earned_points'
        ),
        'SELECT 1',
        "ALTER TABLE orders ADD COLUMN earned_points INT NOT NULL DEFAULT 0 COMMENT '本单获得的积分' AFTER points_used"
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

-- backfill nulls on existing rows
UPDATE orders SET points_discount_amount = 0 WHERE points_discount_amount IS NULL;
UPDATE orders SET earned_points = 0 WHERE earned_points IS NULL;
