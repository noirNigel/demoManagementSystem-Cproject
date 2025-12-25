-- Add growth and available points columns to admin, if missing
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

-- Backfill new columns from existing points balance when present
UPDATE admin
SET level_points = points
WHERE level_points = 0 AND points IS NOT NULL;

UPDATE admin
SET available_points = points
WHERE available_points = 0 AND points IS NOT NULL;
