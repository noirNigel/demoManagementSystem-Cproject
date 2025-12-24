-- 添加商品图片、描述和配方字段，确保前端提交的数据可以持久化
-- 为兼容已有数据库，按列不存在时再新增，避免重复列错误

SET @product_has_image_url := (SELECT COUNT(*)
                              FROM information_schema.columns
                              WHERE table_schema = DATABASE()
                                AND table_name = 'product'
                                AND column_name = 'image_url');
SET @ddl_add_image_url := IF(@product_has_image_url = 0,
                             'ALTER TABLE product ADD COLUMN image_url LONGTEXT NULL COMMENT ''商品主图（可存储Base64或URL）''',
                             'SELECT 1');
PREPARE stmt FROM @ddl_add_image_url; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @product_has_description := (SELECT COUNT(*)
                                 FROM information_schema.columns
                                 WHERE table_schema = DATABASE()
                                   AND table_name = 'product'
                                   AND column_name = 'description');
SET @ddl_add_description := IF(@product_has_description = 0,
                               'ALTER TABLE product ADD COLUMN description TEXT NULL COMMENT ''商品描述''',
                               'SELECT 1');
PREPARE stmt FROM @ddl_add_description; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @product_has_recipe := (SELECT COUNT(*)
                            FROM information_schema.columns
                            WHERE table_schema = DATABASE()
                              AND table_name = 'product'
                              AND column_name = 'recipe');
SET @ddl_add_recipe := IF(@product_has_recipe = 0,
                          'ALTER TABLE product ADD COLUMN recipe LONGTEXT NULL COMMENT ''配方信息，JSON或文本存储''',
                          'SELECT 1');
PREPARE stmt FROM @ddl_add_recipe; EXECUTE stmt; DEALLOCATE PREPARE stmt;
