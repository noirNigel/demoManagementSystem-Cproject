-- Allow larger product images (e.g., Base64 data URLs) by widening the image column
ALTER TABLE product
    MODIFY COLUMN image LONGTEXT NULL COMMENT '商品主图（支持Base64或长URL）';
