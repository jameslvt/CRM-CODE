-- =============================================
-- 企业级 CRM 系统 - 系统模块数据库表结构
-- 数据库版本: MySQL 5.7
-- 字符集: utf8mb4
-- 创建时间: 2026-01-20
-- =============================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 用户表 (crm_user)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_user` (
  `id` BIGINT NOT NULL COMMENT '主键 (雪花算法)',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码 (BCrypt 加密)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
  `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门 ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 2. 角色表 (crm_role)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_role` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_key` VARCHAR(50) NOT NULL COMMENT '角色标识',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `data_scope` TINYINT NOT NULL DEFAULT 1 COMMENT '数据权限: 1-全部, 2-本部门及下级, 3-本部门, 4-仅本人',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_name` (`role_name`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- =============================================
-- 3. 用户角色关联表 (crm_user_role)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_user_role` (
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- =============================================
-- 4. 权限表 (crm_permission)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_permission` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限 ID',
  `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `permission_key` VARCHAR(100) NOT NULL COMMENT '权限标识',
  `type` TINYINT NOT NULL COMMENT '类型: 1-目录, 2-菜单, 3-按钮',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
  `component` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
  `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见: 0-隐藏, 1-显示',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_key` (`permission_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- =============================================
-- 5. 角色权限关联表 (crm_role_permission)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_role_permission` (
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限 ID',
  PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- =============================================
-- 6. 部门表 (crm_department)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_department` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父部门 ID',
  `name` VARCHAR(50) NOT NULL COMMENT '部门名称',
  `leader_id` BIGINT DEFAULT NULL COMMENT '负责人 ID',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- =============================================
-- 7. 字典类型表 (crm_dict_type)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_dict_type` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `name` VARCHAR(100) NOT NULL COMMENT '字典名称',
  `type` VARCHAR(100) NOT NULL COMMENT '字典类型',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- =============================================
-- 8. 字典数据表 (crm_dict_data)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_dict_data` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
  `label` VARCHAR(100) NOT NULL COMMENT '字典标签',
  `value` VARCHAR(100) NOT NULL COMMENT '字典值',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- =============================================
-- 9. 操作日志表 (crm_operation_log)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_operation_log` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `module` VARCHAR(50) NOT NULL COMMENT '模块',
  `operation` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
  `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求 URL',
  `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
  `response_result` TEXT DEFAULT NULL COMMENT '响应结果',
  `user_id` BIGINT DEFAULT NULL COMMENT '操作用户 ID',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP 地址',
  `duration` BIGINT DEFAULT NULL COMMENT '耗时 (ms)',
  `status` TINYINT DEFAULT NULL COMMENT '状态: 0-失败, 1-成功',
  `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_module` (`module`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =============================================
-- 10. 商机阶段配置表 (crm_opportunity_stage)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_opportunity_stage` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `name` VARCHAR(50) NOT NULL COMMENT '阶段名称',
  `stage_key` VARCHAR(50) NOT NULL COMMENT '阶段标识',
  `probability` INT NOT NULL DEFAULT 0 COMMENT '默认赢单概率 (%)',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stage_key` (`stage_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商机阶段配置表';

-- =============================================
-- 初始化商机阶段配置数据
-- =============================================
INSERT INTO `crm_opportunity_stage` (`id`, `name`, `stage_key`, `probability`, `sort`, `status`, `remark`, `create_time`, `update_time`) VALUES
(1, '需求确认', 'requirement_confirm', 10, 1, 1, '客户需求确认阶段', NOW(3), NOW(3)),
(2, '方案报价', 'proposal_quote', 30, 2, 1, '方案设计与报价阶段', NOW(3), NOW(3)),
(3, '商务谈判', 'negotiation', 60, 3, 1, '商务条款谈判阶段', NOW(3), NOW(3)),
(4, '赢单', 'won', 100, 4, 1, '成功赢单', NOW(3), NOW(3)),
(5, '输单', 'lost', 0, 5, 1, '失败输单', NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE `update_time` = NOW(3);

-- =============================================
-- 初始化字典类型数据
-- =============================================
INSERT INTO `crm_dict_type` (`id`, `name`, `type`, `status`, `remark`, `create_time`, `update_time`) VALUES
(1, '用户状态', 'user_status', 1, '用户状态字典', NOW(3), NOW(3)),
(2, '线索来源', 'lead_source', 1, '线索来源字典', NOW(3), NOW(3)),
(3, '线索状态', 'lead_status', 1, '线索状态字典', NOW(3), NOW(3)),
(4, '客户级别', 'customer_level', 1, '客户级别字典', NOW(3), NOW(3)),
(5, '客户规模', 'customer_scale', 1, '客户规模字典', NOW(3), NOW(3)),
(6, '行业类型', 'industry_type', 1, '行业类型字典', NOW(3), NOW(3)),
(7, '商机阶段', 'opportunity_stage', 1, '商机阶段字典', NOW(3), NOW(3)),
(8, '合同状态', 'contract_status', 1, '合同状态字典', NOW(3), NOW(3)),
(9, '回款状态', 'payment_status', 1, '回款状态字典', NOW(3), NOW(3)),
(10, '跟进类型', 'activity_type', 1, '跟进类型字典', NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE `update_time` = NOW(3);

-- =============================================
-- 初始化字典数据
-- =============================================
INSERT INTO `crm_dict_data` (`id`, `dict_type`, `label`, `value`, `sort`, `status`, `create_time`, `update_time`) VALUES
-- 用户状态
(1, 'user_status', '禁用', '0', 1, 1, NOW(3), NOW(3)),
(2, 'user_status', '正常', '1', 2, 1, NOW(3), NOW(3)),

-- 线索来源
(10, 'lead_source', '官网', 'website', 1, 1, NOW(3), NOW(3)),
(11, 'lead_source', '广告', 'advertisement', 2, 1, NOW(3), NOW(3)),
(12, 'lead_source', '转介绍', 'referral', 3, 1, NOW(3), NOW(3)),
(13, 'lead_source', '展会', 'exhibition', 4, 1, NOW(3), NOW(3)),
(14, 'lead_source', '电话营销', 'telemarketing', 5, 1, NOW(3), NOW(3)),
(15, 'lead_source', '其他', 'other', 6, 1, NOW(3), NOW(3)),

-- 线索状态
(20, 'lead_status', '新建', '1', 1, 1, NOW(3), NOW(3)),
(21, 'lead_status', '跟进中', '2', 2, 1, NOW(3), NOW(3)),
(22, 'lead_status', '已转化', '3', 3, 1, NOW(3), NOW(3)),
(23, 'lead_status', '已关闭', '4', 4, 1, NOW(3), NOW(3)),

-- 客户级别
(30, 'customer_level', 'A级 (重要客户)', 'A', 1, 1, NOW(3), NOW(3)),
(31, 'customer_level', 'B级 (普通客户)', 'B', 2, 1, NOW(3), NOW(3)),
(32, 'customer_level', 'C级 (潜在客户)', 'C', 3, 1, NOW(3), NOW(3)),
(33, 'customer_level', 'D级 (低价值客户)', 'D', 4, 1, NOW(3), NOW(3)),

-- 客户规模
(40, 'customer_scale', '小型', 'small', 1, 1, NOW(3), NOW(3)),
(41, 'customer_scale', '中型', 'medium', 2, 1, NOW(3), NOW(3)),
(42, 'customer_scale', '大型', 'large', 3, 1, NOW(3), NOW(3)),
(43, 'customer_scale', '集团', 'group', 4, 1, NOW(3), NOW(3)),

-- 行业类型
(50, 'industry_type', '互联网', 'internet', 1, 1, NOW(3), NOW(3)),
(51, 'industry_type', '金融', 'finance', 2, 1, NOW(3), NOW(3)),
(52, 'industry_type', '制造业', 'manufacturing', 3, 1, NOW(3), NOW(3)),
(53, 'industry_type', '零售', 'retail', 4, 1, NOW(3), NOW(3)),
(54, 'industry_type', '教育', 'education', 5, 1, NOW(3), NOW(3)),
(55, 'industry_type', '医疗', 'healthcare', 6, 1, NOW(3), NOW(3)),
(56, 'industry_type', '房地产', 'real_estate', 7, 1, NOW(3), NOW(3)),
(57, 'industry_type', '其他', 'other', 8, 1, NOW(3), NOW(3)),

-- 商机阶段
(60, 'opportunity_stage', '需求确认', 'requirement_confirm', 1, 1, NOW(3), NOW(3)),
(61, 'opportunity_stage', '方案报价', 'proposal_quote', 2, 1, NOW(3), NOW(3)),
(62, 'opportunity_stage', '商务谈判', 'negotiation', 3, 1, NOW(3), NOW(3)),
(63, 'opportunity_stage', '赢单', 'won', 4, 1, NOW(3), NOW(3)),
(64, 'opportunity_stage', '输单', 'lost', 5, 1, NOW(3), NOW(3)),

-- 合同状态
(70, 'contract_status', '草稿', '1', 1, 1, NOW(3), NOW(3)),
(71, 'contract_status', '审批中', '2', 2, 1, NOW(3), NOW(3)),
(72, 'contract_status', '执行中', '3', 3, 1, NOW(3), NOW(3)),
(73, 'contract_status', '已完成', '4', 4, 1, NOW(3), NOW(3)),
(74, 'contract_status', '已终止', '5', 5, 1, NOW(3), NOW(3)),

-- 回款状态
(80, 'payment_status', '待回款', '1', 1, 1, NOW(3), NOW(3)),
(81, 'payment_status', '部分回款', '2', 2, 1, NOW(3), NOW(3)),
(82, 'payment_status', '已回款', '3', 3, 1, NOW(3), NOW(3)),
(83, 'payment_status', '逾期', '4', 4, 1, NOW(3), NOW(3)),

-- 跟进类型
(90, 'activity_type', '电话', 'phone', 1, 1, NOW(3), NOW(3)),
(91, 'activity_type', '拜访', 'visit', 2, 1, NOW(3), NOW(3)),
(92, 'activity_type', '邮件', 'email', 3, 1, NOW(3), NOW(3)),
(93, 'activity_type', '会议', 'meeting', 4, 1, NOW(3), NOW(3)),
(94, 'activity_type', '其他', 'other', 5, 1, NOW(3), NOW(3))
ON DUPLICATE KEY UPDATE `update_time` = NOW(3);

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 脚本执行完成
-- =============================================
