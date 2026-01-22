-- =====================================================
-- CRM系统数据库索引优化脚本
-- 用于提升查询性能
-- 执行前请备份数据库
-- =====================================================

-- 操作日志表索引
CREATE INDEX IF NOT EXISTS idx_operation_log_user_id ON crm_operation_log(user_id);
CREATE INDEX IF NOT EXISTS idx_operation_log_module ON crm_operation_log(module);
CREATE INDEX IF NOT EXISTS idx_operation_log_status ON crm_operation_log(status);
CREATE INDEX IF NOT EXISTS idx_operation_log_create_time ON crm_operation_log(create_time);
CREATE INDEX IF NOT EXISTS idx_operation_log_composite ON crm_operation_log(module, operation, create_time);

-- 用户表索引优化
CREATE INDEX IF NOT EXISTS idx_user_status_deleted ON crm_user(status, deleted);
CREATE INDEX IF NOT EXISTS idx_user_dept_status ON crm_user(dept_id, status, deleted);

-- 线索表索引优化
CREATE INDEX IF NOT EXISTS idx_lead_owner_status ON crm_lead(owner_id, status, deleted);
CREATE INDEX IF NOT EXISTS idx_lead_source ON crm_lead(source);
CREATE INDEX IF NOT EXISTS idx_lead_create_time ON crm_lead(create_time);
CREATE INDEX IF NOT EXISTS idx_lead_composite ON crm_lead(owner_id, status, create_time);

-- 客户表索引优化
CREATE INDEX IF NOT EXISTS idx_customer_owner_status ON crm_customer(owner_id, status, deleted);
CREATE INDEX IF NOT EXISTS idx_customer_level ON crm_customer(level);
CREATE INDEX IF NOT EXISTS idx_customer_industry ON crm_customer(industry);
CREATE INDEX IF NOT EXISTS idx_customer_create_time ON crm_customer(create_time);
CREATE INDEX IF NOT EXISTS idx_customer_composite ON crm_customer(owner_id, status, level, deleted);

-- 联系人表索引优化
CREATE INDEX IF NOT EXISTS idx_contact_customer_primary ON crm_contact(customer_id, is_primary);

-- 商机表索引优化
CREATE INDEX IF NOT EXISTS idx_opportunity_owner_stage ON crm_opportunity(owner_id, stage, deleted);
CREATE INDEX IF NOT EXISTS idx_opportunity_customer ON crm_opportunity(customer_id);
CREATE INDEX IF NOT EXISTS idx_opportunity_expected_date ON crm_opportunity(expected_date);
CREATE INDEX IF NOT EXISTS idx_opportunity_create_time ON crm_opportunity(create_time);
CREATE INDEX IF NOT EXISTS idx_opportunity_composite ON crm_opportunity(owner_id, stage, expected_date, deleted);

-- 商机产品关联表索引
CREATE INDEX IF NOT EXISTS idx_opportunity_product_opp ON crm_opportunity_product(opportunity_id);
CREATE INDEX IF NOT EXISTS idx_opportunity_product_prod ON crm_opportunity_product(product_id);

-- 合同表索引优化
CREATE INDEX IF NOT EXISTS idx_contract_owner_status ON crm_contract(owner_id, status, deleted);
CREATE INDEX IF NOT EXISTS idx_contract_customer ON crm_contract(customer_id);
CREATE INDEX IF NOT EXISTS idx_contract_opportunity ON crm_contract(opportunity_id);
CREATE INDEX IF NOT EXISTS idx_contract_sign_date ON crm_contract(sign_date);
CREATE INDEX IF NOT EXISTS idx_contract_end_date ON crm_contract(end_date);
CREATE INDEX IF NOT EXISTS idx_contract_composite ON crm_contract(owner_id, status, sign_date, deleted);

-- 回款计划表索引优化
CREATE INDEX IF NOT EXISTS idx_payment_plan_contract ON crm_payment_plan(contract_id);
CREATE INDEX IF NOT EXISTS idx_payment_plan_status ON crm_payment_plan(status);
CREATE INDEX IF NOT EXISTS idx_payment_plan_date ON crm_payment_plan(plan_date);
CREATE INDEX IF NOT EXISTS idx_payment_plan_composite ON crm_payment_plan(contract_id, status, plan_date);

-- 回款记录表索引优化
CREATE INDEX IF NOT EXISTS idx_payment_record_plan ON crm_payment_record(plan_id);
CREATE INDEX IF NOT EXISTS idx_payment_record_date ON crm_payment_record(payment_date);

-- 跟进记录表索引优化
CREATE INDEX IF NOT EXISTS idx_activity_target ON crm_activity(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_activity_create_by ON crm_activity(create_by);
CREATE INDEX IF NOT EXISTS idx_activity_create_time ON crm_activity(create_time);
CREATE INDEX IF NOT EXISTS idx_activity_next_time ON crm_activity(next_time);

-- 产品表索引优化
CREATE INDEX IF NOT EXISTS idx_product_status ON crm_product(status, deleted);
CREATE INDEX IF NOT EXISTS idx_product_category ON crm_product(category);

-- 客户标签关联表索引
CREATE INDEX IF NOT EXISTS idx_customer_tag_relation_customer ON crm_customer_tag_relation(customer_id);
CREATE INDEX IF NOT EXISTS idx_customer_tag_relation_tag ON crm_customer_tag_relation(tag_id);

-- 部门表索引优化
CREATE INDEX IF NOT EXISTS idx_department_parent ON crm_department(parent_id);
CREATE INDEX IF NOT EXISTS idx_department_status ON crm_department(status, deleted);

-- 角色表索引优化
CREATE INDEX IF NOT EXISTS idx_role_status ON crm_role(status, deleted);

-- 字典数据表索引优化
CREATE INDEX IF NOT EXISTS idx_dict_data_type_status ON crm_dict_data(dict_type, status);

-- =====================================================
-- 分析表统计信息（MySQL 8.0+）
-- =====================================================
-- ANALYZE TABLE crm_user, crm_lead, crm_customer, crm_contact,
--               crm_opportunity, crm_contract, crm_payment_plan,
--               crm_payment_record, crm_activity, crm_product;
