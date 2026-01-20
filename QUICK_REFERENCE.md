# CRM 项目快速参考

## 当前状态
- **分支**: 001-enterprise-crm
- **完成阶段**: 1-3（项目设置、基础设施、系统管理）
- **下一步**: 阶段 4-9（业务模块 L2C 闭环）

## 快速启动

### 后端启动
```bash
cd backend
mvn clean install
cd crm-admin
mvn spring-boot:run
```
访问: http://localhost:8080/doc.html

### 前端启动
```bash
cd frontend
pnpm install
pnpm dev
```
访问: http://localhost:5173

### 数据库初始化
```bash
mysql -u root -p
source backend/sql/init.sql
source backend/sql/schema-system.sql
source backend/sql/schema-business.sql
source backend/sql/init-data.sql
```

默认管理员账号：
- 用户名: admin
- 密码: admin123

## 项目结构

```
crm/
├── backend/                    # 后端（Java 8 + Spring Boot 2.7.x）
│   ├── crm-common/            # 公共模块
│   ├── crm-system/            # 系统模块
│   ├── crm-business/          # 业务模块
│   ├── crm-admin/             # 启动模块
│   └── sql/                   # SQL 脚本
├── frontend/                   # 前端（Vue 3 + TypeScript）
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── views/             # 页面视图
│   │   ├── stores/            # 状态管理
│   │   ├── types/             # 类型定义
│   │   └── styles/            # 全局样式
│   └── package.json
└── specs/                      # 规范文档
    └── 001-enterprise-crm/
```

## 已完成的页面

1. ✅ 登录页面 - `/login`
2. ✅ 用户管理 - `/system/user`
3. ✅ 角色管理 - `/system/role`
4. ✅ 部门管理 - `/system/department`
5. ✅ 权限管理 - `/system/permission`

## 设计规范（ui-ux-pro-max）

- **主色**: #2563EB（企业蓝）
- **字体**: Poppins（标题）+ Open Sans（正文）
- **风格**: Trust & Authority
- **响应式**: 桌面/移动端
- **图标**: @vicons/antd（SVG）

## 下一步开发

### 阶段 4: 线索管理
- [ ] 后端：Lead 实体、Service、Controller
- [ ] 前端：线索列表、表单、转化功能
- [ ] 使用 ui-ux-pro-max 设计

### 阶段 5-9: 其他业务模块
- [ ] 客户管理
- [ ] 商机管理
- [ ] 产品管理
- [ ] 合同管理
- [ ] 回款管理

## 常用命令

```bash
# 后端编译
mvn clean compile

# 后端打包
mvn clean package -DskipTests

# 前端类型检查
pnpm type-check

# 前端代码检查
pnpm lint

# 查看 Git 状态
git status

# 提交代码
git add .
git commit -m "feat: 完成系统管理模块"
```

## 技术栈

**后端**:
- Java 8
- Spring Boot 2.7.18
- MyBatis Plus 3.5.5
- MySQL 5.7
- Redis 5.x/6.x
- JWT

**前端**:
- Vue 3.4+
- TypeScript 5.3
- Naive UI 2.38
- Pinia 2.1
- Vite 5.0

## 重要链接

- API 文档: http://localhost:8080/doc.html
- Druid 监控: http://localhost:8080/druid/
- 前端开发: http://localhost:5173
