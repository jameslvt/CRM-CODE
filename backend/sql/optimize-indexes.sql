-- =====================================================
-- CRM系统数据库索引优化脚本 (MySQL 5.7 兼容版)
-- 用于提升查询性能
-- 执行前请备份数据库
-- 更新时间: 2026-01-25
-- =====================================================

-- =====================================================
-- 使用存储过程安全创建索引（避免重复创建报错）
-- =====================================================
DROP PROCEDURE IF EXISTS safe_create_index;
DELIMITER //
CREATE PROCEDURE safe_create_index(
    IN p_table VARCHAR(64),
    IN p_index_name VARCHAR(64),
    IN p_columns VARCHAR(255)
)
BEGIN
    DECLARE index_exists INT DEFAULT 0;
    
    SELECT COUNT(*) INTO index_exists
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table
      AND INDEX_NAME = p_index_name;
    
    IF index_exists = 0 THEN
        SET @sql = CONCAT('CREATE INDEX ', p_index_name, ' ON ', p_table, '(', p_columns, ')');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //
DELIMITER ;

-- =====================================================
-- 1. 操作日志表索引
-- =====================================================
CALL safe_create_index('crm_operation_log', 'idx_operation_log_user_id', 'user_id');
CALL safe_create_index('crm_operation_log', 'idx_operation_log_module', 'module');
CALL safe_create_index('crm_operation_log', 'idx_operation_log_status', 'status');
CALL safe_create_index('crm_operation_log', 'idx_operation_log_create_time', 'create_time');
CALL safe_create_index('crm_operation_log', 'idx_operation_log_composite', 'module, operation, create_time');

-- =====================================================
-- 2. 用户表索引优化
-- =====================================================
CALL safe_create_index('crm_user', 'idx_user_status_deleted', 'status, deleted');
CALL safe_create_index('crm_user', 'idx_user_dept_status', 'dept_id, status, deleted');

-- =====================================================
-- 3. 线索表索引优化
-- =====================================================
CALL safe_create_index('crm_lead', 'idx_lead_source', 'source');
CALL safe_create_index('crm_lead', 'idx_lead_composite', 'owner_id, status, create_time, deleted');

-- =====================================================
-- 4. 客户表索引优化
-- =====================================================
CALL safe_create_index('crm_customer', 'idx_customer_industry', 'industry');
CALL safe_create_index('crm_customer', 'idx_customer_composite', 'owner_id, status, `level`, deleted');

-- =====================================================
-- 5. 联系人表索引优化
-- =====================================================
CALL safe_create_index('crm_contact', 'idx_contact_customer_primary', 'customer_id, is_primary');

-- =====================================================
-- 6. 商机表索引优化
-- =====================================================
CALL safe_create_index('crm_opportunity', 'idx_opportunity_expected_date', 'expected_date');
CALL safe_create_index('crm_opportunity', 'idx_opportunity_composite', 'owner_id, stage, expected_date, deleted');

-- =====================================================
-- 7. 商机产品关联表索引
-- =====================================================
-- 主键已包含 opportunity_id, product_id 的索引覆盖，无需额外添加

-- =====================================================
-- 8. 合同表索引优化
-- =====================================================
CALL safe_create_index('crm_contract', 'idx_contract_opportunity', 'opportunity_id');
CALL safe_create_index('crm_contract', 'idx_contract_sign_date', 'sign_date');
CALL safe_create_index('crm_contract', 'idx_contract_end_date', 'end_date');
CALL safe_create_index('crm_contract', 'idx_contract_composite', 'owner_id, status, sign_date, deleted');

-- =====================================================
-- 9. 回款计划表索引优化
-- =====================================================
CALL safe_create_index('crm_payment_plan', 'idx_payment_plan_composite', 'contract_id, status, plan_date');

-- =====================================================
-- 10. 回款记录表索引优化
-- =====================================================
CALL safe_create_index('crm_payment_record', 'idx_payment_record_plan', 'plan_id');
CALL safe_create_index('crm_payment_record', 'idx_payment_record_date', 'payment_date');

-- =====================================================
-- 11. 跟进记录表索引优化
-- =====================================================
CALL safe_create_index('crm_activity', 'idx_activity_create_by', 'create_by');
CALL safe_create_index('crm_activity', 'idx_activity_next_time', 'next_time');
-- idx_target 已在建表时创建

-- =====================================================
-- 12. 产品表索引优化
-- =====================================================
CALL safe_create_index('crm_product', 'idx_product_status', 'status, deleted');
CALL safe_create_index('crm_product', 'idx_product_category', 'category');

-- =====================================================
-- 13. 客户标签关联表索引
-- =====================================================
-- 主键已包含 customer_id, tag_id 的索引覆盖，无需额外添加

-- =====================================================
-- 14. 部门表索引优化
-- =====================================================
CALL safe_create_index('crm_department', 'idx_department_parent', 'parent_id');
CALL safe_create_index('crm_department', 'idx_department_status', 'status, deleted');
CALL safe_create_index('crm_department', 'idx_department_code', 'code');
CALL safe_create_index('crm_department', 'idx_department_ancestors', 'ancestors');

-- =====================================================
-- 15. 角色表索引优化
-- =====================================================
CALL safe_create_index('crm_role', 'idx_role_status', 'status, deleted');

-- =====================================================
-- 16. 字典数据表索引优化
-- =====================================================
CALL safe_create_index('crm_dict_data', 'idx_dict_data_type_status', 'dict_type, status');

-- =====================================================
-- 清理存储过程
-- =====================================================
DROP PROCEDURE IF EXISTS safe_create_index;

-- =====================================================
-- 脚本执行完成
-- =====================================================
