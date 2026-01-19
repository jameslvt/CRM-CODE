# 实施计划: 企业级 CRM 系统

**分支**: `001-enterprise-crm` | **日期**: 2026-01-18 | **规范**: [spec.md](./spec.md)
**输入**: 来自 `/specs/001-enterprise-crm/spec.md` 的功能规范

**注意**: 此模板由 `/speckit.plan` 命令填充. 执行工作流程请参见 `.specify/templates/commands/plan.md`.

## 摘要

构建企业级客户关系管理系统（CRM），实现从线索到现金（L2C: Leads to Cash）的完整业务闭环。系统采用模块化单体架构，使用 Java 8 + Spring Boot 2.7.x 后端和 Vue 3 + Naive UI 前端技术栈。

**核心业务模块 (P0)**:
- 系统基础: 用户权限、组织架构
- L2C 核心: 线索 → 客户 → 联系人 → 商机 → 产品 → 合同 → 回款

**增强模块 (P1/P2/P3)**:
- P1: 跟进记录、数据分析、仪表盘
- P2: AI 智能辅助（MCP 集成、智能创建、智能查重）
- P3: 第三方集成（企业微信、钉钉、飞书）

## 技术背景

**语言/版本**: Java 8 (后端), TypeScript (前端)
**主要依赖**:
- 后端: Spring Boot 2.7.x, MyBatis Plus, Lombok, Maven
- 前端: Vue.js 3 (Composition API), Naive UI, Pinia
- 缓存: Redis 5.x/6.x

**存储**: MySQL 5.7 (字符集 `utf8mb4`)
**测试**: JUnit 5 + Mockito 4.x (后端), Vitest + Vue Test Utils (前端)
**目标平台**: Web 应用程序 (现代浏览器 + Linux 服务器)
**项目类型**: Web 应用程序 (前后端分离)
**性能目标**:
- 100 并发用户正常使用
- 页面加载时间 < 3秒
- 10000 条记录流畅浏览

**约束条件**:
- 线索录入 < 2分钟
- 转化操作 < 30秒
- 查询响应 < 5秒
- 80% 日常操作 3次点击内完成

**规模/范围**:
- 初期用户规模 100 人以内
- 11 个核心用户故事
- 45 个功能需求
- 10 个核心业务实体

## 章程检查

*门控: 必须在阶段 0 研究前通过. 阶段 1 设计后重新检查.*

### 门控条件 (来自项目章程)

| # | 原则 | 要求 | 状态 |
|---|------|------|------|
| 1 | 业务优先 | L2C 闭环优先，AI 功能后置 (P2) | ✅ 符合 - P0 专注 L2C 核心 |
| 2 | 技术栈 (后端) | Java 8 + Spring Boot 2.7.x + MyBatis Plus + MySQL 5.7 | ✅ 符合 |
| 3 | 技术栈 (前端) | Vue 3 + Naive UI + Pinia + TypeScript | ✅ 符合 |
| 4 | 架构模式 | 模块化单体，禁止微服务注册中心 | ✅ 符合 - 采用模块化单体 |
| 5 | 分层架构 | Controller → Service → Mapper 严格分层 | ✅ 符合 |
| 6 | 模块解耦 | 模块间通过 Spring Event 或接口抽象通信 | ✅ 符合 |
| 7 | 数据库命名 | 表前缀 `crm_` | ✅ 符合 |
| 8 | 时间处理 | 使用 `java.time` (LocalDateTime) | ✅ 符合 |
| 9 | API 设计 | RESTful + Swagger 2/Knife4j + Result<T> | ✅ 符合 |
| 10 | 禁止事项 | 无过度设计/硬编码/Service层操作Request | ✅ 符合 |

### 门控评估结果

**阶段 0 前评估**: ✅ **通过** - 所有章程条件均符合，可进入阶段 0 研究

**阶段 1 后评估**: ✅ **通过** - 设计制品验证完成
- `data-model.md`: 所有表使用 `crm_` 前缀，时间字段使用 `DATETIME(3)`
- `contracts/`: API 遵循 RESTful 规范，使用 `Result<T>` 响应格式
- `quickstart.md`: 技术栈配置与章程一致 (Java 8, Spring Boot 2.7.x, Vue 3)
- 架构设计采用模块化单体，无微服务注册中心

## 项目结构

### 文档(此功能)

```
specs/001-enterprise-crm/
├── spec.md              # 功能规范
├── plan.md              # 此文件 (/speckit.plan 命令输出)
├── research.md          # 阶段 0 输出 (/speckit.plan 命令)
├── data-model.md        # 阶段 1 输出 (/speckit.plan 命令)
├── quickstart.md        # 阶段 1 输出 (/speckit.plan 命令)
├── contracts/           # 阶段 1 输出 (/speckit.plan 命令)
│   ├── openapi.yaml     # RESTful API 定义
│   └── responses.md     # 统一响应格式说明
└── tasks.md             # 阶段 2 输出 (/speckit.tasks 命令)
```

### 源代码(仓库根目录)

```
# 后端 - 模块化单体架构 (Maven 多模块)
backend/
├── pom.xml                          # 父 POM
├── crm-common/                      # 公共模块
│   └── src/main/java/
│       └── com/crm/common/
│           ├── config/              # 通用配置
│           ├── utils/               # 工具类
│           ├── exception/           # 异常定义
│           └── result/              # Result<T> 封装
├── crm-system/                      # 系统模块
│   └── src/main/java/
│       └── com/crm/system/
│           ├── controller/          # 用户、角色、部门 API
│           ├── service/             # 业务逻辑
│           ├── mapper/              # MyBatis Mapper
│           ├── entity/              # 实体类
│           └── dto/                 # 数据传输对象
├── crm-business/                    # 核心业务模块
│   └── src/main/java/
│       └── com/crm/business/
│           ├── controller/          # 线索、客户、商机、合同、回款 API
│           ├── service/
│           ├── mapper/
│           ├── entity/
│           ├── dto/
│           └── event/               # 业务事件
├── crm-ai/                          # AI 模块 (P2, 初期可不启动)
│   └── src/main/java/
│       └── com/crm/ai/
│           ├── controller/
│           ├── service/
│           └── mcp/                 # MCP Server 实现
└── crm-admin/                       # 启动模块
    └── src/main/java/
        └── com/crm/
            └── CrmApplication.java  # 主启动类

# 后端测试
backend/
├── crm-system/src/test/
├── crm-business/src/test/
└── crm-admin/src/test/
    └── java/com/crm/
        ├── integration/             # 集成测试
        └── unit/                    # 单元测试

# 前端 - Vue 3 + TypeScript
frontend/
├── package.json
├── vite.config.ts
├── tsconfig.json
└── src/
    ├── main.ts                      # 入口
    ├── App.vue
    ├── router/                      # 路由配置
    ├── stores/                      # Pinia 状态管理
    │   ├── user.ts
    │   ├── lead.ts
    │   ├── customer.ts
    │   └── opportunity.ts
    ├── api/                         # API 调用封装
    │   ├── request.ts               # Axios 封装
    │   ├── system/                  # 系统模块 API
    │   └── business/                # 业务模块 API
    ├── components/                  # 通用组件
    │   ├── common/                  # 基础组件
    │   └── business/                # 业务组件
    ├── views/                       # 页面视图
    │   ├── system/                  # 系统管理页面
    │   │   ├── user/
    │   │   ├── role/
    │   │   └── department/
    │   └── business/                # 业务页面
    │       ├── lead/                # 线索管理
    │       ├── customer/            # 客户管理
    │       ├── contact/             # 联系人管理
    │       ├── opportunity/         # 商机管理
    │       ├── product/             # 产品管理
    │       ├── contract/            # 合同管理
    │       ├── payment/             # 回款管理
    │       └── dashboard/           # 仪表盘
    ├── layouts/                     # 布局组件
    ├── utils/                       # 工具函数
    └── types/                       # TypeScript 类型定义

# 前端测试
frontend/
└── tests/
    ├── unit/                        # 单元测试
    └── e2e/                         # 端到端测试
```

**结构决策**: 采用 Web 应用程序结构（选项2），后端使用 Maven 多模块实现模块化单体架构，前端使用 Vue 3 标准项目结构

## 复杂度跟踪

*仅在章程检查有必须证明的违规时填写*

**状态**: ✅ 无违规 - 当前设计完全遵循章程约束，无需复杂性证明
