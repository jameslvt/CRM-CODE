# 技术研究报告: 企业级 CRM 系统

**功能**: 001-enterprise-crm
**日期**: 2026-01-18
**状态**: 已完成

## 概述

本文档记录了企业级 CRM 系统实施前的技术研究和决策。由于项目章程已明确定义技术栈约束，本研究主要聚焦于技术实践的最佳方案和集成模式。

---

## 1. 后端技术决策

### 1.1 Spring Boot 2.7.x + Java 8

**决策**: 使用 Spring Boot 2.7.18 (2.x 系列最终稳定版)

**理由**:
- 章程强制要求 Java 8 兼容性
- Spring Boot 2.7.x 是 Java 8 支持的最后稳定版本
- 2.7.x 仍有安全补丁支持至 2025 年 8 月

**替代方案评估**:
- Spring Boot 3.x: 需要 Java 17+，不符合章程约束
- Spring Boot 2.6.x: 功能较旧，建议使用 2.7.x

### 1.2 MyBatis Plus

**决策**: 使用 MyBatis Plus 3.5.x

**理由**:
- 简化 CRUD 操作，提供代码生成器
- 内置分页插件，满足列表查询需求
- 支持乐观锁、逻辑删除等企业级特性

**最佳实践**:
- 使用 `@TableName` 注解指定表名
- 使用 `@TableField(fill = FieldFill.INSERT)` 实现自动填充
- 统一使用 `LambdaQueryWrapper` 构建查询条件

### 1.3 数据库设计

**决策**: MySQL 5.7 + utf8mb4 字符集

**理由**:
- 章程强制要求 MySQL 5.7
- utf8mb4 支持完整 Unicode，包括 emoji

**最佳实践**:
- 所有表使用 `crm_` 前缀
- 主键统一使用 `BIGINT` + 雪花算法
- 时间字段使用 `DATETIME(3)` 精度
- 统一添加 `create_time`, `update_time`, `deleted` 字段

### 1.4 缓存策略

**决策**: Redis 5.x/6.x + Spring Cache

**理由**:
- 用于会话管理和热点数据缓存
- Spring Cache 抽象简化缓存操作

**使用场景**:
- 用户会话 (Spring Session)
- 字典数据缓存
- 热点查询缓存 (客户列表、产品目录)

---

## 2. 前端技术决策

### 2.1 Vue 3 + Composition API

**决策**: 使用 Vue 3.4+ 和 Composition API

**理由**:
- 章程要求 Vue.js 3
- Composition API 提供更好的代码组织和复用

**最佳实践**:
- 使用 `<script setup>` 语法
- 使用 `ref` 和 `reactive` 管理响应式状态
- 使用组合式函数 (Composables) 复用逻辑

### 2.2 Naive UI

**决策**: 使用 Naive UI 作为 UI 组件库

**理由**:
- 章程指定 Naive UI
- 完整的 TypeScript 支持
- 丰富的后台管理组件

**关键组件使用**:
- `n-data-table`: 数据表格 (支持分页、排序、筛选)
- `n-form`: 表单 (支持验证)
- `n-modal`: 模态框 (新增/编辑)
- `n-tree`: 树形组件 (组织架构)
- `n-tabs`: 标签页 (客户 360 度视图)

### 2.3 状态管理

**决策**: 使用 Pinia

**理由**:
- 章程指定 Pinia
- Vue 3 官方推荐的状态管理方案
- 完整的 TypeScript 支持

**Store 设计**:
- `useUserStore`: 当前用户信息和权限
- `useDictStore`: 字典数据缓存
- `useAppStore`: 应用全局状态 (侧边栏、主题等)

---

## 3. 架构模式决策

### 3.1 模块化单体架构

**决策**: 采用 Maven 多模块项目

**模块划分**:
```
crm-parent/
├── crm-common      # 公共模块 (无业务依赖)
├── crm-system      # 系统模块 (依赖 common)
├── crm-business    # 业务模块 (依赖 common, system)
├── crm-ai          # AI 模块 (依赖 common)
└── crm-admin       # 启动模块 (依赖所有模块)
```

**理由**:
- 章程要求模块化单体架构
- 便于代码组织和职责划分
- 未来可拆分为独立服务

### 3.2 模块间通信

**决策**: 使用 Spring Event 实现模块解耦

**理由**:
- 章程要求模块间通过事件或接口抽象通信
- 避免模块间强依赖

**示例场景**:
- 线索转化为客户时发布 `LeadConvertedEvent`
- 商机赢单时发布 `OpportunityWonEvent`
- 合同签署时发布 `ContractSignedEvent`

---

## 4. API 设计决策

### 4.1 RESTful API 规范

**决策**: 遵循 RESTful 设计原则

**URL 设计**:
- `GET /api/leads` - 线索列表
- `GET /api/leads/{id}` - 线索详情
- `POST /api/leads` - 创建线索
- `PUT /api/leads/{id}` - 更新线索
- `DELETE /api/leads/{id}` - 删除线索
- `POST /api/leads/{id}/convert` - 线索转化

### 4.2 统一响应格式

**决策**: 使用 `Result<T>` 封装所有响应

```java
@Data
public class Result<T> {
    private Integer code;    // 状态码: 200 成功, 其他为错误码
    private String msg;      // 消息
    private T data;          // 数据
    private Long timestamp;  // 时间戳
}
```

### 4.3 分页参数

**决策**: 统一分页参数命名

```
GET /api/leads?pageNum=1&pageSize=10&sortField=createTime&sortOrder=desc
```

**响应格式**:
```java
@Data
public class PageResult<T> {
    private List<T> records;   // 数据列表
    private Long total;       // 总记录数
    private Long pageNum;     // 当前页
    private Long pageSize;    // 每页大小
    private Long pages;       // 总页数
}
```

---

## 5. 安全设计决策

### 5.1 认证方案

**决策**: 使用 Spring Security + JWT

**理由**:
- 前后端分离架构适合使用 Token 认证
- JWT 支持无状态认证，便于扩展

**Token 设计**:
- Access Token: 有效期 2 小时
- Refresh Token: 有效期 7 天
- Token 存储在 Redis 中支持主动失效

### 5.2 权限控制

**决策**: 基于 RBAC (角色-权限) 模型

**设计**:
- 用户 → 角色 (多对多)
- 角色 → 权限 (多对多)
- 权限包含菜单权限和按钮权限

**数据权限**:
- 全部数据
- 本部门及下级
- 本部门
- 仅本人

---

## 6. 集成模式决策

### 6.1 OpenAI API 集成 (P2 阶段)

**决策**: 封装 OpenAI 兼容接口

**设计**:
- 抽象 `AiService` 接口
- 支持 OpenAI 官方 API 和兼容 API (如 Azure OpenAI)
- 配置化的 API 端点和密钥

### 6.2 MCP 协议 (P2 阶段)

**决策**: 预留 MCP Server 接口

**设计**:
- 实现 MCP 协议的 Tool 定义
- 支持通过 AI 创建线索、客户、商机
- 支持通过 AI 查询数据

### 6.3 第三方平台集成 (P3 阶段)

**决策**: 插件化设计

**设计**:
- 定义统一的 `NotificationService` 接口
- 各平台实现独立插件
- 通过配置启用/禁用

---

## 7. 测试框架决策

### 7.1 后端测试

**决策**: JUnit 5 + Mockito 4.x + Spring Boot Test + TestContainers

**理由**:
- `spring-boot-starter-test` 默认包含 JUnit 5 和 Mockito
- **关键**: Mockito 5.0+ 需要 Java 11+，不兼容 Java 8
- Mockito 4.11.x 是 Java 8 支持的最后版本
- TestContainers 用于集成测试的真实数据库环境

**版本选择**:
- JUnit: 5.9.x - 5.10.x
- Mockito: 4.11.x (锁定，禁止升级到 5.x)
- TestContainers: 1.19.x

**测试分层**:
- 单元测试 (60%): JUnit 5 + Mockito
- 集成测试 (30%): @SpringBootTest + TestContainers
- E2E 测试 (10%): 核心业务流程验证

### 7.2 前端测试

**决策**: Vitest + Vue Test Utils + Playwright

**理由**:
- Vitest 是 Vue 3 官方推荐的测试框架
- 基于 esbuild 和 Vite，比 Jest 快 5-10 倍
- 与 Vite 配置共享，零额外配置
- 完整的 TypeScript 支持

**版本选择**:
- Vitest: 1.x
- Vue Test Utils: 2.4.x
- Playwright: 1.x (用于 E2E 测试)

**替代方案评估**:
- Jest: 速度慢、Vite 支持弱，不采用
- Cypress: 仅适合 E2E，单元测试需搭配其他框架

---

## 8. 研究结论

所有技术决策均符合项目章程约束，无 NEEDS CLARIFICATION 项。

| 领域 | 决策 | 状态 |
|------|------|------|
| 后端框架 | Spring Boot 2.7.18 + Java 8 | ✅ 已确定 |
| ORM | MyBatis Plus 3.5.x | ✅ 已确定 |
| 数据库 | MySQL 5.7 + utf8mb4 | ✅ 已确定 |
| 缓存 | Redis 5.x/6.x | ✅ 已确定 |
| 前端框架 | Vue 3.4+ (Composition API) | ✅ 已确定 |
| UI 组件库 | Naive UI | ✅ 已确定 |
| 状态管理 | Pinia | ✅ 已确定 |
| 认证方案 | Spring Security + JWT | ✅ 已确定 |
| API 文档 | Knife4j (Swagger 2) | ✅ 已确定 |
| 后端测试 | JUnit 5 + Mockito 4.x | ✅ 已确定 |
| 前端测试 | Vitest + Vue Test Utils | ✅ 已确定 |
