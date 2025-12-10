-- Add carousel images column to store JSON array of image URLs/base64 strings
ALTER TABLE product
    ADD COLUMN IF NOT EXISTS images LONGTEXT NULL COMMENT '商品轮播图（JSON 数组字符串）';
