-- Ensure product.image can store compressed base64 payloads without truncation
ALTER TABLE product
    MODIFY COLUMN image LONGTEXT NULL COMMENT '商品主图（支持Base64或长URL）';
