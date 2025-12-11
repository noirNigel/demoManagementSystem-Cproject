-- 添加商品图片、描述和配方字段，确保前端提交的数据可以持久化
ALTER TABLE product
    ADD COLUMN IF NOT EXISTS image_url LONGTEXT NULL COMMENT '商品主图（可存储Base64或URL）';

ALTER TABLE product
    ADD COLUMN IF NOT EXISTS description TEXT NULL COMMENT '商品描述';

ALTER TABLE product
    ADD COLUMN IF NOT EXISTS recipe LONGTEXT NULL COMMENT '配方信息，JSON或文本存储';
