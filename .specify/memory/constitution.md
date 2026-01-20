<!--
## 同步影响报告

- **版本更改**: 无 (模板) → 1.0.0 (初始发布)
- **修改的原则**: 无 (全新创建)
- **添加的部分**:
  - I. 业务优先
  - II. 技术栈约束
  - III. 模块化单体架构
  - IV. 分层架构
  - V. 编码规范
  - API 设计规范
  - 开发工作流
  - 禁忌事项
- **删除的部分**: 无
- **模板同步状态**:
  - `.specify/templates/plan-template.md` ✅ 无需更新 (章程检查部分已兼容)
  - `.specify/templates/spec-template.md` ✅ 无需更新 (需求格式已兼容)
  - `.specify/templates/tasks-template.md` ✅ 无需更新 (任务分类已兼容)
- **延迟 TODO**: 无
-->

# 企业级 CRM 系统项目章程

## 核心使命

构建一个**以业务为核心**的企业级 CRM 系统（对标 CordysCRM）。

**优先级原则**：
1. **P0 (首要目标)**: 优先确保 **L2C (线索到现金)** 的业务闭环稳定运行，实现客户、商机、合同、回款的全流程管理。
2. **P1 (次要目标)**: 在业务功能完善的基础上，集成 **Model Context Protocol (MCP)** 并接入**标准 OpenAI 接口**，为系统赋予 AI 辅助能力。

## 核心原则

### I. 业务优先

- **L2C 闭环优先**: 所有开发工作必须优先保障线索(Leads)→客户(Customer)→商机(Opportunity)→合同(Contract)→回款(Payment)的完整业务链路。
- **AI 功能后置**: 仅在 P0 业务功能稳定运行后，才可启动 AI 模块开发。
- **价值驱动**: 每个功能必须明确其业务价值，拒绝无明确业务目标的技术实验。

### II. 技术栈约束

**后端 (Backend)**:
- **语言**: Java 8 (必须严格遵守，兼容现有生产环境)
- **框架**: Spring Boot 2.7.x (Spring Boot 2 的最终稳定版本)
- **ORM**: MyBatis Plus
- **数据库**: MySQL 5.7 (字符集必须使用 `utf8mb4`)
- **缓存**: Redis 5.x/6.x
- **工具链**: Maven, Lombok

**前端 (Frontend)**:
- **框架**: Vue.js 3 (Composition API)，前端在设计开发时，使用skills技能（ui-ux-pro-max-skill）
- **UI 组件库**: Naive UI
- **状态管理**: Pinia
- **语言**: TypeScript

**AI 与集成 (AI & Integration)**:
- **接口标准**: 仅需适配标准 OpenAI API (Chat Completion) 格式
- **扩展协议**: 预留 MCP (Model Context Protocol) Server 接口实现

### III. 模块化单体架构

- **架构模式**: 采用模块化单体 (Modular Monolith) 架构，便于统一部署与维护，未来可拆分为微服务。
- **模块划分**:
  - `crm-common`: 公共工具、基类
  - `crm-system`: 用户、角色、菜单权限
  - `crm-business`: 核心业务（客户、线索、商机、合同）
  - `crm-ai`: OpenAI 接口封装与 MCP 实现（独立模块，初期可不启动）
- **模块解耦**: 模块间通信必须通过事件 (Spring Event) 或接口抽象，禁止强依赖。

### IV. 分层架构

所有代码必须遵循严格的三层架构:

1. **Controller**: 接口层，负责参数校验
2. **Service**: 业务逻辑层，负责事务控制 (`@Transactional`)
3. **Mapper**: 数据持久层

**注意**: 由于使用 Java 8，DTO 必须使用 Lombok `@Data`，禁止使用 `record` 特性。

### V. 编码规范

**通用规范**:
- **注释**: 核心业务逻辑必须包含中文注释
- **命名**:
  - 数据库表前缀: `crm_` (如 `crm_leads`)
  - 类名/方法名: 驼峰命名法
- **时间处理**: 统一使用 `java.time` 包 (LocalDateTime)，禁止使用旧的 Date 类

## API 设计规范

- **风格**: RESTful API
- **文档**: 使用 Swagger 2 / Knife4j (适配 Spring Boot 2.x)
- **响应格式**: 统一使用 `Result<T>` 封装 (`code`, `msg`, `data`)

## 开发工作流

采用 **Spec-Driven** 开发流程:

1. **Spec First**: 编写功能 Spec 文档
2. **Review**: 确认业务逻辑是否闭环
3. **Implementation**: 生成代码

## 禁忌事项 ❌

以下行为被严格禁止:

- **禁止过度设计**: 初期禁止引入微服务注册中心 (Nacos/Eureka) 或复杂的分布式事务 (Seata)，必须保持架构简单
- **禁止硬编码**: 必须使用常量或枚举，禁止魔法数字和魔法字符串
- **禁止混用**: 严禁在 Service 层直接操作 `HttpServletRequest`

## 治理

- **章程优先级**: 本章程优先于所有其他开发实践和约定
- **修正流程**: 任何章程修改必须经过文档化、团队审批和迁移计划
- **合规检查**: 所有 PR/代码审查必须验证是否符合本章程原则
- **复杂性证明**: 任何超出本章程约束的复杂性引入必须提供书面理由

### 任务状态闭环 (Mandatory)
每次执行 `/speckit.implement` 或完成代码编写后，**必须**执行以下收尾动作：
1.  检查 `specs/*/tasks.md` 文件。
2.  将当前完成的任务项从 `[ ]` 标记为 `[x]`。
3.  如果任务未全部完成，严禁标记整个模块为完成。

**版本**: 1.0.0 | **批准日期**: 2026-01-18 | **最后修正**: 2026-01-18
