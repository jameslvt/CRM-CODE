-- =============================================
-- 企业级 CRM 系统 - 补充初始化数据脚本
-- 创建时间: 2026-01-21
-- 说明: 补充缺失的权限数据
-- =============================================

USE crm_db;

-- =============================================
-- 1. 补充联系人管理菜单和按钮权限
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
-- 联系人管理菜单
(27, 2, '联系人管理', 'business:contact', 2, '/business/contact', 'business/contact/index', 'idcard', 7, 1, 1, NOW(3), NOW(3)),
-- 联系人管理按钮
(271, 27, '查询联系人', 'business:contact:list', 3, NULL, NULL, NULL, 1, 1, 1, NOW(3), NOW(3)),
(272, 27, '新增联系人', 'business:contact:add', 3, NULL, NULL, NULL, 2, 1, 1, NOW(3), NOW(3)),
(273, 27, '编辑联系人', 'business:contact:edit', 3, NULL, NULL, NULL, 3, 1, 1, NOW(3), NOW(3)),
(274, 27, '删除联系人', 'business:contact:delete', 3, NULL, NULL, NULL, 4, 1, 1, NOW(3), NOW(3)),
(275, 27, '设为主联系人', 'business:contact:primary', 3, NULL, NULL, NULL, 5, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 2. 补充客户标签管理菜单和按钮权限
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
-- 客户标签管理菜单
(28, 2, '客户标签', 'business:customer-tag', 2, '/business/customer-tag', 'business/customer-tag/index', 'tags', 8, 1, 1, NOW(3), NOW(3)),
-- 客户标签管理按钮
(281, 28, '查询标签', 'business:customer-tag:list', 3, NULL, NULL, NULL, 1, 1, 1, NOW(3), NOW(3)),
(282, 28, '新增标签', 'business:customer-tag:add', 3, NULL, NULL, NULL, 2, 1, 1, NOW(3), NOW(3)),
(283, 28, '编辑标签', 'business:customer-tag:edit', 3, NULL, NULL, NULL, 3, 1, 1, NOW(3), NOW(3)),
(284, 28, '删除标签', 'business:customer-tag:delete', 3, NULL, NULL, NULL, 4, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 3. 补充跟进记录管理菜单和按钮权限
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
-- 跟进记录管理菜单
(29, 2, '跟进记录', 'business:activity', 2, '/business/activity', 'business/activity/index', 'history', 9, 1, 1, NOW(3), NOW(3)),
-- 跟进记录管理按钮
(291, 29, '查询跟进记录', 'business:activity:list', 3, NULL, NULL, NULL, 1, 1, 1, NOW(3), NOW(3)),
(292, 29, '新增跟进记录', 'business:activity:add', 3, NULL, NULL, NULL, 2, 1, 1, NOW(3), NOW(3)),
(293, 29, '编辑跟进记录', 'business:activity:edit', 3, NULL, NULL, NULL, 3, 1, 1, NOW(3), NOW(3)),
(294, 29, '删除跟进记录', 'business:activity:delete', 3, NULL, NULL, NULL, 4, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 4. 补充客户管理缺失的按钮权限
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
(226, 22, '查询客户列表', 'business:customer:list', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3)),
(227, 22, '查看客户详情', 'business:customer:detail', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3)),
(228, 22, '查看360视图', 'business:customer:360', 3, NULL, NULL, NULL, 6, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 5. 补充商机管理缺失的按钮权限
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
(235, 23, '查询商机列表', 'business:opportunity:list', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3)),
(236, 23, '查看商机详情', 'business:opportunity:detail', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3)),
(237, 23, '推进阶段', 'business:opportunity:advance', 3, NULL, NULL, NULL, 5, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 6. 补充产品管理缺失的按钮权限
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
(245, 24, '查询产品列表', 'business:product:list', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3)),
(246, 24, '查看产品详情', 'business:product:detail', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 7. 补充合同管理缺失的按钮权限
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
(255, 25, '查询合同列表', 'business:contract:list', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3)),
(256, 25, '查看合同详情', 'business:contract:detail', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3)),
(257, 25, '提交审批', 'business:contract:submit', 3, NULL, NULL, NULL, 5, 1, 1, NOW(3), NOW(3)),
(258, 25, '审批合同', 'business:contract:approve', 3, NULL, NULL, NULL, 6, 1, 1, NOW(3), NOW(3)),
(259, 25, '完成合同', 'business:contract:complete', 3, NULL, NULL, NULL, 7, 1, 1, NOW(3), NOW(3)),
(260, 25, '终止合同', 'business:contract:terminate', 3, NULL, NULL, NULL, 8, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 8. 补充回款管理缺失的按钮权限
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
(265, 26, '查询回款计划', 'business:payment:list', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3)),
(266, 26, '查看回款详情', 'business:payment:detail', 3, NULL, NULL, NULL, 0, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 9. 补充 AI 功能权限（可选模块）
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
-- AI 功能目录
(4, 0, 'AI 助手', 'ai', 1, '/ai', NULL, 'robot', 50, 1, 1, NOW(3), NOW(3)),
(41, 4, 'AI 对话', 'ai:chat', 2, '/ai/chat', 'ai/chat/index', 'message', 1, 1, 1, NOW(3), NOW(3)),
-- AI 功能按钮
(411, 41, 'AI 对话', 'ai:chat:use', 3, NULL, NULL, NULL, 1, 1, 1, NOW(3), NOW(3)),
(412, 41, '智能创建', 'ai:smart-create', 3, NULL, NULL, NULL, 2, 1, 1, NOW(3), NOW(3)),
(413, 41, '智能查重', 'ai:duplicate-check', 3, NULL, NULL, NULL, 3, 1, 1, NOW(3), NOW(3)),
(414, 41, '自然语言查询', 'ai:nl-query', 3, NULL, NULL, NULL, 4, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 10. 补充公海池菜单
-- =============================================
INSERT INTO crm_permission (id, parent_id, name, permission_key, type, path, component, icon, sort, visible, status, create_time, update_time)
VALUES
(30, 2, '公海池', 'business:pool', 2, '/business/pool', 'business/customer/pool', 'global', 10, 1, 1, NOW(3), NOW(3)),
(301, 30, '查看公海池', 'business:pool:list', 3, NULL, NULL, NULL, 1, 1, 1, NOW(3), NOW(3)),
(302, 30, '领取客户', 'business:pool:claim', 3, NULL, NULL, NULL, 2, 1, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 11. 重新分配超级管理员所有权限
-- =============================================
DELETE FROM crm_role_permission WHERE role_id = 1;
INSERT INTO crm_role_permission (role_id, permission_id)
SELECT 1, id FROM crm_permission;

-- =============================================
-- 12. 更新销售经理权限（添加新增的权限）
-- =============================================
INSERT INTO crm_role_permission (role_id, permission_id)
SELECT 2, id FROM crm_permission
WHERE permission_key LIKE 'business:%'
   OR permission_key LIKE 'dashboard:%'
   OR permission_key IN ('business', 'dashboard')
ON DUPLICATE KEY UPDATE role_id = role_id;

-- =============================================
-- 13. 更新销售人员权限
-- =============================================
INSERT INTO crm_role_permission (role_id, permission_id)
SELECT 3, id FROM crm_permission
WHERE permission_key IN (
    'business',
    'business:lead', 'business:lead:list', 'business:lead:detail', 'business:lead:add', 'business:lead:edit', 'business:lead:convert',
    'business:customer', 'business:customer:list', 'business:customer:detail', 'business:customer:add', 'business:customer:edit', 'business:customer:360',
    'business:contact', 'business:contact:list', 'business:contact:add', 'business:contact:edit', 'business:contact:primary',
    'business:opportunity', 'business:opportunity:list', 'business:opportunity:detail', 'business:opportunity:add', 'business:opportunity:edit', 'business:opportunity:advance',
    'business:product', 'business:product:list', 'business:product:detail',
    'business:activity', 'business:activity:list', 'business:activity:add', 'business:activity:edit',
    'business:pool', 'business:pool:list', 'business:pool:claim',
    'dashboard', 'dashboard:sales'
)
ON DUPLICATE KEY UPDATE role_id = role_id;

-- =============================================
-- 14. 更新财务人员权限
-- =============================================
INSERT INTO crm_role_permission (role_id, permission_id)
SELECT 4, id FROM crm_permission
WHERE permission_key IN (
    'business',
    'business:customer', 'business:customer:list', 'business:customer:detail',
    'business:contract', 'business:contract:list', 'business:contract:detail', 'business:contract:add', 'business:contract:edit', 'business:contract:upload', 'business:contract:submit', 'business:contract:approve', 'business:contract:complete',
    'business:payment', 'business:payment:list', 'business:payment:detail', 'business:payment:add', 'business:payment:edit', 'business:payment:record',
    'dashboard', 'dashboard:sales', 'dashboard:sales:export'
)
ON DUPLICATE KEY UPDATE role_id = role_id;

-- =============================================
-- 15. 补充字典数据 - 付款方式
-- =============================================
INSERT INTO crm_dict_type (id, name, type, status, remark, create_time, update_time)
VALUES (11, '付款方式', 'payment_method', 1, '付款方式字典', NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE update_time = NOW(3);

INSERT INTO crm_dict_data (id, dict_type, label, value, sort, status, create_time, update_time)
VALUES
(100, 'payment_method', '银行转账', 'bank_transfer', 1, 1, NOW(3), NOW(3)),
(101, 'payment_method', '支票', 'check', 2, 1, NOW(3), NOW(3)),
(102, 'payment_method', '现金', 'cash', 3, 1, NOW(3), NOW(3)),
(103, 'payment_method', '支付宝', 'alipay', 4, 1, NOW(3), NOW(3)),
(104, 'payment_method', '微信支付', 'wechat_pay', 5, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE update_time = NOW(3);

-- =============================================
-- 16. 补充字典数据 - 性别
-- =============================================
INSERT INTO crm_dict_type (id, name, type, status, remark, create_time, update_time)
VALUES (12, '性别', 'gender', 1, '性别字典', NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE update_time = NOW(3);

INSERT INTO crm_dict_data (id, dict_type, label, value, sort, status, create_time, update_time)
VALUES
(110, 'gender', '男', '1', 1, 1, NOW(3), NOW(3)),
(111, 'gender', '女', '2', 2, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE update_time = NOW(3);

-- =============================================
-- 17. 补充字典数据 - 客户状态
-- =============================================
INSERT INTO crm_dict_type (id, name, type, status, remark, create_time, update_time)
VALUES (13, '客户状态', 'customer_status', 1, '客户状态字典', NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE update_time = NOW(3);

INSERT INTO crm_dict_data (id, dict_type, label, value, sort, status, create_time, update_time)
VALUES
(120, 'customer_status', '正常', '1', 1, 1, NOW(3), NOW(3)),
(121, 'customer_status', '公海', '2', 2, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE update_time = NOW(3);

-- =============================================
-- 显示补充结果
-- =============================================
SELECT '补充数据初始化完成！' AS '状态';
SELECT COUNT(*) AS '权限总数' FROM crm_permission;
SELECT COUNT(*) AS '字典类型数' FROM crm_dict_type;
SELECT COUNT(*) AS '字典数据数' FROM crm_dict_data;
