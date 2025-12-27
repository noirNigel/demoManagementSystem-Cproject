-- Normalize points_used column to prevent null inserts on legacy schemas
UPDATE orders SET points_used = 0 WHERE points_used IS NULL;
ALTER TABLE orders MODIFY COLUMN points_used INT NOT NULL DEFAULT 0 COMMENT '下单抵扣的积分';
