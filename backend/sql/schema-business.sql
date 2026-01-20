-- =============================================
-- 企业级 CRM 系统 - 业务模块数据库表结构
-- 功能: 001-enterprise-crm
-- 日期: 2026-01-20
-- 数据库: MySQL 5.7
-- 字符集: utf8mb4
-- =============================================

-- =============================================
-- 1. 线索表 (crm_lead)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_lead` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `name` VARCHAR(100) NOT NULL COMMENT '线索名称',
  `source` VARCHAR(50) DEFAULT NULL COMMENT '来源: 官网、广告、转介绍等',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `company` VARCHAR(200) DEFAULT NULL COMMENT '公司名称',
  `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
  `industry` VARCHAR(50) DEFAULT NULL COMMENT '行业',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '地址',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-新建, 2-跟进中, 3-已转化, 4-已关闭',
  `owner_id` BIGINT DEFAULT NULL COMMENT '负责人ID',
  `customer_id` BIGINT DEFAULT NULL COMMENT '转化后的客户ID',
  `remark` TEXT DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线索表';

-- =============================================
-- 2. 客户表 (crm_customer)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_customer` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `name` VARCHAR(200) NOT NULL COMMENT '客户名称',
  `short_name` VARCHAR(50) DEFAULT NULL COMMENT '简称',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '客户编码',
  `industry` VARCHAR(50) DEFAULT NULL COMMENT '行业',
  `scale` VARCHAR(50) DEFAULT NULL COMMENT '规模: 小型、中型、大型、集团',
  `source` VARCHAR(50) DEFAULT NULL COMMENT '来源',
  `level` VARCHAR(20) DEFAULT NULL COMMENT '级别: A、B、C、D',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
  `fax` VARCHAR(20) DEFAULT NULL COMMENT '传真',
  `website` VARCHAR(200) DEFAULT NULL COMMENT '网站',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '地址',
  `owner_id` BIGINT DEFAULT NULL COMMENT '负责人ID',
  `lead_id` BIGINT DEFAULT NULL COMMENT '来源线索ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 2-公海',
  `remark` TEXT DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_name` (`name`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- =============================================
-- 3. 客户标签表 (crm_customer_tag)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_customer_tag` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '标签颜色',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户标签表';

-- =============================================
-- 4. 客户标签关联表 (crm_customer_tag_relation)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_customer_tag_relation` (
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`customer_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户标签关联表';

-- =============================================
-- 5. 联系人表 (crm_contact)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_contact` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `gender` TINYINT DEFAULT NULL COMMENT '性别: 1-男, 2-女',
  `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
  `department` VARCHAR(50) DEFAULT NULL COMMENT '部门',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
  `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `wechat` VARCHAR(50) DEFAULT NULL COMMENT '微信',
  `is_primary` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主要联系人: 0-否, 1-是',
  `birthday` DATE DEFAULT NULL COMMENT '生日',
  `remark` TEXT DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人表';

-- =============================================
-- 6. 产品表 (crm_product)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_product` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
  `price` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `description` TEXT DEFAULT NULL COMMENT '描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-停用, 1-启用',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- =============================================
-- 7. 商机表 (crm_opportunity)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_opportunity` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `name` VARCHAR(200) NOT NULL COMMENT '商机名称',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `contact_id` BIGINT DEFAULT NULL COMMENT '联系人ID',
  `amount` DECIMAL(14,2) NOT NULL DEFAULT 0.00 COMMENT '预计金额',
  `stage` VARCHAR(50) NOT NULL COMMENT '阶段: 需求确认、方案报价、商务谈判、赢单、输单',
  `probability` INT DEFAULT 0 COMMENT '赢单概率(%)',
  `expected_date` DATE DEFAULT NULL COMMENT '预计成交日期',
  `owner_id` BIGINT DEFAULT NULL COMMENT '负责人ID',
  `source` VARCHAR(50) DEFAULT NULL COMMENT '商机来源',
  `remark` TEXT DEFAULT NULL COMMENT '备注',
  `close_reason` VARCHAR(500) DEFAULT NULL COMMENT '关闭原因(输单时)',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_stage` (`stage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商机表';

-- =============================================
-- 8. 商机产品关联表 (crm_opportunity_product)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_opportunity_product` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `opportunity_id` BIGINT NOT NULL COMMENT '商机ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `price` DECIMAL(12,2) NOT NULL COMMENT '单价',
  `discount` DECIMAL(5,2) DEFAULT 100.00 COMMENT '折扣(%)',
  `amount` DECIMAL(14,2) NOT NULL COMMENT '金额',
  PRIMARY KEY (`id`),
  KEY `idx_opportunity_id` (`opportunity_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商机产品关联表';

-- =============================================
-- 9. 合同表 (crm_contract)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_contract` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `contract_no` VARCHAR(50) NOT NULL COMMENT '合同编号',
  `name` VARCHAR(200) NOT NULL COMMENT '合同名称',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `opportunity_id` BIGINT DEFAULT NULL COMMENT '商机ID',
  `amount` DECIMAL(14,2) NOT NULL COMMENT '合同金额',
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `sign_date` DATE DEFAULT NULL COMMENT '签约日期',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-草稿, 2-审批中, 3-执行中, 4-已完成, 5-已终止',
  `owner_id` BIGINT DEFAULT NULL COMMENT '负责人ID',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '合同文件URL',
  `remark` TEXT DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_no` (`contract_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- =============================================
-- 10. 回款计划表 (crm_payment_plan)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_payment_plan` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `contract_id` BIGINT NOT NULL COMMENT '合同ID',
  `period` INT NOT NULL COMMENT '期数',
  `plan_amount` DECIMAL(14,2) NOT NULL COMMENT '计划金额',
  `plan_date` DATE NOT NULL COMMENT '计划回款日期',
  `actual_amount` DECIMAL(14,2) DEFAULT 0.00 COMMENT '实际回款金额',
  `actual_date` DATE DEFAULT NULL COMMENT '实际回款日期',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-待回款, 2-部分回款, 3-已回款, 4-逾期',
  `remark` TEXT DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `update_time` DATETIME(3) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回款计划表';

-- =============================================
-- 11. 回款记录表 (crm_payment_record)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_payment_record` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `plan_id` BIGINT NOT NULL COMMENT '回款计划ID',
  `amount` DECIMAL(14,2) NOT NULL COMMENT '回款金额',
  `payment_date` DATE NOT NULL COMMENT '回款日期',
  `payment_method` VARCHAR(50) DEFAULT NULL COMMENT '付款方式: 银行转账、支票、现金',
  `remark` TEXT DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回款记录表';

-- =============================================
-- 12. 跟进记录表 (crm_activity)
-- =============================================
CREATE TABLE IF NOT EXISTS `crm_activity` (
  `id` BIGINT NOT NULL COMMENT '主键',
  `type` VARCHAR(20) NOT NULL COMMENT '类型: 电话、拜访、邮件、会议、其他',
  `content` TEXT NOT NULL COMMENT '跟进内容',
  `target_type` VARCHAR(20) NOT NULL COMMENT '关联对象类型: lead、customer、opportunity',
  `target_id` BIGINT NOT NULL COMMENT '关联对象ID',
  `next_time` DATETIME DEFAULT NULL COMMENT '下次跟进时间',
  `create_time` DATETIME(3) NOT NULL COMMENT '创建时间',
  `create_by` BIGINT NOT NULL COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跟进记录表';

-- =============================================
-- 表结构创建完成
-- =============================================
