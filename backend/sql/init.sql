-- =============================================
-- 企业级 CRM 系统 - 数据库初始化脚本
-- 创建时间: 2026-01-20
-- 说明: 创建数据库并配置字符集
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS crm_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

-- 使用数据库
USE crm_db;

-- 显示数据库信息
SELECT
    SCHEMA_NAME AS '数据库名',
    DEFAULT_CHARACTER_SET_NAME AS '字符集',
    DEFAULT_COLLATION_NAME AS '排序规则'
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = 'crm_db';
