-- Ensure product text fields can hold long content such as rich descriptions or large recipe payloads
ALTER TABLE product
    MODIFY COLUMN recipe LONGTEXT NULL COMMENT '配方信息，JSON或文本存储',
    MODIFY COLUMN description LONGTEXT NULL COMMENT '商品描述';
