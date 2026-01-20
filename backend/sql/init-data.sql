-- =============================================
-- 企业级 CRM 系统 - 初始化数据脚本
-- 创建时间: 2026-01-20
-- 说明: 插入系统初始数据（管理员账号、基础角色、权限等）
-- =============================================

USE crm_db;

-- =============================================
-- 1. 插入管理员账号
-- =============================================
-- 密码: admin123 (BCrypt 加密后的值)
INSERT INTO crm_user (id, username, password, nickname, email, phone, dept_id, status, create_time, update_time, deleted)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@crm.com', '13800138000', 1, 1, NOW(3), NOW(3), 0)
ON DUPLICATE KEY UPDATE username = username;

-- =============================================
-- 2. 插入根部门
-- =============================================
INSERT INTO crm_department (id, parent_id, name, leader_id, sort, status, create_time, update_time, deleted)
VALUES (1, 0, '总公司', 1, 0, 1, NOW(3), NOW(3), 0)
ON DUPLICATE KEY UPDATE name = name;

-- =============================================
-- 3. 插入基础角色
-- =============================================
INSERT INTO crm_role (id, role_name, role_key, sort, data_scope, status, remark, create_time, update_time, deleted)
VALUES
(1, '超级管理员', 'admin', 1, 1, 1, '超级管理员，拥有所有权限', NOW(3), NOW(3), 0),
(2, '销售经理', 'sales_manager', 2, 2, 1, '销售经理，管理本部门及下级部门数据', NOW(3), NOW(3), 0),
(3, '销售人员', 'sales', 3, 4, 1, '销售人员，仅查看和管理自己的数据', NOW(3), NOW(3), 0),
(4, '财务人员', 'finance', 4, 1, 1, '财务人员，管理合同和回款', NOW(3), NOW(3), 0)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- =============================================
-- 4. 分配管理员角色
-- =============================================
INSERT INTO crm_user_role (user_id, role_id)
VALUES (1, 1)
ON DUPLICATE KEY UPDATE user_id = user_id;

-- =============================================
-- 5. 插入权限菜单（目录和菜单）
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
-- 系统管理目录
(1, 0, '系统管理', 'system', 1, '/system', NULL, 'setting', 100, 1, 1, NOW(3), NOW(3)),
(11, 1, '用户管理', 'system:user', 2, '/system/user', 'system/user/index', 'user', 1, 1, 1, NOW(3), NOW(3)),
(12, 1, '角色管理', 'system:role', 2, '/system/role', 'system/role/index', 'team', 2, 1, 1, NOW(3), NOW(3)),
(13, 1, '部门管理', 'system:dept', 2, '/system/department', 'system/department/index', 'apartment', 3, 1, 1, NOW(3), NOW(3)),
(14, 1, '权限管理', 'system:permission', 2, '/system/permission', 'system/permission/index', 'lock', 4, 1, 1, NOW(3), NOW(3)),
(15, 1, '字典管理', 'system:dict', 2, '/system/dict', 'system/dict/index', 'book', 5, 1, 1, NOW(3), NOW(3)),
(16, 1, '操作日志', 'system:log', 2, '/system/log', 'system/log/index', 'file-text', 6, 1, 1, NOW(3), NOW(3)),

-- 业务管理目录
(2, 0, '业务管理', 'business', 1, '/business', NULL, 'briefcase', 10, 1, 1, NOW(3), NOW(3)),
(21, 2, '线索管理', 'business:lead', 2, '/business/lead', 'business/lead/index', 'user-add', 1, 1, 1, NOW(3), NOW(3)),
(22, 2, '客户管理', 'business:customer', 2, '/business/customer', 'business/customer/index', 'contacts', 2, 1, 1, NOW(3), NOW(3)),
(23, 2, '商机管理', 'business:opportunity', 2, '/business/opportunity', 'business/opportunity/index', 'dollar', 3, 1, 1, NOW(3), NOW(3)),
(24, 2, '产品管理', 'business:product', 2, '/business/product', 'business/product/index', 'shopping', 4, 1, 1, NOW(3), NOW(3)),
(25, 2, '合同管理', 'business:contract', 2, '/business/contract', 'business/contract/index', 'file', 5, 1, 1, NOW(3), NOW(3)),
(26, 2, '回款管理', 'business:payment', 2, '/business/payment', 'business/payment/index', 'money-collect', 6, 1, 1, NOW(3), NOW(3)),

-- 数据分析目录
(3, 0, '数据分析', 'dashboard', 1, '/dashboard', NULL, 'bar-chart', 1, 1, 1, NOW(3), NOW(3)),
(31, 3, '销售仪表盘', 'dashboard:sales', 2, '/dashboard/sales', 'business/dashboard/index', 'dashboard', 1, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 6. 插入按钮权限
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
-- 用户管理按钮
(111, 11, '新增用户', 'system:user:add', 3, NULL, NULL, NULL, 1, 1, 1, NOW(3), NOW(3)),
(112, 11, '编辑用户', 'system:user:edit', 3, NULL, NULL, NULL, 2, 1, 1, NOW(3), NOW(3)),
(113, 11, '删除用户', 'system:user:delete', 3, NULL, NULL, NULL, 3, 1, 1, NOW(3), NOW(3)),
(114, 11, '重置密码', 'system:user:reset', 3, NULL, NULL, NULL, 4, 1, 1, NOW(3), NOW(3)),

-- 线索管理按钮
(211, 21, '新增线索', 'business:lead:add', 3, NULL, NULL, NULL, 1, 1, 1, NOW(3), NOW(3)),
(212, 21, '编辑线索', 'business:lead:edit', 3, NULL, NULL, NULL, 2, 1, 1, NOW(3), NOW(3)),
(213, 21, '删除线索', 'business:lead:delete', 3, NULL, NULL, NULL, 3, 1, 1, NOW(3), NOW(3)),
(214, 21, '转化线索', 'business:lead:convert', 3, NULL, NULL, NULL, 4, 1, 1, NOW(3), NOW(3)),
(215, 21, '导入线索', 'business:lead:import', 3, NULL, NULL, NULL, 5, 1, 1, NOW(3), NOW(3)),
(216, 21, '导出线索', 'business:lead:export', 3, NULL, NULL, NULL, 6, 1, 1, NOW(3), NOW(3)),

-- 客户管理按钮
(221, 22, '新增客户', 'business:customer:add', 3, NULL, NULL, NULL, 1, 1, 1, NOW(3), NOW(3)),
(222, 22, '编辑客户', 'business:customer:edit', 3, NULL, NULL, NULL, 2, 1, 1, NOW(3), NOW(3)),
(223, 22, '删除客户', 'business:customer:delete', 3, NULL, NULL, NULL, 3, 1, 1, NOW(3), NOW(3)),
(224, 22, '放入公海', 'business:customer:pool', 3, NULL, NULL, NULL, 4, 1, 1, NOW(3), NOW(3)),
(225, 22, '领取客户', 'business:customer:claim', 3, NULL, NULL, NULL, 5, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 7. 分配超级管理员所有权限
-- =============================================
INSERT INTO crm_role_permission (role_id, permission_id)
SELECT 1, id FROM crm_permission
ON DUPLICATE KEY UPDATE role_id = role_id;

-- =============================================
-- 8. 显示初始化结果
-- =============================================
SELECT '数据初始化完成！' AS '状态';
SELECT COUNT(*) AS '用户数' FROM crm_user WHERE deleted = 0;
SELECT COUNT(*) AS '角色数' FROM crm_role WHERE deleted = 0;
SELECT COUNT(*) AS '权限数' FROM crm_permission;
SELECT COUNT(*) AS '部门数' FROM crm_department WHERE deleted = 0;
