-- Add convert_time column to crm_lead table
ALTER TABLE `crm_lead` ADD COLUMN `convert_time` DATETIME(3) DEFAULT NULL COMMENT '转化时间' AFTER `customer_id`;
