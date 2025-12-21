-- 简单的修复脚本（如果上面的动态SQL不工作）
-- 请先检查你的 stockpile 表结构，然后执行相应的SQL

-- 方案1：如果表中有其他列名（如 quantity, stock_amount 等），需要先重命名或添加新列
-- 假设现有列名是 quantity，需要改为 amount：
-- ALTER TABLE stockpile CHANGE COLUMN quantity amount INT NOT NULL DEFAULT 0;

-- 方案2：如果表中完全没有这些列，直接添加：
ALTER TABLE stockpile ADD COLUMN amount INT NOT NULL DEFAULT 0 COMMENT '可卖库存';
ALTER TABLE stockpile ADD COLUMN frozen INT NOT NULL DEFAULT 0 COMMENT '冻结库存';

-- 方案3：如果 product_id 列不存在或类型不对，需要修复：
-- ALTER TABLE stockpile MODIFY COLUMN product_id INT NOT NULL;

-- 方案4：删除冗余字段（可选）
-- ALTER TABLE stockpile DROP COLUMN IF EXISTS product_name;

-- 方案5：为所有商品创建默认库存记录
INSERT INTO stockpile (product_id, amount, frozen)
SELECT id, 0, 0
FROM product
WHERE id NOT IN (SELECT product_id FROM stockpile WHERE product_id IS NOT NULL)
ON DUPLICATE KEY UPDATE amount = amount; -- 如果使用唯一约束

-- 方案6：清理孤立数据
DELETE FROM stockpile WHERE product_id NOT IN (SELECT id FROM product);

