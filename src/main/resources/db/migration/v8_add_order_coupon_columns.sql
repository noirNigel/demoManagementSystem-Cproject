ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS goods_amount DECIMAL(12,2) DEFAULT 0 AFTER total_amount,
    ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(12,2) DEFAULT 0 AFTER goods_amount,
    ADD COLUMN IF NOT EXISTS coupon_id BIGINT NULL AFTER discount_amount,
    ADD COLUMN IF NOT EXISTS user_coupon_id BIGINT NULL AFTER coupon_id,
    ADD COLUMN IF NOT EXISTS coupon_discount_amount DECIMAL(12,2) DEFAULT 0 AFTER user_coupon_id;
