# 任务: 企业级 CRM 系统

**输入**: 来自 `/specs/001-enterprise-crm/` 的设计文档
**前置条件**: plan.md ✅、spec.md ✅、research.md ✅、data-model.md ✅、contracts/ ✅

**测试**: 功能规范未明确要求测试，本任务列表聚焦实现任务。

**组织结构**: 任务按用户故事分组，以便每个故事能够独立实施和测试。

## 格式: `[ID] [P?] [Story?] 描述`
- **[P]**: 可以并行运行（不同文件，无依赖关系）
- **[Story]**: 此任务属于哪个用户故事（例如: US1、US2、US3）
- 在描述中包含确切的文件路径

## 路径约定
- **后端**: `backend/crm-{module}/src/main/java/com/crm/{module}/`
- **前端**: `frontend/src/`
- **数据库**: `backend/sql/`

---

## 阶段 1: 设置（共享基础设施）

**目的**: 项目初始化和基本结构

- [x] T001 创建 Maven 多模块项目结构 `backend/pom.xml`（父 POM）
- [x] T002 [P] 创建 `backend/crm-common/pom.xml` 公共模块
- [x] T003 [P] 创建 `backend/crm-system/pom.xml` 系统模块
- [x] T004 [P] 创建 `backend/crm-business/pom.xml` 业务模块
- [x] T005 [P] 创建 `backend/crm-admin/pom.xml` 启动模块
- [x] T006 创建 Vue 3 项目 `frontend/package.json`（Vite + TypeScript + Naive UI + Pinia）
- [x] T007 [P] 配置后端代码检查 `backend/.editorconfig` 和 Checkstyle
- [x] T008 [P] 配置前端代码检查 `frontend/.eslintrc.js` 和 Prettier

---

## 阶段 2: 基础（阻塞前置条件）

**目的**: 在任何用户故事可以实施之前必须完成的核心基础设施

**⚠️ 关键**: 在此阶段完成之前，无法开始任何用户故事工作

### 2.1 数据库初始化

- [x] T009 创建数据库初始化脚本 `backend/sql/init.sql`（创建数据库和字符集配置）
- [x] T010 创建系统模块表结构 `backend/sql/schema-system.sql`（crm_user, crm_role, crm_permission, crm_department 等）
- [x] T011 创建业务模块表结构 `backend/sql/schema-business.sql`（crm_lead, crm_customer, crm_contact 等）
- [x] T012 创建初始数据脚本 `backend/sql/init-data.sql`（管理员账号、基础角色、字典数据）

### 2.2 公共模块 (crm-common)

- [x] T013 [P] 创建统一响应封装 `backend/crm-common/src/main/java/com/crm/common/result/Result.java`
- [x] T014 [P] 创建分页结果封装 `backend/crm-common/src/main/java/com/crm/common/result/PageResult.java`
- [x] T015 [P] 创建全局异常类 `backend/crm-common/src/main/java/com/crm/common/exception/BusinessException.java`
- [x] T016 [P] 创建全局异常处理器 `backend/crm-common/src/main/java/com/crm/common/exception/GlobalExceptionHandler.java`
- [x] T017 [P] 创建基础实体类 `backend/crm-common/src/main/java/com/crm/common/entity/BaseEntity.java`（id, createTime, updateTime, deleted）
- [x] T018 [P] 创建 MyBatis Plus 配置 `backend/crm-common/src/main/java/com/crm/common/config/MybatisPlusConfig.java`（分页插件、自动填充）
- [x] T019 [P] 创建 Redis 配置 `backend/crm-common/src/main/java/com/crm/common/config/RedisConfig.java`
- [x] T020 [P] 创建雪花算法 ID 生成器 `backend/crm-common/src/main/java/com/crm/common/utils/IdGenerator.java`

### 2.3 安全框架

- [x] T021 创建 JWT 工具类 `backend/crm-common/src/main/java/com/crm/common/utils/JwtUtils.java`
- [x] T022 创建 Spring Security 配置 `backend/crm-admin/src/main/java/com/crm/config/SecurityConfig.java`
- [x] T023 创建 JWT 认证过滤器 `backend/crm-admin/src/main/java/com/crm/security/JwtAuthenticationFilter.java`
- [x] T024 创建权限校验服务 `backend/crm-admin/src/main/java/com/crm/security/PermissionService.java`

### 2.4 启动模块配置

- [x] T025 创建主启动类 `backend/crm-admin/src/main/java/com/crm/CrmApplication.java`
- [x] T026 创建应用配置文件 `backend/crm-admin/src/main/resources/application.yml`
- [x] T027 创建本地开发配置 `backend/crm-admin/src/main/resources/application-local.yml.example`
- [x] T028 配置 Knife4j/Swagger `backend/crm-admin/src/main/java/com/crm/config/SwaggerConfig.java`

### 2.5 前端基础架构

- [x] T029 [P] 创建 Axios 请求封装 `frontend/src/api/request.ts`（拦截器、错误处理、Token 刷新）
- [x] T030 [P] 创建路由配置 `frontend/src/router/index.ts`
- [x] T031 [P] 创建主布局组件 `frontend/src/layouts/MainLayout.vue`（侧边栏、顶栏、内容区）
- [x] T032 [P] 创建用户状态管理 `frontend/src/stores/user.ts`（登录状态、用户信息、权限）
- [x] T033 [P] 创建应用状态管理 `frontend/src/stores/app.ts`（侧边栏折叠、主题）
- [x] T034 [P] 创建 TypeScript 通用类型 `frontend/src/types/common.ts`（Result、PageResult、分页参数）

### 2.6 字典管理

- [x] T035 [P] 创建字典类型实体 `backend/crm-system/src/main/java/com/crm/system/entity/DictType.java`
- [x] T036 [P] 创建字典数据实体 `backend/crm-system/src/main/java/com/crm/system/entity/DictData.java`
- [x] T037 [P] 创建字典类型 Mapper `backend/crm-system/src/main/java/com/crm/system/mapper/DictTypeMapper.java`
- [x] T038 [P] 创建字典数据 Mapper `backend/crm-system/src/main/java/com/crm/system/mapper/DictDataMapper.java`
- [x] T039 创建字典服务 `backend/crm-system/src/main/java/com/crm/system/service/DictService.java`
- [x] T040 创建字典控制器 `backend/crm-system/src/main/java/com/crm/system/controller/DictController.java`
- [x] T041 创建字典状态管理 `frontend/src/stores/dict.ts`（字典缓存）

### 2.7 商机阶段配置

- [x] T042 [P] 创建商机阶段配置实体 `backend/crm-system/src/main/java/com/crm/system/entity/OpportunityStage.java`
- [x] T043 [P] 创建商机阶段 Mapper `backend/crm-system/src/main/java/com/crm/system/mapper/OpportunityStageMapper.java`
- [x] T044 创建商机阶段配置服务 `backend/crm-system/src/main/java/com/crm/system/service/OpportunityStageService.java`（CRUD、排序）
- [x] T045 创建商机阶段配置控制器 `backend/crm-system/src/main/java/com/crm/system/controller/OpportunityStageController.java`
- [x] T046 创建商机阶段配置页面 `frontend/src/views/system/config/opportunity-stage.vue`

**检查点**: 基础就绪 - 现在可以开始并行实施用户故事

---

## 阶段 3: 用户故事 1 - 系统基础设施搭建（优先级: P0）🎯 MVP

**目标**: 实现用户权限管理和组织架构，交付基础的系统访问能力

**独立测试**: 创建组织架构 → 添加用户 → 分配角色 → 验证权限控制

### 3.1 后端 - 实体层

- [x] T049 [P] [US1] 创建用户实体 `backend/crm-system/src/main/java/com/crm/system/entity/User.java`
- [x] T050 [P] [US1] 创建角色实体 `backend/crm-system/src/main/java/com/crm/system/entity/Role.java`
- [x] T051 [P] [US1] 创建权限实体 `backend/crm-system/src/main/java/com/crm/system/entity/Permission.java`
- [x] T052 [P] [US1] 创建部门实体 `backend/crm-system/src/main/java/com/crm/system/entity/Department.java`
- [x] T053 [P] [US1] 创建用户角色关联实体 `backend/crm-system/src/main/java/com/crm/system/entity/UserRole.java`
- [x] T054 [P] [US1] 创建角色权限关联实体 `backend/crm-system/src/main/java/com/crm/system/entity/RolePermission.java`

### 3.2 后端 - DTO 层

- [x] T048 [P] [US1] 创建登录请求 DTO `backend/crm-system/src/main/java/com/crm/system/dto/LoginRequest.java`
- [x] T049 [P] [US1] 创建登录响应 DTO `backend/crm-system/src/main/java/com/crm/system/dto/LoginResponse.java`
- [x] T050 [P] [US1] 创建用户 DTO `backend/crm-system/src/main/java/com/crm/system/dto/UserDTO.java`
- [x] T051 [P] [US1] 创建角色 DTO `backend/crm-system/src/main/java/com/crm/system/dto/RoleDTO.java`
- [x] T052 [P] [US1] 创建部门 DTO `backend/crm-system/src/main/java/com/crm/system/dto/DepartmentDTO.java`

### 3.3 后端 - Mapper 层

- [x] T053 [P] [US1] 创建用户 Mapper `backend/crm-system/src/main/java/com/crm/system/mapper/UserMapper.java`
- [x] T054 [P] [US1] 创建角色 Mapper `backend/crm-system/src/main/java/com/crm/system/mapper/RoleMapper.java`
- [x] T055 [P] [US1] 创建权限 Mapper `backend/crm-system/src/main/java/com/crm/system/mapper/PermissionMapper.java`
- [x] T056 [P] [US1] 创建部门 Mapper `backend/crm-system/src/main/java/com/crm/system/mapper/DepartmentMapper.java`

### 3.4 后端 - Service 层

- [x] T057 [US1] 创建认证服务 `backend/crm-system/src/main/java/com/crm/system/service/AuthService.java`（登录、登出、Token 刷新）
- [x] T058 [US1] 创建用户服务 `backend/crm-system/src/main/java/com/crm/system/service/UserService.java`（CRUD、分配角色）
- [x] T059 [US1] 创建角色服务 `backend/crm-system/src/main/java/com/crm/system/service/RoleService.java`（CRUD、分配权限）
- [x] T060 [US1] 创建权限服务 `backend/crm-system/src/main/java/com/crm/system/service/PermissionService.java`（CRUD、权限树）
- [x] T061 [US1] 创建部门服务 `backend/crm-system/src/main/java/com/crm/system/service/DepartmentService.java`（CRUD、部门树）

### 3.5 后端 - Controller 层

- [x] T062 [US1] 创建认证控制器 `backend/crm-system/src/main/java/com/crm/system/controller/AuthController.java`
- [x] T063 [US1] 创建用户控制器 `backend/crm-system/src/main/java/com/crm/system/controller/UserController.java`
- [x] T064 [US1] 创建角色控制器 `backend/crm-system/src/main/java/com/crm/system/controller/RoleController.java`
- [x] T065 [US1] 创建权限控制器 `backend/crm-system/src/main/java/com/crm/system/controller/PermissionController.java`
- [x] T066 [US1] 创建部门控制器 `backend/crm-system/src/main/java/com/crm/system/controller/DepartmentController.java`

### 3.6 前端 - API 层

- [x] T067 [P] [US1] 创建认证 API `frontend/src/api/system/auth.ts`
- [x] T068 [P] [US1] 创建用户 API `frontend/src/api/system/user.ts`
- [x] T069 [P] [US1] 创建角色 API `frontend/src/api/system/role.ts`
- [x] T070 [P] [US1] 创建权限 API `frontend/src/api/system/permission.ts`
- [x] T071 [P] [US1] 创建部门 API `frontend/src/api/system/department.ts`

### 3.7 前端 - 类型定义

- [x] T072 [P] [US1] 创建系统模块类型 `frontend/src/types/system.ts`（User、Role、Permission、Department）

### 3.8 前端 - 页面

- [x] T073 [US1] 创建登录页面 `frontend/src/views/login/index.vue`
- [x] T074 [US1] 创建用户管理页面 `frontend/src/views/system/user/index.vue`（列表、新增/编辑弹窗）
- [x] T075 [US1] 创建角色管理页面 `frontend/src/views/system/role/index.vue`（列表、权限分配）
- [x] T076 [US1] 创建部门管理页面 `frontend/src/views/system/department/index.vue`（树形结构）
- [x] T077 [US1] 创建权限管理页面 `frontend/src/views/system/permission/index.vue`（菜单树）

**检查点**: 用户故事 1 完成 - 系统管理员可以管理用户、角色、部门和权限

---

## 阶段 4: 用户故事 2 - 线索管理（优先级: P0）

**目标**: 实现线索全生命周期管理，交付线索录入、跟踪和转化能力

**独立测试**: 创建线索 → 更新状态 → 转化为客户 → 验证关联关系

### 4.1 后端 - 实体与 DTO

- [x] T078 [P] [US2] 创建线索实体 `backend/crm-business/src/main/java/com/crm/business/entity/Lead.java`
- [x] T079 [P] [US2] 创建线索 DTO `backend/crm-business/src/main/java/com/crm/business/dto/LeadDTO.java`
- [x] T080 [P] [US2] 创建线索查询 DTO `backend/crm-business/src/main/java/com/crm/business/dto/LeadQueryDTO.java`
- [x] T081 [P] [US2] 创建线索转化 DTO `backend/crm-business/src/main/java/com/crm/business/dto/LeadConvertDTO.java`

### 4.2 后端 - Mapper 与 Service

- [x] T082 [US2] 创建线索 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/LeadMapper.java`
- [x] T083 [US2] 创建线索服务 `backend/crm-business/src/main/java/com/crm/business/service/LeadService.java`（CRUD、状态流转、转化）
- [x] T084 [US2] 创建线索转化事件 `backend/crm-business/src/main/java/com/crm/business/event/LeadConvertedEvent.java`

### 4.3 后端 - Controller

- [x] T085 [US2] 创建线索控制器 `backend/crm-business/src/main/java/com/crm/business/controller/LeadController.java`

### 4.4 前端 - API 与类型

- [x] T086 [P] [US2] 创建线索 API `frontend/src/api/business/lead.ts`
- [x] T087 [P] [US2] 创建线索类型 `frontend/src/types/business/lead.ts`

### 4.5 前端 - 页面

- [x] T088 [US2] 创建线索列表页面 `frontend/src/views/business/lead/index.vue`
- [x] T089 [US2] 创建线索详情页面 `frontend/src/views/business/lead/detail.vue`
- [x] T090 [US2] 创建线索表单组件 `frontend/src/components/business/LeadForm.vue`
- [x] T091 [US2] 创建线索转化弹窗 `frontend/src/components/business/LeadConvertDialog.vue`

### 4.6 批量导入导出

- [x] T092 [P] [US2] 创建 Excel 导入导出工具类 `backend/crm-common/src/main/java/com/crm/common/utils/ExcelUtils.java`（支持 WPS）
- [x] T093 [US2] 在线索服务中添加批量导入方法 `backend/crm-business/src/main/java/com/crm/business/service/LeadService.java`
- [x] T094 [US2] 在线索服务中添加批量导出方法 `backend/crm-business/src/main/java/com/crm/business/service/LeadService.java`
- [x] T095 [US2] 在线索控制器中添加导入导出端点 `backend/crm-business/src/main/java/com/crm/business/controller/LeadController.java`
- [x] T096 [US2] 创建导入导出组件 `frontend/src/components/business/LeadImportExport.vue`

**检查点**: 用户故事 2 完成 - 销售人员可以管理线索全生命周期（含批量导入导出）

---

## 阶段 5: 用户故事 3 - 客户与联系人管理（优先级: P0）

**目标**: 实现客户和联系人管理，交付客户信息集中管理和 360 度视图能力

**独立测试**: 创建客户 → 添加联系人 → 查看 360 度视图 → 验证公海池机制

### 5.1 后端 - 实体与 DTO

- [ ] T097 [P] [US3] 创建客户实体 `backend/crm-business/src/main/java/com/crm/business/entity/Customer.java`
- [ ] T098 [P] [US3] 创建联系人实体 `backend/crm-business/src/main/java/com/crm/business/entity/Contact.java`
- [ ] T099 [P] [US3] 创建客户 DTO `backend/crm-business/src/main/java/com/crm/business/dto/CustomerDTO.java`
- [ ] T100 [P] [US3] 创建联系人 DTO `backend/crm-business/src/main/java/com/crm/business/dto/ContactDTO.java`
- [ ] T101 [P] [US3] 创建客户 360 度视图 DTO `backend/crm-business/src/main/java/com/crm/business/dto/Customer360DTO.java`

### 5.2 后端 - Mapper 与 Service

- [ ] T102 [P] [US3] 创建客户 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/CustomerMapper.java`
- [ ] T103 [P] [US3] 创建联系人 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/ContactMapper.java`
- [ ] T104 [US3] 创建客户服务 `backend/crm-business/src/main/java/com/crm/business/service/CustomerService.java`（CRUD、公海池、360视图）
- [ ] T105 [US3] 创建联系人服务 `backend/crm-business/src/main/java/com/crm/business/service/ContactService.java`（CRUD、设置主联系人）

### 5.3 后端 - Controller

- [ ] T106 [US3] 创建客户控制器 `backend/crm-business/src/main/java/com/crm/business/controller/CustomerController.java`
- [ ] T107 [US3] 创建联系人控制器 `backend/crm-business/src/main/java/com/crm/business/controller/ContactController.java`

### 5.4 前端 - API 与类型

- [ ] T108 [P] [US3] 创建客户 API `frontend/src/api/business/customer.ts`
- [ ] T109 [P] [US3] 创建联系人 API `frontend/src/api/business/contact.ts`
- [ ] T110 [P] [US3] 创建客户类型 `frontend/src/types/business/customer.ts`
- [ ] T111 [P] [US3] 创建联系人类型 `frontend/src/types/business/contact.ts`

### 5.5 前端 - 页面

- [ ] T112 [US3] 创建客户列表页面 `frontend/src/views/business/customer/index.vue`
- [ ] T113 [US3] 创建客户详情页面（360度视图）`frontend/src/views/business/customer/detail.vue`
- [ ] T114 [US3] 创建客户表单组件 `frontend/src/components/business/CustomerForm.vue`
- [ ] T115 [US3] 创建联系人列表组件 `frontend/src/components/business/ContactList.vue`
- [ ] T116 [US3] 创建联系人表单组件 `frontend/src/components/business/ContactForm.vue`
- [ ] T117 [US3] 创建公海池页面 `frontend/src/views/business/customer/pool.vue`

### 5.6 客户分类与标签

- [ ] T118 [P] [US3] 创建客户标签实体 `backend/crm-business/src/main/java/com/crm/business/entity/CustomerTag.java`
- [ ] T119 [P] [US3] 创建客户标签关联实体 `backend/crm-business/src/main/java/com/crm/business/entity/CustomerTagRelation.java`
- [ ] T120 [US3] 创建客户标签 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/CustomerTagMapper.java`
- [ ] T121 [US3] 创建客户标签服务 `backend/crm-business/src/main/java/com/crm/business/service/CustomerTagService.java`（CRUD、标签关联）
- [ ] T122 [US3] 在客户控制器中添加标签管理端点 `backend/crm-business/src/main/java/com/crm/business/controller/CustomerController.java`
- [ ] T123 [US3] 创建客户标签管理组件 `frontend/src/components/business/CustomerTagManager.vue`

**检查点**: 用户故事 3 完成 - 销售人员可以管理客户和联系人，查看 360 度视图（含标签管理）

---

## 阶段 6: 用户故事 4 - 商机管理（优先级: P0）

**目标**: 实现商机全流程跟踪，交付商机阶段推进和金额计算能力

**独立测试**: 创建商机 → 关联产品 → 推进阶段 → 转化为合同

### 6.1 后端 - 实体与 DTO

- [ ] T124 [P] [US4] 创建商机实体 `backend/crm-business/src/main/java/com/crm/business/entity/Opportunity.java`
- [ ] T125 [P] [US4] 创建商机产品关联实体 `backend/crm-business/src/main/java/com/crm/business/entity/OpportunityProduct.java`
- [ ] T126 [P] [US4] 创建商机 DTO `backend/crm-business/src/main/java/com/crm/business/dto/OpportunityDTO.java`
- [ ] T127 [P] [US4] 创建商机产品 DTO `backend/crm-business/src/main/java/com/crm/business/dto/OpportunityProductDTO.java`
- [ ] T128 [P] [US4] 创建阶段推进 DTO `backend/crm-business/src/main/java/com/crm/business/dto/StageAdvanceDTO.java`

### 6.2 后端 - Mapper 与 Service

- [ ] T129 [P] [US4] 创建商机 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/OpportunityMapper.java`
- [ ] T130 [P] [US4] 创建商机产品 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/OpportunityProductMapper.java`
- [ ] T131 [US4] 创建商机服务 `backend/crm-business/src/main/java/com/crm/business/service/OpportunityService.java`（CRUD、阶段推进、金额计算）
- [ ] T132 [US4] 创建商机赢单事件 `backend/crm-business/src/main/java/com/crm/business/event/OpportunityWonEvent.java`

### 6.3 后端 - Controller

- [ ] T133 [US4] 创建商机控制器 `backend/crm-business/src/main/java/com/crm/business/controller/OpportunityController.java`

### 6.4 前端 - API 与类型

- [ ] T134 [P] [US4] 创建商机 API `frontend/src/api/business/opportunity.ts`
- [ ] T135 [P] [US4] 创建商机类型 `frontend/src/types/business/opportunity.ts`
- [ ] T136 [P] [US4] 创建商机状态管理 `frontend/src/stores/opportunity.ts`

### 6.5 前端 - 页面

- [ ] T137 [US4] 创建商机列表页面 `frontend/src/views/business/opportunity/index.vue`
- [ ] T138 [US4] 创建商机详情页面 `frontend/src/views/business/opportunity/detail.vue`
- [ ] T139 [US4] 创建商机表单组件 `frontend/src/components/business/OpportunityForm.vue`
- [ ] T140 [US4] 创建商机产品选择组件 `frontend/src/components/business/OpportunityProductSelect.vue`
- [ ] T141 [US4] 创建商机阶段推进组件 `frontend/src/components/business/StageProgress.vue`

**检查点**: 用户故事 4 完成 - 销售人员可以管理商机全流程

---

## 阶段 7: 用户故事 5 - 产品管理（优先级: P0）

**目标**: 实现产品目录管理，交付标准化产品信息和定价能力

**独立测试**: 创建产品 → 设置价格 → 在商机中选择 → 验证金额计算

### 7.1 后端 - 实体与 DTO

- [ ] T142 [P] [US5] 创建产品实体 `backend/crm-business/src/main/java/com/crm/business/entity/Product.java`
- [ ] T143 [P] [US5] 创建产品 DTO `backend/crm-business/src/main/java/com/crm/business/dto/ProductDTO.java`

### 7.2 后端 - Mapper 与 Service

- [ ] T144 [US5] 创建产品 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/ProductMapper.java`
- [ ] T145 [US5] 创建产品服务 `backend/crm-business/src/main/java/com/crm/business/service/ProductService.java`（CRUD、启用/停用）

### 7.3 后端 - Controller

- [ ] T146 [US5] 创建产品控制器 `backend/crm-business/src/main/java/com/crm/business/controller/ProductController.java`

### 7.4 前端 - API 与类型

- [ ] T147 [P] [US5] 创建产品 API `frontend/src/api/business/product.ts`
- [ ] T148 [P] [US5] 创建产品类型 `frontend/src/types/business/product.ts`

### 7.5 前端 - 页面

- [ ] T149 [US5] 创建产品列表页面 `frontend/src/views/business/product/index.vue`
- [ ] T150 [US5] 创建产品表单组件 `frontend/src/components/business/ProductForm.vue`
- [ ] T151 [US5] 创建产品选择器组件 `frontend/src/components/business/ProductSelector.vue`（用于商机）

**检查点**: 用户故事 5 完成 - 产品管理员可以维护产品目录

---

## 阶段 8: 用户故事 6 - 合同管理（优先级: P0）

**目标**: 实现合同全生命周期管理，交付合同创建、文件上传和状态跟踪能力

**独立测试**: 从商机生成合同 → 上传文件 → 更新状态 → 验证关联关系

### 8.1 后端 - 实体与 DTO

- [ ] T152 [P] [US6] 创建合同实体 `backend/crm-business/src/main/java/com/crm/business/entity/Contract.java`
- [ ] T153 [P] [US6] 创建合同 DTO `backend/crm-business/src/main/java/com/crm/business/dto/ContractDTO.java`

### 8.2 后端 - Mapper 与 Service

- [ ] T154 [US6] 创建合同 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/ContractMapper.java`
- [ ] T155 [US6] 创建合同服务 `backend/crm-business/src/main/java/com/crm/business/service/ContractService.java`（CRUD、状态流转、文件上传）
- [ ] T156 [US6] 创建文件上传服务 `backend/crm-common/src/main/java/com/crm/common/service/FileService.java`
- [ ] T157 [US6] 创建合同签署事件 `backend/crm-business/src/main/java/com/crm/business/event/ContractSignedEvent.java`

### 8.3 后端 - Controller

- [ ] T158 [US6] 创建合同控制器 `backend/crm-business/src/main/java/com/crm/business/controller/ContractController.java`

### 8.4 前端 - API 与类型

- [ ] T159 [P] [US6] 创建合同 API `frontend/src/api/business/contract.ts`
- [ ] T160 [P] [US6] 创建合同类型 `frontend/src/types/business/contract.ts`

### 8.5 前端 - 页面

- [ ] T161 [US6] 创建合同列表页面 `frontend/src/views/business/contract/index.vue`
- [ ] T162 [US6] 创建合同详情页面 `frontend/src/views/business/contract/detail.vue`
- [ ] T163 [US6] 创建合同表单组件 `frontend/src/components/business/ContractForm.vue`
- [ ] T164 [US6] 创建文件上传组件 `frontend/src/components/common/FileUpload.vue`

**检查点**: 用户故事 6 完成 - 销售人员可以管理合同全生命周期

---

## 阶段 9: 用户故事 7 - 回款管理（优先级: P0）

**目标**: 实现回款计划和实际回款管理，交付现金流监控能力，完成 L2C 闭环

**独立测试**: 创建回款计划 → 记录实际回款 → 查看回款统计 → 验证逾期提醒

### 9.1 后端 - 实体与 DTO

- [ ] T165 [P] [US7] 创建回款计划实体 `backend/crm-business/src/main/java/com/crm/business/entity/PaymentPlan.java`
- [ ] T166 [P] [US7] 创建回款记录实体 `backend/crm-business/src/main/java/com/crm/business/entity/PaymentRecord.java`
- [ ] T167 [P] [US7] 创建回款计划 DTO `backend/crm-business/src/main/java/com/crm/business/dto/PaymentPlanDTO.java`
- [ ] T168 [P] [US7] 创建回款记录 DTO `backend/crm-business/src/main/java/com/crm/business/dto/PaymentRecordDTO.java`

### 9.2 后端 - Mapper 与 Service

- [ ] T169 [P] [US7] 创建回款计划 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/PaymentPlanMapper.java`
- [ ] T170 [P] [US7] 创建回款记录 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/PaymentRecordMapper.java`
- [ ] T171 [US7] 创建回款计划服务 `backend/crm-business/src/main/java/com/crm/business/service/PaymentPlanService.java`（CRUD、状态更新、逾期检查）
- [ ] T172 [US7] 创建回款记录服务 `backend/crm-business/src/main/java/com/crm/business/service/PaymentRecordService.java`（CRUD、自动更新计划状态）

### 9.3 后端 - Controller

- [ ] T173 [US7] 创建回款计划控制器 `backend/crm-business/src/main/java/com/crm/business/controller/PaymentPlanController.java`
- [ ] T174 [US7] 创建回款记录控制器 `backend/crm-business/src/main/java/com/crm/business/controller/PaymentRecordController.java`

### 9.4 前端 - API 与类型

- [ ] T175 [P] [US7] 创建回款 API `frontend/src/api/business/payment.ts`
- [ ] T176 [P] [US7] 创建回款类型 `frontend/src/types/business/payment.ts`

### 9.5 前端 - 页面

- [ ] T177 [US7] 创建回款管理页面 `frontend/src/views/business/payment/index.vue`
- [ ] T178 [US7] 创建回款计划列表组件 `frontend/src/components/business/PaymentPlanList.vue`
- [ ] T179 [US7] 创建回款记录表单组件 `frontend/src/components/business/PaymentRecordForm.vue`
- [ ] T180 [US7] 创建应收账款报表组件 `frontend/src/components/business/ReceivableReport.vue`

**检查点**: 用户故事 7 完成 - L2C 业务闭环完成！财务人员可以管理回款全流程

---

## 阶段 10: 用户故事 8 - 跟进记录管理（优先级: P1）

**目标**: 实现销售活动追踪，交付沟通历史记录和时间线展示能力

**独立测试**: 在客户下创建跟进记录 → 选择跟进类型 → 查看时间线

### 10.1 后端 - 实体与 DTO

- [ ] T181 [P] [US8] 创建跟进记录实体 `backend/crm-business/src/main/java/com/crm/business/entity/Activity.java`
- [ ] T182 [P] [US8] 创建跟进记录 DTO `backend/crm-business/src/main/java/com/crm/business/dto/ActivityDTO.java`

### 10.2 后端 - Mapper 与 Service

- [ ] T183 [US8] 创建跟进记录 Mapper `backend/crm-business/src/main/java/com/crm/business/mapper/ActivityMapper.java`
- [ ] T184 [US8] 创建跟进记录服务 `backend/crm-business/src/main/java/com/crm/business/service/ActivityService.java`（CRUD、按对象查询）

### 10.3 后端 - Controller

- [ ] T185 [US8] 创建跟进记录控制器 `backend/crm-business/src/main/java/com/crm/business/controller/ActivityController.java`

### 10.4 前端 - API 与类型

- [ ] T186 [P] [US8] 创建跟进记录 API `frontend/src/api/business/activity.ts`
- [ ] T187 [P] [US8] 创建跟进记录类型 `frontend/src/types/business/activity.ts`

### 10.5 前端 - 组件

- [ ] T188 [US8] 创建跟进记录时间线组件 `frontend/src/components/business/ActivityTimeline.vue`
- [ ] T189 [US8] 创建跟进记录表单组件 `frontend/src/components/business/ActivityForm.vue`
- [ ] T190 [US8] 将跟进记录集成到客户详情页 `frontend/src/views/business/customer/detail.vue`（添加跟进记录 Tab）
- [ ] T191 [US8] 将跟进记录集成到商机详情页 `frontend/src/views/business/opportunity/detail.vue`（添加跟进记录 Tab）

**检查点**: 用户故事 8 完成 - 销售人员可以记录和查看跟进历史

---

## 阶段 11: 用户故事 9 - 数据分析与仪表盘（优先级: P1）

**目标**: 实现销售数据可视化，交付管理决策支持能力

**独立测试**: 访问仪表盘 → 切换时间范围 → 下钻查看明细 → 导出报表

### 11.1 后端 - DTO 与 Service

- [ ] T192 [P] [US9] 创建仪表盘数据 DTO `backend/crm-business/src/main/java/com/crm/business/dto/DashboardDTO.java`
- [ ] T193 [P] [US9] 创建销售漏斗 DTO `backend/crm-business/src/main/java/com/crm/business/dto/SalesFunnelDTO.java`
- [ ] T194 [P] [US9] 创建业绩趋势 DTO `backend/crm-business/src/main/java/com/crm/business/dto/PerformanceTrendDTO.java`
- [ ] T195 [US9] 创建仪表盘服务 `backend/crm-business/src/main/java/com/crm/business/service/DashboardService.java`（汇总统计、漏斗、趋势）
- [ ] T196 [US9] 创建报表导出服务 `backend/crm-business/src/main/java/com/crm/business/service/ReportExportService.java`

### 11.2 后端 - Controller

- [ ] T197 [US9] 创建仪表盘控制器 `backend/crm-business/src/main/java/com/crm/business/controller/DashboardController.java`

### 11.3 前端 - API 与类型

- [ ] T198 [P] [US9] 创建仪表盘 API `frontend/src/api/business/dashboard.ts`
- [ ] T199 [P] [US9] 创建仪表盘类型 `frontend/src/types/business/dashboard.ts`

### 11.4 前端 - 页面与组件

- [ ] T200 [US9] 创建仪表盘页面 `frontend/src/views/business/dashboard/index.vue`
- [ ] T201 [US9] 创建销售漏斗图组件 `frontend/src/components/business/SalesFunnelChart.vue`
- [ ] T202 [US9] 创建业绩趋势图组件 `frontend/src/components/business/PerformanceTrendChart.vue`
- [ ] T203 [US9] 创建核心指标卡片组件 `frontend/src/components/business/MetricCard.vue`
- [ ] T204 [US9] 创建时间范围选择器组件 `frontend/src/components/common/DateRangePicker.vue`

**检查点**: 用户故事 9 完成 - 销售经理可以查看数据分析和业绩指标

---

## 阶段 12: 用户故事 10 - AI 智能辅助（优先级: P2，可选模块）

**目标**: 实现 AI 辅助销售能力，交付智能创建和查询功能

**模块状态**: `crm-ai` 模块初期可以不启动，系统核心功能不依赖此模块。可根据实际需要选择性实施。

**独立测试**: 通过 AI 描述创建线索 → 智能查重提示 → 自然语言查询

### 12.1 后端 - AI 模块

- [ ] T205 [P] [US10] 创建 AI 模块 POM `backend/crm-ai/pom.xml`
- [ ] T206 [P] [US10] 创建 OpenAI 客户端封装 `backend/crm-ai/src/main/java/com/crm/ai/client/OpenAIClient.java`
- [ ] T207 [P] [US10] 创建 AI 服务接口 `backend/crm-ai/src/main/java/com/crm/ai/service/AiService.java`
- [ ] T208 [US10] 创建智能创建服务 `backend/crm-ai/src/main/java/com/crm/ai/service/SmartCreateService.java`
- [ ] T209 [US10] 创建智能查重服务 `backend/crm-ai/src/main/java/com/crm/ai/service/DuplicateCheckService.java`
- [ ] T210 [US10] 创建自然语言查询服务 `backend/crm-ai/src/main/java/com/crm/ai/service/NLQueryService.java`

### 12.2 后端 - MCP Server（预留）

- [ ] T211 [US10] 创建 MCP 工具定义 `backend/crm-ai/src/main/java/com/crm/ai/mcp/CrmTools.java`
- [ ] T212 [US10] 创建 MCP Server 端点 `backend/crm-ai/src/main/java/com/crm/ai/mcp/McpServerController.java`

### 12.3 后端 - Controller

- [ ] T213 [US10] 创建 AI 控制器 `backend/crm-ai/src/main/java/com/crm/ai/controller/AiController.java`

### 12.4 前端 - API 与类型

- [ ] T214 [P] [US10] 创建 AI API `frontend/src/api/ai/index.ts`
- [ ] T215 [P] [US10] 创建 AI 类型 `frontend/src/types/ai.ts`

### 12.5 前端 - 组件

- [ ] T216 [US10] 创建 AI 助手对话框组件 `frontend/src/components/ai/AiAssistantDialog.vue`
- [ ] T217 [US10] 创建智能查重提示组件 `frontend/src/components/ai/DuplicateAlert.vue`
- [ ] T218 [US10] 将 AI 助手集成到主布局 `frontend/src/layouts/MainLayout.vue`（添加 AI 入口）

**检查点**: 用户故事 10 完成 - 销售人员可以使用 AI 辅助功能

---

## 阶段 13: 用户故事 11 - 第三方平台集成（优先级: P3）

**目标**: 预留第三方平台集成接口，交付通知和协作能力

**独立测试**: 配置集成 → 触发通知 → 验证消息到达

### 13.1 后端 - 集成模块

- [ ] T219 [P] [US11] 创建通知服务接口 `backend/crm-common/src/main/java/com/crm/common/service/NotificationService.java`
- [ ] T220 [P] [US11] 创建企业微信集成服务 `backend/crm-business/src/main/java/com/crm/business/integration/WechatWorkService.java`
- [ ] T221 [P] [US11] 创建钉钉集成服务 `backend/crm-business/src/main/java/com/crm/business/integration/DingTalkService.java`
- [ ] T222 [P] [US11] 创建飞书集成服务 `backend/crm-business/src/main/java/com/crm/business/integration/FeishuService.java`
- [ ] T223 [US11] 创建集成配置管理 `backend/crm-business/src/main/java/com/crm/business/service/IntegrationConfigService.java`

### 13.2 后端 - Controller

- [ ] T224 [US11] 创建集成配置控制器 `backend/crm-business/src/main/java/com/crm/business/controller/IntegrationController.java`

### 13.3 前端 - API 与页面

- [ ] T225 [P] [US11] 创建集成配置 API `frontend/src/api/system/integration.ts`
- [ ] T226 [US11] 创建集成配置页面 `frontend/src/views/system/integration/index.vue`

**检查点**: 用户故事 11 完成 - 系统预留第三方集成能力

---

## 阶段 14: 完善与横切关注点

**目的**: 影响多个用户故事的改进

### 14.1 操作日志

- [ ] T227 [P] 创建操作日志实体 `backend/crm-system/src/main/java/com/crm/system/entity/OperationLog.java`
- [ ] T228 [P] 创建操作日志切面 `backend/crm-common/src/main/java/com/crm/common/aspect/OperationLogAspect.java`
- [ ] T229 创建操作日志查询页面 `frontend/src/views/system/log/index.vue`

### 14.2 数据权限

- [ ] T230 实现数据权限拦截器 `backend/crm-common/src/main/java/com/crm/common/interceptor/DataScopeInterceptor.java`
- [ ] T231 在业务服务中应用数据权限（线索、客户、商机等）

### 14.3 代码清理与优化

- [ ] T232 统一异常处理和错误码定义
- [ ] T233 API 响应格式验证和 Swagger 文档完善
- [ ] T234 前端组件代码重构和通用组件提取
- [ ] T235 性能优化：添加必要的数据库索引
- [ ] T236 安全加固：XSS 防护、SQL 注入防护检查

### 14.5 验证与文档

- [ ] T237 运行 quickstart.md 验证完整部署流程
- [ ] T238 更新 API 文档（Knife4j 注解完善）

---

## 依赖关系与执行顺序

### 阶段依赖关系

```
阶段 1: 设置 ─────────────────────────────────────────────┐
    │                                                     │
    ↓                                                     │
阶段 2: 基础 ─────────────────────────────────────────────┤
    │                                                     │
    ├──→ 阶段 3: US1 系统基础设施 (P0) 🎯 MVP            │
    │         │                                           │
    │         ↓                                           │
    ├──→ 阶段 4: US2 线索管理 (P0) ←───────────────────┐ │
    │         │                                        │ │
    │         ↓                                        │ │
    ├──→ 阶段 5: US3 客户与联系人 (P0) ←───────────────┤ │
    │         │                                        │ │
    │         ↓                                        │ │
    ├──→ 阶段 6: US4 商机管理 (P0) ←───────────────────┤ │
    │         │                                        │ │
    │         ↓                                        │ │
    ├──→ 阶段 7: US5 产品管理 (P0) ←───────────────────┤ │
    │         │                                        │ │
    │         ↓                                        │ │
    ├──→ 阶段 8: US6 合同管理 (P0) ←───────────────────┤ │
    │         │                                        │ │
    │         ↓                                        │ │
    └──→ 阶段 9: US7 回款管理 (P0) ←───────── L2C 闭环 ┘ │
              │                                           │
              ↓                                           │
         阶段 10: US8 跟进记录 (P1) ──────────────────────┤
              │                                           │
              ↓                                           │
         阶段 11: US9 仪表盘 (P1) ────────────────────────┤
              │                                           │
              ↓                                           │
         阶段 12: US10 AI 智能 (P2) ──────────────────────┤
              │                                           │
              ↓                                           │
         阶段 13: US11 第三方集成 (P3) ───────────────────┤
              │                                           │
              ↓                                           │
         阶段 14: 完善 ←──────────────────────────────────┘
```

### 用户故事依赖关系

| 用户故事 | 依赖 | 说明 |
|----------|------|------|
| US1 系统基础 | 阶段 2 基础 | 无其他故事依赖，MVP 核心 |
| US2 线索 | US1 | 需要用户认证和权限 |
| US3 客户 | US1, US2 | 线索转化为客户 |
| US4 商机 | US3, US5 | 需要客户和产品 |
| US5 产品 | US1 | 仅需系统基础 |
| US6 合同 | US4 | 从商机生成合同 |
| US7 回款 | US6 | 合同关联回款 |
| US8 跟进 | US2, US3, US4 | 可关联线索/客户/商机 |
| US9 仪表盘 | US2-US7 | 汇总所有业务数据 |
| US10 AI | US2, US3 | AI 创建线索/客户 |
| US11 集成 | US1 | 仅需系统配置 |

### 并行机会

**阶段 1 并行**:
- T002-T005: 所有模块 POM 可并行创建
- T007-T008: 代码检查配置可并行

**阶段 2 并行**:
- T013-T020: 公共模块组件可并行
- T029-T034: 前端基础架构可并行

**用户故事内并行**:
- 每个故事的实体和 DTO 可并行创建
- 前端 API 和类型可并行创建
- 后端和前端开发可并行（API 契约优先）

---

## 并行示例: 用户故事 1

```bash
# 一起启动所有实体创建:
任务: T035 "创建用户实体"
任务: T036 "创建角色实体"
任务: T037 "创建权限实体"
任务: T038 "创建部门实体"
任务: T039 "创建用户角色关联实体"
任务: T040 "创建角色权限关联实体"

# 一起启动所有 DTO 创建:
任务: T041-T045 "创建各类 DTO"

# 一起启动所有 Mapper 创建:
任务: T046-T049 "创建各类 Mapper"

# 一起启动前端 API 和类型:
任务: T060-T065 "创建前端 API 和类型"
```

---

## 实施策略

### 仅 MVP（用户故事 1）

1. 完成阶段 1: 设置
2. 完成阶段 2: 基础（关键 - 阻塞所有故事）
3. 完成阶段 3: 用户故事 1
4. **停止并验证**: 独立测试用户故事 1
5. 如准备好则部署/演示

### P0 完整交付（L2C 闭环）

1. 完成设置 + 基础 → 基础就绪
2. 完成 US1 → 系统可访问
3. 完成 US2-US7 → L2C 业务闭环完成
4. **停止并验证**: 完整业务流程测试
5. 部署生产环境

### 增量交付

1. 阶段 1-3 → MVP（系统基础）
2. 添加 US2 → 线索管理可用
3. 添加 US3 → 客户管理可用
4. 添加 US4-US7 → L2C 闭环
5. 添加 US8-US9 → 业务增强
6. 添加 US10-US11 → 高级功能

### 并行团队策略

有多个开发人员时:

1. 团队一起完成设置 + 基础
2. 基础完成后:
   - 后端开发 A: US1 后端
   - 前端开发 A: US1 前端
   - 后端开发 B: US2 后端（API 契约先行）
   - 前端开发 B: US2 前端（Mock API）
3. 故事独立完成和集成

---

## 任务统计

| 阶段 | 用户故事 | 任务数 | 并行任务 |
|------|----------|--------|----------|
| 阶段 1 | 设置 | 8 | 6 |
| 阶段 2 | 基础 | 38 | 20 |
| 阶段 3 | US1 系统基础 | 36 | 21 |
| 阶段 4 | US2 线索 | 19 | 7 |
| 阶段 5 | US3 客户 | 27 | 13 |
| 阶段 6 | US4 商机 | 18 | 10 |
| 阶段 7 | US5 产品 | 10 | 4 |
| 阶段 8 | US6 合同 | 13 | 4 |
| 阶段 9 | US7 回款 | 16 | 8 |
| 阶段 10 | US8 跟进 | 11 | 4 |
| 阶段 11 | US9 仪表盘 | 13 | 5 |
| 阶段 12 | US10 AI (可选) | 14 | 5 |
| 阶段 13 | US11 集成 | 8 | 5 |
| 阶段 14 | 完善 | 12 | 2 |
| **总计** | | **243** | **114** |

---

## 注意事项

- [P] 任务 = 不同文件，无依赖关系，可并行执行
- [Story] 标签将任务映射到特定用户故事以实现可追溯性
- 每个用户故事应该独立可完成和可测试
- 在每个任务或逻辑组后提交
- 在任何检查点停止以独立验证故事
- 避免: 模糊任务、相同文件冲突、破坏独立性的跨故事依赖
- P0 用户故事（US1-US7）是 L2C 核心，优先完成
- P1 用户故事（US8-US9）是业务增强，建议完成
- P2/P3 用户故事（US10-US11）可根据资源情况选择实现
