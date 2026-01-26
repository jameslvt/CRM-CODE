-- =============================================
-- 部门表结构优化迁移脚本
-- 添加 code 和 ancestors 字段
-- 创建时间: 2026-01-25
-- =============================================

-- 1. 添加 code 字段（部门编码）
ALTER TABLE `crm_department` 
ADD COLUMN `code` VARCHAR(50) NULL COMMENT '部门编码' AFTER `name`;

-- 2. 添加 ancestors 字段（祖先路径）
ALTER TABLE `crm_department` 
ADD COLUMN `ancestors` VARCHAR(500) NULL COMMENT '祖先路径,逗号分隔' AFTER `parent_id`;

-- 3. 为现有数据生成默认 code（使用 DEPT_ + id）
UPDATE `crm_department` SET `code` = CONCAT('DEPT_', id) WHERE `code` IS NULL;

-- 4. 初始化 ancestors 字段（根据 parent_id 递归生成）
-- 对于顶级部门 (parent_id = 0)
UPDATE `crm_department` SET `ancestors` = '0' WHERE `parent_id` = 0;

-- 对于一级子部门
UPDATE `crm_department` d1
SET d1.`ancestors` = CONCAT('0,', d1.`parent_id`)
WHERE d1.`parent_id` > 0 
AND EXISTS (SELECT 1 FROM (SELECT id FROM `crm_department` WHERE parent_id = 0) t WHERE t.id = d1.`parent_id`);

-- 5. 添加 code 字段唯一索引
ALTER TABLE `crm_department` ADD UNIQUE KEY `uk_code` (`code`);

-- 6. 添加 ancestors 字段索引（用于数据权限查询）
ALTER TABLE `crm_department` ADD KEY `idx_ancestors` (`ancestors`);

-- =============================================
-- 脚本执行完成
-- =============================================
