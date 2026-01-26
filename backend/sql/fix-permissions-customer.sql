-- 修复客户管理权限不一致的问题
USE crm_db;

-- 1. 修正 "放入公海" 权限标识 (pool -> release)
UPDATE crm_permission SET permission_key = 'business:customer:release' WHERE permission_key = 'business:customer:pool';

-- 2. 修正 "领取客户" 权限标识 (claim -> acquire)
UPDATE crm_permission SET permission_key = 'business:customer:acquire' WHERE permission_key = 'business:customer:claim';

-- 3. 补全缺失的 "分配客户" 权限
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES (226, 22, '分配客户', 'business:customer:assign', 3, NULL, NULL, NULL, 6, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE permission_key = VALUES(permission_key);

-- 4. 补全缺失的 "查询/详情" 权限 (防止鉴权失败)
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES 
(227, 22, '查询客户列表', 'business:customer:list', 3, NULL, NULL, NULL, 0, 1, 1, NOW(), NOW()),
(228, 22, '查看客户详情', 'business:customer:detail', 3, NULL, NULL, NULL, 0, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE permission_key = VALUES(permission_key);

-- 5. 确保超级管理员 (role_id=1) 拥有上述所有权限
INSERT INTO crm_role_permission (role_id, permission_id)
SELECT 1, id FROM crm_permission
WHERE permission_key IN (
  'business:customer:release',
  'business:customer:acquire', 
  'business:customer:assign',
  'business:customer:list',
  'business:customer:detail'
)
ON DUPLICATE KEY UPDATE role_id = role_id;

SELECT '权限修复完成，请重新登录生效' as result;
