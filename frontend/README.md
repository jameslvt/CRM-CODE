# CRM 系统 - 前端

企业级 CRM 系统的前端应用，基于 Vue 3 + TypeScript + Naive UI 构建。

## 技术栈

- **框架**: Vue 3 (Composition API)
- **语言**: TypeScript
- **UI 组件库**: Naive UI
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **构建工具**: Vite
- **代码规范**: ESLint + Prettier

## 项目结构

```
frontend/
├── src/
│   ├── api/                    # API 请求封装
│   │   └── request.ts          # Axios 封装（拦截器、Token 管理）
│   ├── assets/                 # 静态资源
│   ├── components/             # 通用组件
│   ├── layouts/                # 布局组件
│   │   └── MainLayout.vue      # 主布局（侧边栏、顶栏、内容区）
│   ├── router/                 # 路由配置
│   │   └── index.ts            # 路由定义和导航守卫
│   ├── stores/                 # Pinia 状态管理
│   │   ├── app.ts              # 应用状态（主题、侧边栏等）
│   │   └── user.ts             # 用户状态（登录、权限等）
│   ├── types/                  # TypeScript 类型定义
│   │   └── common.ts           # 通用类型
│   ├── utils/                  # 工具函数
│   ├── views/                  # 页面视图
│   │   ├── business/           # 业务页面
│   │   │   ├── lead/           # 线索管理
│   │   │   ├── customer/       # 客户管理
│   │   │   ├── contact/        # 联系人管理
│   │   │   ├── opportunity/    # 商机管理
│   │   │   ├── product/        # 产品管理
│   │   │   ├── contract/       # 合同管理
│   │   │   └── payment/        # 回款管理
│   │   ├── dashboard/          # 仪表盘
│   │   ├── error/              # 错误页面
│   │   ├── login/              # 登录页面
│   │   ├── profile/            # 个人中心
│   │   └── system/             # 系统管理
│   │       ├── user/           # 用户管理
│   │       ├── role/           # 角色管理
│   │       └── department/     # 部门管理
│   ├── App.vue                 # 根组件
│   ├── AppContent.vue          # 内容组件
│   └── main.ts                 # 应用入口
├── .env.development            # 开发环境变量
├── .env.example                # 环境变量示例
├── index.html                  # HTML 模板
├── package.json                # 项目依赖
├── tsconfig.json               # TypeScript 配置
└── vite.config.ts              # Vite 配置
```

## 核心功能

### 1. Axios 请求封装

- 统一的请求/响应拦截器
- 自动添加 Token 到请求头
- Token 过期自动刷新
- 401 错误自动跳转登录页
- 统一的错误处理
- 支持文件上传和下载

### 2. 路由配置

- 基于 Vue Router 4
- 支持路由懒加载
- 路由权限控制（基于角色和权限）
- 自动生成面包屑导航
- 页面缓存（keep-alive）

### 3. 状态管理

#### 用户状态 (useUserStore)
- 登录/登出
- 用户信息管理
- 权限验证（hasPermission、hasRole 等）
- 密码修改

#### 应用状态 (useAppStore)
- 侧边栏折叠/展开
- 主题切换（亮色/暗色/自动）
- 设备类型检测（桌面/平板/移动）
- 面包屑导航
- 加载状态管理

### 4. 主布局组件

- 响应式侧边栏（支持折叠）
- 移动端抽屉式侧边栏
- 顶部导航栏（面包屑、用户信息、主题切换）
- 基于权限的菜单显示
- 页面缓存支持

## 开发指南

### 安装依赖

```bash
# 使用 pnpm（推荐）
pnpm install

# 或使用 npm
npm install
```

### 启动开发服务器

```bash
pnpm dev
```

访问 http://localhost:5173

### 构建生产版本

```bash
pnpm build
```

### 代码检查

```bash
# 运行 ESLint
pnpm lint

# 类型检查
pnpm type-check
```

### 运行测试

```bash
# 单元测试
pnpm test

# E2E 测试
pnpm test:e2e
```

## 环境变量

复制 `.env.example` 为 `.env.development` 或 `.env.production`，并根据实际情况修改：

```env
# API 基础路径
VITE_API_BASE_URL=http://localhost:8080/api

# 应用标题
VITE_APP_TITLE=CRM 系统

# 应用版本
VITE_APP_VERSION=1.0.0

# 是否启用 Mock 数据
VITE_USE_MOCK=false

# 是否启用调试模式
VITE_DEBUG=true
```

## API 调用示例

```typescript
import { request } from '@/api/request'

// GET 请求
const result = await request.get('/users')

// POST 请求
const result = await request.post('/users', { name: '张三' })

// PUT 请求
const result = await request.put('/users/1', { name: '李四' })

// DELETE 请求
const result = await request.delete('/users/1')

// 文件上传
const result = await request.upload('/upload', file)

// 文件下载
await request.download('/download/file.pdf', 'file.pdf')
```

## 权限控制

### 在路由中配置权限

```typescript
{
  path: '/customers',
  name: 'Customers',
  component: () => import('@/views/business/customer/index.vue'),
  meta: {
    title: '客户管理',
    requiresAuth: true,
    permissions: ['customer:view'], // 需要的权限
    roles: ['admin', 'sales']        // 需要的角色
  }
}
```

### 在组件中检查权限

```typescript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 检查单个权限
if (userStore.hasPermission('customer:edit')) {
  // 有权限
}

// 检查多个权限（任意一个）
if (userStore.hasAnyPermission(['customer:edit', 'customer:delete'])) {
  // 有权限
}

// 检查角色
if (userStore.hasRole('admin')) {
  // 是管理员
}
```

## 主题切换

应用支持亮色、暗色和自动主题：

```typescript
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()

// 切换主题
appStore.toggleTheme()

// 设置指定主题
appStore.setThemeMode('dark')  // 'light' | 'dark' | 'auto'

// 检查当前主题
if (appStore.isDark) {
  // 当前是暗色主题
}
```

## 响应式设计

应用会自动检测设备类型并调整布局：

- **桌面端** (>= 1024px): 显示完整侧边栏
- **平板** (768px - 1023px): 侧边栏可折叠
- **移动端** (< 768px): 抽屉式侧边栏

## 注意事项

1. **路径别名**: 使用 `@/` 代替 `src/`
2. **组件命名**: 使用 PascalCase
3. **文件命名**: 使用 kebab-case
4. **代码风格**: 遵循 ESLint 和 Prettier 配置
5. **类型安全**: 充分利用 TypeScript 类型系统
6. **权限控制**: 所有需要权限的页面都要配置 meta.permissions 或 meta.roles

## 后续开发

当前已完成基础架构搭建，后续需要开发：

1. 完善各业务模块的页面和功能
2. 实现表单验证和数据提交
3. 添加数据表格和分页
4. 实现文件上传和预览
5. 添加图表和数据可视化
6. 完善错误处理和用户提示
7. 添加单元测试和 E2E 测试

## 相关文档

- [Vue 3 文档](https://cn.vuejs.org/)
- [Naive UI 文档](https://www.naiveui.com/)
- [Pinia 文档](https://pinia.vuejs.org/zh/)
- [Vue Router 文档](https://router.vuejs.org/zh/)
- [Vite 文档](https://cn.vitejs.dev/)
