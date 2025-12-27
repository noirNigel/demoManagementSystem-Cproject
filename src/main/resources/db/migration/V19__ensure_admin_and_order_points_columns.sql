-- Ensure admin point tracking columns exist even if earlier migrations were skipped
SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'admin' AND column_name = 'level_points'
        ),
        'SELECT 1',
        "ALTER TABLE admin ADD COLUMN level_points INT NOT NULL DEFAULT 0 COMMENT '成长积分(用于等级,只增不减)'"
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'admin' AND column_name = 'available_points'
        ),
        'SELECT 1',
        "ALTER TABLE admin ADD COLUMN available_points INT NOT NULL DEFAULT 0 COMMENT '可用积分(抵扣用,可减少)'"
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

-- Backfill the new columns from legacy points when they are still zero
SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'admin' AND column_name = 'level_points'
        ),
        'UPDATE admin SET level_points = points WHERE level_points = 0 AND points IS NOT NULL',
        'SELECT 1'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'admin' AND column_name = 'available_points'
        ),
        'UPDATE admin SET available_points = points WHERE available_points = 0 AND points IS NOT NULL',
        'SELECT 1'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;

-- Ensure orders table tracks used points for inserts
SET @stmt := (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'orders' AND column_name = 'points_used'
        ),
        'SELECT 1',
        'ALTER TABLE orders ADD COLUMN points_used INT NOT NULL DEFAULT 0 COMMENT "下单抵扣的积分"'
    )
);
PREPARE column_stmt FROM @stmt; EXECUTE column_stmt; DEALLOCATE PREPARE column_stmt;
