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

## 阶段 4：线索管理

### 修复 1：前后端线索字段名不匹配

**问题描述**：
- 前端使用字段：`leadName`, `contactName`, `companyName`
- 后端实体字段：`name`, `company`（没有 `contactName` 字段）
- 数据库表字段：`name`, `phone`, `company`

**修复文件**：
1. `frontend/src/types/business.ts` - 修改 Lead 接口定义
2. `frontend/src/components/business/LeadForm.vue` - 修改线索表单组件
3. `frontend/src/views/business/lead/index.vue` - 修改线索列表页面

**修复内容**：

#### 1. 修改前端 Lead 类型定义

```typescript
// 修改前
export interface Lead {
  id: number
  leadName: string
  contactName: string
  companyName: string
  phone: string
  email: string
  source: number
  status: number
  rating: number
}

// 修改后
export interface Lead {
  id: number
  name: string          // 与后端实体保持一致
  phone: string
  email: string
  company: string       // 与后端实体保持一致
  source: number
  status: number
  rating: number
  ownerId: number
  ownerName: string
  createTime: string
  updateTime: string
}
```

#### 2. 修改 LeadQueryParams

```typescript
// 修改前
export interface LeadQueryParams {
  name?: string
  source?: number
  status?: number
  rating?: number
}

// 修改后
export interface LeadQueryParams {
  keyword?: string      // 后端使用 keyword 进行模糊搜索
  source?: number
  status?: number
}
```

---

### 修复 2：LeadForm 表单默认值被覆盖

**问题描述**：
- 表单初始化时设置了默认值（`status: 1`, `rating: 2`）
- 但 `watch` 函数在 `props.formData` 变化时用空对象覆盖了默认值
- 导致表单验证失败

**修复文件**：
1. `frontend/src/components/business/LeadForm.vue`

**修复内容**：

```typescript
// 修改前
watch(() => props.formData, (newVal) => {
  Object.assign(formModel, newVal)
})

// 修改后
watch(() => props.formData, (newVal) => {
  if (newVal && Object.keys(newVal).length > 0) {
    Object.assign(formModel, newVal)
  }
})
```

#### 修改验证规则，添加数字类型支持

```typescript
// 为数字类型字段添加 type: 'number'
const rules = {
  status: { required: true, type: 'number', message: '请选择状态', trigger: 'change' },
  rating: { required: true, type: 'number', message: '请选择评级', trigger: 'change' }
}
```

---

### 修复 3：前端 API 响应数据处理错误

**问题描述**：
- `request.get` 返回 `Result<T>` 格式：`{ code, msg, data }`
- 但 `lead.ts` 中的 API 函数声明返回 `PageResult<Lead>`，没有正确提取 `data` 字段
- 导致前端无法正确处理分页数据

**修复文件**：
1. `frontend/src/api/business/lead.ts`

**修复内容**：

```typescript
// 修改前
export function pageLeads(params: LeadQueryParams): Promise<PageResult<Lead>> {
  return request.get('/business/lead/list', { params })
}

// 修改后
export async function pageLeads(params: LeadQueryParams): Promise<PageResult<Lead>> {
  const result = await request.get<PageResult<Lead>>('/business/lead/list', { params })
  return result.data
}
```

---

### 修复 4：CORS 跨域问题

**问题描述**：
- 通过外部 URL 访问前端时，vite 代理不工作
- 后端没有配置 CORS，导致跨域请求被阻止
- Spring Security 拦截了 OPTIONS 预检请求，返回 401

**修复文件**：
1. `frontend/.env.development` - 配置前端直接访问后端
2. `backend/crm-admin/src/main/java/com/crm/config/CorsConfig.java` - 新建 CORS 配置类
3. `backend/crm-admin/src/main/java/com/crm/config/SecurityConfig.java` - 修改 Security 配置

**修复内容**：

#### 1. 修改前端环境变量

```env
# 修改前
VITE_API_BASE_URL=/api

# 修改后
VITE_API_BASE_URL=https://8080-i62m6mhndi6dolslh9963-35002421.sg1.manus.computer
```

#### 2. 创建 CORS 配置类

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

#### 3. 修改 SecurityConfig，启用 CORS 并放行 OPTIONS 请求

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
        // 启用 CORS
        .cors().configurationSource(corsConfigurationSource())
        .and()
        
        // 配置请求授权规则
        .authorizeRequests()
        // 放行所有 OPTIONS 请求（CORS 预检请求）
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        // ... 其他配置
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOriginPattern("*");
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

## 已知问题状态

| ID | 问题描述 | 状态 |
|----|---------|------|
| I-001 | RoleDTO 字段与 Role 实体不匹配 | ✅ 已修复 |
| I-002 | 用户分配角色接口请求体格式不匹配 | ✅ 已修复 |
| I-003 | 字典 API 路径不匹配 | ✅ 已修复 |
| I-004 | Lead 字段名不匹配 | ✅ 已修复 |
| I-005 | LeadForm 默认值被覆盖 | ✅ 已修复 |
| I-006 | API 响应数据处理错误 | ✅ 已修复 |
| I-007 | CORS 跨域问题 | ✅ 已修复 |

---

## 待测试阶段

- **阶段 5**：用户故事 3 - 客户与联系人管理
- **阶段 6**：用户故事 4 - 商机管理
- **阶段 7**：用户故事 5 - 产品管理
- **阶段 8**：用户故事 6 - 合同管理
- **阶段 9**：用户故事 7 - 回款管理
- **阶段 10**：用户故事 8 - 跟进记录
- **阶段 11**：用户故事 9 - 仪表盘与报表
- **阶段 12-14**：AI 智能、第三方集成、完善

---

## 修复总结

### 主要问题类型

1. **前后端字段名不匹配**：统一使用后端字段名，修改前端类型定义和组件
2. **API 路径不匹配**：确保前后端 API 路径一致，添加必要的前缀
3. **表单默认值处理**：修复 watch 函数逻辑，避免覆盖默认值
4. **CORS 跨域配置**：配置后端 CORS 支持，放行 OPTIONS 预检请求
5. **响应数据处理**：正确提取 API 响应中的 data 字段

### 最佳实践

1. **保持业务完整性**：修复过程中不删减任何业务逻辑
2. **统一字段命名**：前后端使用一致的字段名，避免映射错误
3. **完整测试**：每次修改后通过浏览器和 API 测试验证
4. **分阶段提交**：每完成一个阶段的修复后提交代码

---

*最后更新：2026-01-22*


---

## 阶段 5-14：所有业务模块 API 修复

### 修复：所有业务 API 响应数据处理错误

**问题描述**：
所有业务 API 文件都存在相同的问题：
- 直接返回 `request.get()` 的结果，没有正确提取 `data` 字段
- 部分 API 路径以 `/api/` 开头，与 `VITE_API_BASE_URL` 中的 `/api` 重复

**修复文件**：
1. `frontend/src/api/business/customer.ts` - 修复客户 API
2. `frontend/src/api/business/contact.ts` - 修复联系人 API
3. `frontend/src/api/business/opportunity.ts` - 修复商机 API
4. `frontend/src/api/business/product.ts` - 修复产品 API
5. `frontend/src/api/business/contract.ts` - 修复合同 API
6. `frontend/src/api/business/payment.ts` - 修复回款 API
7. `frontend/src/api/business/activity.ts` - 修复活动 API
8. `frontend/src/api/business/dashboard.ts` - 修复仪表盘 API
9. `frontend/src/api/ai/index.ts` - 修复 AI API（移除重复的 /api 前缀）

**修复内容**：

#### 1. 修复分页查询函数

```typescript
// 修改前
export function pageCustomers(params: CustomerQueryParams): Promise<PageResult<Customer>> {
  return request.get('/business/customer/list', { params })
}

// 修改后
export async function pageCustomers(params: CustomerQueryParams): Promise<PageResult<Customer>> {
  const result = await request.get<PageResult<Customer>>('/business/customer/list', { params })
  return result.data
}
```

#### 2. 修复单个实体查询函数

```typescript
// 修改前
export function getCustomer(id: string): Promise<Customer> {
  return request.get(`/business/customer/${id}`)
}

// 修改后
export async function getCustomer(id: string): Promise<Customer> {
  const result = await request.get<Customer>(`/business/customer/${id}`)
  return result.data
}
```

#### 3. 修复 AI API 路径重复问题

```typescript
// 修改前
export function getAiStatus(): Promise<AiStatus> {
  return request.get('/api/ai/status')  // 路径重复 /api
}

// 修改后
export async function getAiStatus(): Promise<AiStatus> {
  const result = await request.get<AiStatus>('/ai/status')  // 移除重复的 /api
  return result.data
}
```

---

### TypeScript 编译错误修复

**问题描述**：
修改 API 文件后，部分 Vue 组件出现 TypeScript 编译错误：
1. `LeadConvertDialog.vue` 使用了旧的字段名
2. `lead/detail.vue` 使用了旧的字段名
3. `lead/index.vue` 状态比较类型不正确
4. `role/index.vue` DataScope 类型使用不正确

**修复文件**：
1. `frontend/src/components/business/LeadConvertDialog.vue`
2. `frontend/src/views/business/lead/detail.vue`
3. `frontend/src/views/business/lead/index.vue`
4. `frontend/src/views/system/role/index.vue`

**修复内容**：

#### 1. 修复 LeadConvertDialog.vue

```typescript
// 修改前
formModel.companyName = props.lead.companyName
formModel.contactName = props.lead.contactName

// 修改后
formModel.companyName = props.lead.company
// 移除 contactName（后端没有此字段）
```

#### 2. 修复 lead/detail.vue

```typescript
// 修改前
<span>{{ lead.leadName }}</span>
<span>{{ lead.companyName }}</span>

// 修改后
<span>{{ lead.name }}</span>
<span>{{ lead.company }}</span>
```

#### 3. 修复 lead/index.vue 状态比较

```typescript
// 修改前
row.status !== LeadStatus.CONVERTED

// 修改后
row.status !== Number(LeadStatus.CONVERTED)
```

#### 4. 修复 role/index.vue DataScope 类型

```typescript
// 修改前
formData.dataScope = DataScope.ALL

// 修改后
formData.dataScope = DataScope.ALL as number
```

---

## 待修复问题

### 客户管理新增弹窗不显示

**问题描述**：
点击"新增客户"按钮后，弹窗没有显示。

**初步分析**：
- 后端 API 正常工作（通过 curl 测试成功创建客户）
- 前端 TypeScript 编译通过
- Vue 组件事件绑定可能有问题
- 可能是 n-modal 组件渲染问题

**待进一步调查**：
- 检查 CustomerForm 组件是否正确导入
- 检查 n-modal 组件的 show 属性绑定
- 检查 Vue 组件是否有运行时错误

---

*最后更新：2026-01-22*
