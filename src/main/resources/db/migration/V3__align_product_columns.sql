-- Align product media-related columns with the actual database schema
ALTER TABLE product
    ADD COLUMN IF NOT EXISTS category_path VARCHAR(255) AFTER category_id,
    ADD COLUMN IF NOT EXISTS recipe TEXT AFTER category_path,
    ADD COLUMN IF NOT EXISTS image VARCHAR(512) NULL COMMENT '商品主图' AFTER recipe,
    ADD COLUMN IF NOT EXISTS description TEXT NULL COMMENT '商品描述' AFTER image,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at,
    ADD COLUMN IF NOT EXISTS warning_threshold INT DEFAULT 10 AFTER stock;

-- Backfill the new image column from the previously used image_url column if present
UPDATE product SET image = image_url
WHERE image IS NULL AND image_url IS NOT NULL;
