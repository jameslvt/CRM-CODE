# 数据模型设计: 企业级 CRM 系统

**功能**: 001-enterprise-crm
**日期**: 2026-01-18
**状态**: 已完成

## 概述

本文档定义了企业级 CRM 系统的数据模型，包括所有实体、字段、关系和验证规则。所有表使用 `crm_` 前缀，字符集为 `utf8mb4`。

---

## 实体关系图 (ER Diagram)

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   用户      │────→│   角色      │←────│   权限      │
│  (User)     │ N:N │  (Role)     │ N:N │(Permission) │
└─────────────┘     └─────────────┘     └─────────────┘
       │
       │ 1:N
       ↓
┌─────────────┐
│   部门      │
│(Department) │←──────────────────────────────────────┐
└─────────────┘                                       │
       │ 1:N (self)                                   │
       ↓                                              │
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   线索      │────→│   客户      │←────│   联系人    │
│  (Lead)     │ 1:1 │ (Customer)  │ 1:N │ (Contact)   │
└─────────────┘     └─────────────┘     └─────────────┘
                           │
                           │ 1:N
                           ↓
                    ┌─────────────┐     ┌─────────────┐
                    │   商机      │←───→│   产品      │
                    │(Opportunity)│ N:N │ (Product)   │
                    └─────────────┘     └─────────────┘
                           │
                           │ 1:N
                           ↓
                    ┌─────────────┐
                    │   合同      │
                    │ (Contract)  │
                    └─────────────┘
                           │
                           │ 1:N
                           ↓
                    ┌─────────────┐
                    │   回款      │
                    │ (Payment)   │
                    └─────────────┘

┌─────────────┐
│  跟进记录   │ → 可关联: 线索、客户、商机
│ (Activity)  │
└─────────────┘
```

---

## 1. 系统模块实体

### 1.1 用户表 (crm_user)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 (雪花算法) |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码 (BCrypt 加密) |
| nickname | VARCHAR(50) | | 昵称 |
| email | VARCHAR(100) | | 邮箱 |
| phone | VARCHAR(20) | | 手机号 |
| avatar | VARCHAR(255) | | 头像 URL |
| dept_id | BIGINT | FK | 所属部门 ID |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 0-禁用, 1-正常 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除: 0-未删除, 1-已删除 |

**索引**:
- `idx_username` (username)
- `idx_dept_id` (dept_id)
- `idx_status` (status)

---

### 1.2 角色表 (crm_role)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| role_name | VARCHAR(50) | NOT NULL, UNIQUE | 角色名称 |
| role_key | VARCHAR(50) | NOT NULL, UNIQUE | 角色标识 |
| sort | INT | NOT NULL, DEFAULT 0 | 排序 |
| data_scope | TINYINT | NOT NULL, DEFAULT 1 | 数据权限: 1-全部, 2-本部门及下级, 3-本部门, 4-仅本人 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态 |
| remark | VARCHAR(500) | | 备注 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除 |

---

### 1.3 用户角色关联表 (crm_user_role)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| user_id | BIGINT | PK, FK | 用户 ID |
| role_id | BIGINT | PK, FK | 角色 ID |

---

### 1.4 权限表 (crm_permission)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| parent_id | BIGINT | DEFAULT 0 | 父权限 ID |
| name | VARCHAR(50) | NOT NULL | 权限名称 |
| permission_key | VARCHAR(100) | NOT NULL, UNIQUE | 权限标识 |
| type | TINYINT | NOT NULL | 类型: 1-目录, 2-菜单, 3-按钮 |
| path | VARCHAR(200) | | 路由路径 |
| component | VARCHAR(200) | | 组件路径 |
| icon | VARCHAR(50) | | 图标 |
| sort | INT | NOT NULL, DEFAULT 0 | 排序 |
| visible | TINYINT | NOT NULL, DEFAULT 1 | 是否可见 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |

---

### 1.5 角色权限关联表 (crm_role_permission)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| role_id | BIGINT | PK, FK | 角色 ID |
| permission_id | BIGINT | PK, FK | 权限 ID |

---

### 1.6 部门表 (crm_department)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| parent_id | BIGINT | DEFAULT 0 | 父部门 ID |
| name | VARCHAR(50) | NOT NULL | 部门名称 |
| leader_id | BIGINT | | 负责人 ID |
| sort | INT | NOT NULL, DEFAULT 0 | 排序 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除 |

---

## 2. 业务模块实体

### 2.1 线索表 (crm_lead)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| name | VARCHAR(100) | NOT NULL | 线索名称 |
| source | VARCHAR(50) | | 来源: 官网、广告、转介绍等 |
| phone | VARCHAR(20) | | 联系电话 |
| email | VARCHAR(100) | | 邮箱 |
| company | VARCHAR(200) | | 公司名称 |
| position | VARCHAR(50) | | 职位 |
| industry | VARCHAR(50) | | 行业 |
| rating | VARCHAR(10) | | 评级: A-高, B-中, C-低, D-极低 |
| address | VARCHAR(500) | | 地址 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 1-新建, 2-跟进中, 3-已转化, 4-已关闭 |
| owner_id | BIGINT | FK | 负责人 ID |
| customer_id | BIGINT | FK | 转化后的客户 ID |
| remark | TEXT | | 备注 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |
| create_by | BIGINT | | 创建人 |
| update_by | BIGINT | | 更新人 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除 |

**状态流转**:
```
新建(1) → 跟进中(2) → 已转化(3)
                   ↘ 已关闭(4)
```

**索引**:
- `idx_owner_id` (owner_id)
- `idx_status` (status)
- `idx_create_time` (create_time)

---

### 2.2 客户表 (crm_customer)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| name | VARCHAR(200) | NOT NULL | 客户名称 |
| short_name | VARCHAR(50) | | 简称 |
| code | VARCHAR(50) | UNIQUE | 客户编码 |
| industry | VARCHAR(50) | | 行业 |
| scale | VARCHAR(50) | | 规模: 小型、中型、大型、集团 |
| source | VARCHAR(50) | | 来源 |
| level | VARCHAR(20) | | 级别: A、B、C、D |
| phone | VARCHAR(20) | | 电话 |
| fax | VARCHAR(20) | | 传真 |
| website | VARCHAR(200) | | 网站 |
| address | VARCHAR(500) | | 地址 |
| owner_id | BIGINT | FK | 负责人 ID |
| lead_id | BIGINT | FK | 来源线索 ID |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 1-正常, 2-公海 |
| remark | TEXT | | 备注 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |
| create_by | BIGINT | | 创建人 |
| update_by | BIGINT | | 更新人 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除 |

**索引**:
- `idx_name` (name)
- `idx_owner_id` (owner_id)
- `idx_status` (status)

---

### 2.3 联系人表 (crm_contact)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| customer_id | BIGINT | FK, NOT NULL | 客户 ID |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| gender | TINYINT | | 性别: 1-男, 2-女 |
| position | VARCHAR(50) | | 职位 |
| department | VARCHAR(50) | | 部门 |
| phone | VARCHAR(20) | | 电话 |
| mobile | VARCHAR(20) | | 手机 |
| email | VARCHAR(100) | | 邮箱 |
| wechat | VARCHAR(50) | | 微信 |
| is_primary | TINYINT | NOT NULL, DEFAULT 0 | 是否主要联系人 |
| birthday | DATE | | 生日 |
| remark | TEXT | | 备注 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除 |

**索引**:
- `idx_customer_id` (customer_id)

---

### 2.4 产品表 (crm_product)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| name | VARCHAR(200) | NOT NULL | 产品名称 |
| code | VARCHAR(50) | UNIQUE | 产品编码 |
| category | VARCHAR(50) | | 分类 |
| unit | VARCHAR(20) | | 计量单位 |
| price | DECIMAL(12,2) | NOT NULL, DEFAULT 0 | 单价 |
| description | TEXT | | 描述 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 0-停用, 1-启用 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除 |

**索引**:
- `idx_status` (status)

---

### 2.5 商机表 (crm_opportunity)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| name | VARCHAR(200) | NOT NULL | 商机名称 |
| customer_id | BIGINT | FK, NOT NULL | 客户 ID |
| contact_id | BIGINT | FK | 联系人 ID |
| amount | DECIMAL(14,2) | NOT NULL, DEFAULT 0 | 预计金额 |
| stage | VARCHAR(50) | NOT NULL | 阶段: 需求确认、方案报价、商务谈判、赢单、输单 |
| probability | INT | DEFAULT 0 | 赢单概率 (%) |
| expected_date | DATE | | 预计成交日期 |
| owner_id | BIGINT | FK | 负责人 ID |
| source | VARCHAR(50) | | 商机来源 |
| remark | TEXT | | 备注 |
| close_reason | VARCHAR(500) | | 关闭原因 (输单时) |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |
| create_by | BIGINT | | 创建人 |
| update_by | BIGINT | | 更新人 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除 |

**阶段流转**:
```
需求确认(10%) → 方案报价(30%) → 商务谈判(60%) → 赢单(100%)
                                              ↘ 输单(0%)
```

**索引**:
- `idx_customer_id` (customer_id)
- `idx_owner_id` (owner_id)
- `idx_stage` (stage)

---

### 2.6 商机产品关联表 (crm_opportunity_product)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| opportunity_id | BIGINT | FK, NOT NULL | 商机 ID |
| product_id | BIGINT | FK, NOT NULL | 产品 ID |
| quantity | INT | NOT NULL, DEFAULT 1 | 数量 |
| price | DECIMAL(12,2) | NOT NULL | 单价 |
| discount | DECIMAL(5,2) | DEFAULT 100 | 折扣 (%) |
| amount | DECIMAL(14,2) | NOT NULL | 金额 |

---

### 2.7 合同表 (crm_contract)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| contract_no | VARCHAR(50) | NOT NULL, UNIQUE | 合同编号 |
| name | VARCHAR(200) | NOT NULL | 合同名称 |
| customer_id | BIGINT | FK, NOT NULL | 客户 ID |
| opportunity_id | BIGINT | FK | 商机 ID |
| amount | DECIMAL(14,2) | NOT NULL | 合同金额 |
| start_date | DATE | | 开始日期 |
| end_date | DATE | | 结束日期 |
| sign_date | DATE | | 签约日期 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 1-草稿, 2-审批中, 3-执行中, 4-已完成, 5-已终止 |
| owner_id | BIGINT | FK | 负责人 ID |
| file_url | VARCHAR(500) | | 合同文件 URL |
| remark | TEXT | | 备注 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |
| create_by | BIGINT | | 创建人 |
| update_by | BIGINT | | 更新人 |
| deleted | TINYINT | NOT NULL, DEFAULT 0 | 逻辑删除 |

**状态流转**:
```
草稿(1) → 审批中(2) → 执行中(3) → 已完成(4)
                              ↘ 已终止(5)
```

**索引**:
- `idx_customer_id` (customer_id)
- `idx_owner_id` (owner_id)
- `idx_status` (status)

---

### 2.8 回款计划表 (crm_payment_plan)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| contract_id | BIGINT | FK, NOT NULL | 合同 ID |
| period | INT | NOT NULL | 期数 |
| plan_amount | DECIMAL(14,2) | NOT NULL | 计划金额 |
| plan_date | DATE | NOT NULL | 计划回款日期 |
| actual_amount | DECIMAL(14,2) | DEFAULT 0 | 实际回款金额 |
| actual_date | DATE | | 实际回款日期 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态: 1-待回款, 2-部分回款, 3-已回款, 4-逾期 |
| remark | TEXT | | 备注 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |

**索引**:
- `idx_contract_id` (contract_id)
- `idx_status` (status)

---

### 2.9 回款记录表 (crm_payment_record)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| plan_id | BIGINT | FK, NOT NULL | 回款计划 ID |
| amount | DECIMAL(14,2) | NOT NULL | 回款金额 |
| payment_date | DATE | NOT NULL | 回款日期 |
| payment_method | VARCHAR(50) | | 付款方式: 银行转账、支票、现金 |
| remark | TEXT | | 备注 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| create_by | BIGINT | | 创建人 |

**索引**:
- `idx_plan_id` (plan_id)

---

### 2.10 跟进记录表 (crm_activity)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| type | VARCHAR(20) | NOT NULL | 类型: 电话、拜访、邮件、会议、其他 |
| content | TEXT | NOT NULL | 跟进内容 |
| target_type | VARCHAR(20) | NOT NULL | 关联对象类型: lead、customer、opportunity |
| target_id | BIGINT | NOT NULL | 关联对象 ID |
| next_time | DATETIME | | 下次跟进时间 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| create_by | BIGINT | NOT NULL | 创建人 |

**索引**:
- `idx_target` (target_type, target_id)
- `idx_create_time` (create_time)

---

## 3. 字典表

### 3.1 字典类型表 (crm_dict_type)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| name | VARCHAR(100) | NOT NULL | 字典名称 |
| type | VARCHAR(100) | NOT NULL, UNIQUE | 字典类型 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态 |
| remark | VARCHAR(500) | | 备注 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |

### 3.2 字典数据表 (crm_dict_data)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| dict_type | VARCHAR(100) | FK, NOT NULL | 字典类型 |
| label | VARCHAR(100) | NOT NULL | 字典标签 |
| value | VARCHAR(100) | NOT NULL | 字典值 |
| sort | INT | NOT NULL, DEFAULT 0 | 排序 |
| status | TINYINT | NOT NULL, DEFAULT 1 | 状态 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |
| update_time | DATETIME(3) | NOT NULL | 更新时间 |

---

## 4. 操作日志表

### 4.1 操作日志表 (crm_operation_log)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK | 主键 |
| module | VARCHAR(50) | NOT NULL | 模块 |
| operation | VARCHAR(50) | NOT NULL | 操作类型 |
| method | VARCHAR(200) | | 请求方法 |
| request_url | VARCHAR(500) | | 请求 URL |
| request_params | TEXT | | 请求参数 |
| response_result | TEXT | | 响应结果 |
| user_id | BIGINT | | 操作用户 ID |
| ip | VARCHAR(50) | | IP 地址 |
| duration | BIGINT | | 耗时 (ms) |
| status | TINYINT | | 状态: 0-失败, 1-成功 |
| error_msg | TEXT | | 错误信息 |
| create_time | DATETIME(3) | NOT NULL | 创建时间 |

---

## 5. 验证规则汇总

| 实体 | 字段 | 验证规则 |
|------|------|----------|
| User | username | 4-50 字符, 字母数字下划线 |
| User | password | 6-20 字符, 至少包含字母和数字 |
| User | email | 有效邮箱格式 |
| User | phone | 11 位手机号 |
| Lead | name | 必填, 2-100 字符 |
| Lead | phone | 有效电话格式 |
| Customer | name | 必填, 2-200 字符 |
| Contact | name | 必填, 2-50 字符 |
| Product | name | 必填, 2-200 字符 |
| Product | price | >= 0 |
| Opportunity | name | 必填, 2-200 字符 |
| Opportunity | amount | >= 0 |
| Contract | contract_no | 必填, 唯一 |
| Contract | amount | > 0 |
| PaymentPlan | plan_amount | > 0 |
| PaymentRecord | amount | > 0 |
| Activity | content | 必填 |
