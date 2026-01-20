# 用户管理页面快速启动指南

## 1. 安装依赖

```bash
cd /Users/jameslvt/Documents/AI/crm/frontend
npm install
# 或使用 pnpm
pnpm install
```

## 2. 启动开发服务器

```bash
npm run dev
# 或
pnpm dev
```

访问: http://localhost:5173

## 3. 访问用户管理页面

在浏览器中访问: http://localhost:5173/system/user

## 4. 文件位置

- **页面文件**: `/Users/jameslvt/Documents/AI/crm/frontend/src/views/system/user/index.vue`
- **API 接口**: `/Users/jameslvt/Documents/AI/crm/frontend/src/api/system/user.ts`
- **类型定义**: `/Users/jameslvt/Documents/AI/crm/frontend/src/types/system.ts`
- **全局样式**: `/Users/jameslvt/Documents/AI/crm/frontend/src/styles/global.css`

## 5. 主要功能

- 用户列表查询（支持搜索、筛选、分页）
- 新增用户
- 编辑用户
- 删除用户
- 分配角色
- 重置密码

## 6. 技术栈

- Vue 3 + TypeScript
- Naive UI
- Composition API
- Pinia (状态管理)
- Axios (HTTP 请求)

## 7. 注意事项

- 需要后端 API 支持（接口路径: `/api/system/user/*`）
- 开发阶段可以使用 Mock 数据
- 确保 `.env.development` 文件中配置了正确的 API 地址

## 8. 更多信息

详细文档请查看:
- `/Users/jameslvt/Documents/AI/crm/frontend/src/views/system/user/README.md`
- `/Users/jameslvt/Documents/AI/crm/frontend/USER_MANAGEMENT_SUMMARY.md`
