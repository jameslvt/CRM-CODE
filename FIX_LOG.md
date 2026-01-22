# CRM 系统修复记录

## 修复日期：2026-01-22

---

## 阶段 0：环境配置修复

### 修复内容
1. **vite.config.ts** - 添加 allowedHosts 配置以支持外部访问
   - 添加 `host: '0.0.0.0'`
   - 添加 `allowedHosts: ['.manus.computer', 'localhost']`

---

## 阶段 3：系统基础设施（用户、角色、权限、部门）

### 修复 1：角色字段名不匹配（I-001）

**问题描述**：
- 后端 RoleDTO 返回字段：`roleCode`, `roleName`, `sortOrder`
- 前端 Role 类型期望字段：`code`, `name`, `sort`
- 导致前端无法正确显示角色数据

**修复文件**：
1. `frontend/src/types/system.ts` - 修改 Role 接口定义
2. `frontend/src/views/system/role/index.vue` - 修改角色管理页面

**修复内容**：

#### 1. 修改前端 Role 类型定义

```typescript
// 修改前
export interface Role {
  id: number
  code: string
  name: string
  sort: number
}

// 修改后
export interface Role {
  id: number
  roleCode: string      // 与后端 RoleDTO 保持一致
  roleName: string      // 与后端 RoleDTO 保持一致
  sortOrder: number     // 与后端 RoleDTO 保持一致
}
```

#### 2. 修改角色管理页面

- 表单数据字段：`name` → `roleName`, `code` → `roleCode`, `sort` → `sortOrder`
- 表单验证规则：更新对应字段名
- 表格列配置：更新 key 值
- 编辑/重置函数：更新字段映射

---

### 修复 2：用户分配角色请求体格式不匹配（I-002）

**问题描述**：
- 前端发送格式：`{ roleIds: [1, 2, 3] }`
- 后端期望格式：`[1, 2, 3]`（直接数组）

**修复文件**：
1. `frontend/src/api/system/user.ts`

**修复内容**：

```typescript
// 修改前
export function assignRoles(params: AssignRolesParams): Promise<Result<void>> {
  return request.post(`/system/users/${params.userId}/roles`, {
    roleIds: params.roleIds
  })
}

// 修改后
export function assignRoles(params: AssignRolesParams): Promise<Result<void>> {
  return request.post(`/system/users/${params.userId}/roles`, params.roleIds)
}
```

---

### 修复 3：角色分配权限请求体格式不匹配

**问题描述**：
- 前端发送格式：`{ permissionIds: [1, 2, 3] }`
- 后端期望格式：`[1, 2, 3]`（直接数组）

**修复文件**：
1. `frontend/src/api/system/role.ts`

**修复内容**：

```typescript
// 修改前
export function assignPermissions(params: AssignPermissionsParams): Promise<Result<void>> {
  return request.post(`/system/roles/${params.roleId}/permissions`, {
    permissionIds: params.permissionIds
  })
}

// 修改后
export function assignPermissions(params: AssignPermissionsParams): Promise<Result<void>> {
  return request.post(`/system/roles/${params.roleId}/permissions`, params.permissionIds)
}
```

---

### 修复 4：字典 API 路径缺少 /api 前缀（I-003）

**问题描述**：
- 后端 DictController 路径：`/system/dict`
- 前端请求路径通过 baseURL `/api` 拼接为 `/api/system/dict`
- 实际后端路径应为：`/api/system/dict`

**修复文件**：
1. `backend/crm-system/src/main/java/com/crm/system/controller/DictController.java`

**修复内容**：

```java
// 修改前
@RequestMapping("/system/dict")
public class DictController {

// 修改后
@RequestMapping("/api/system/dict")
public class DictController {
```

---

## 已知问题状态

| ID | 问题描述 | 状态 |
|----|---------|------|
| I-001 | RoleDTO 字段与 Role 实体不匹配 | ✅ 已修复 |
| I-002 | 用户分配角色接口请求体格式不匹配 | ✅ 已修复 |
| I-003 | 字典 API 路径不匹配 | ✅ 已修复 |

---

## 待测试阶段

- **阶段 4**：用户故事 2 - 线索管理
- **阶段 5**：用户故事 3 - 客户与联系人管理
- **阶段 6**：用户故事 4 - 商机管理
- **阶段 7**：用户故事 5 - 产品管理
- **阶段 8**：用户故事 6 - 合同管理
- **阶段 9**：用户故事 7 - 回款管理
- **阶段 10**：用户故事 8 - 跟进记录
- **阶段 11**：用户故事 9 - 仪表盘与报表
- **阶段 12-14**：AI 智能、第三方集成、完善

---

*最后更新：2026-01-22*
