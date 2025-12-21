-- 修复 stockpile 表结构的 SQL 脚本
-- 执行此脚本前，请先备份数据库！

-- 1. 检查并添加缺失的列（如果不存在）
-- 检查 amount 列是否存在，如果不存在则添加
SET @dbname = DATABASE();
SET @tablename = "stockpile";
SET @columnname = "amount";
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  "SELECT 'Column amount already exists.'",
  CONCAT("ALTER TABLE ", @tablename, " ADD COLUMN ", @columnname, " INT NOT NULL DEFAULT 0 COMMENT '可卖库存';")
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 检查 frozen 列是否存在，如果不存在则添加
SET @columnname = "frozen";
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  "SELECT 'Column frozen already exists.'",
  CONCAT("ALTER TABLE ", @tablename, " ADD COLUMN ", @columnname, " INT NOT NULL DEFAULT 0 COMMENT '冻结库存';")
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 2. 如果上面的动态SQL不工作，可以使用下面的简单版本（需要手动检查列是否存在）
-- ALTER TABLE stockpile ADD COLUMN IF NOT EXISTS amount INT NOT NULL DEFAULT 0 COMMENT '可卖库存';
-- ALTER TABLE stockpile ADD COLUMN IF NOT EXISTS frozen INT NOT NULL DEFAULT 0 COMMENT '冻结库存';

-- 3. 删除冗余的 product_name 列（如果存在且不再需要）
-- ALTER TABLE stockpile DROP COLUMN IF EXISTS product_name;

-- 4. 确保 product_id 列存在且正确
-- ALTER TABLE stockpile MODIFY COLUMN product_id INT NOT NULL;

-- 5. 添加外键约束（如果还没有）
-- ALTER TABLE stockpile 
-- ADD CONSTRAINT fk_stockpile_product 
-- FOREIGN KEY (product_id) REFERENCES product(id) 
-- ON DELETE CASCADE;

-- 6. 为所有没有库存记录的商品创建默认库存记录
INSERT INTO stockpile (product_id, amount, frozen)
SELECT id, 0, 0
FROM product
WHERE id NOT IN (SELECT product_id FROM stockpile WHERE product_id IS NOT NULL);

-- 7. 清理孤立数据（删除商品已删除但库存记录还在的情况）
DELETE FROM stockpile WHERE product_id NOT IN (SELECT id FROM product);

