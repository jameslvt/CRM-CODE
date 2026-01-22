# 快速启动指南: 企业级 CRM 系统

**功能**: 001-enterprise-crm
**日期**: 2026-01-18
**状态**: 已完成

## 概述

本文档提供企业级 CRM 系统的快速启动指南，帮助开发者快速搭建本地开发环境并运行项目。

---

## 1. 环境要求

### 1.1 必需软件

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8.x | 后端运行环境 |
| Maven | 3.6+ | 后端构建工具 |
| Node.js | 18.x+ | 前端运行环境 |
| pnpm | 8.x+ | 前端包管理器 (推荐) |
| MySQL | 5.7.x | 数据库 |
| Redis | 5.x/6.x | 缓存服务 |

### 1.2 推荐 IDE

- **后端**: IntelliJ IDEA 2023.x+ (推荐) 或 Eclipse
- **前端**: VS Code + Volar 插件

---

## 2. 项目克隆

```bash
# 克隆项目
git clone <repository-url>
cd crm

# 切换到功能分支
git checkout 001-enterprise-crm
```

---

## 3. 数据库初始化

### 3.1 创建数据库

```sql
-- 创建数据库
CREATE DATABASE crm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 创建用户 (可选，生产环境推荐)
CREATE USER 'crm_user'@'localhost' IDENTIFIED BY 'crm_password';
GRANT ALL PRIVILEGES ON crm_db.* TO 'crm_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3.2 执行初始化脚本

```bash
# 进入后端目录
cd backend

# 执行数据库迁移脚本 (项目启动时自动执行)
# 或手动执行 SQL 脚本
mysql -u root -p crm_db < sql/init.sql
mysql -u root -p crm_db < sql/init-data.sql
```

---

## 4. 后端启动

### 4.1 配置文件

复制配置模板并修改:

```bash
cd backend/crm-admin/src/main/resources
cp application-local.yml.example application-local.yml
```

编辑 `application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/crm_db?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: # 如有密码填写

# JWT 密钥 (生产环境务必修改)
jwt:
  secret: your-secret-key-at-least-32-characters
  access-token-expire: 7200  # 2小时
  refresh-token-expire: 604800  # 7天
```

### 4.2 构建与启动

```bash
# 进入后端根目录
cd backend

# 安装依赖并构建
mvn clean install -DskipTests

# 启动应用
mvn spring-boot:run -pl crm-admin

# 或使用 IDE 直接运行 CrmApplication.java
```

### 4.3 验证后端

访问 API 文档: http://localhost:8080/doc.html (Knife4j)

默认管理员账号:
- 用户名: `admin`
- 密码: `123456`

---

## 5. 前端启动

### 5.1 安装依赖

```bash
# 进入前端目录
cd frontend

# 安装依赖 (推荐使用 pnpm)
pnpm install

# 或使用 npm
npm install
```

### 5.2 配置环境变量

创建 `.env.local` 文件:

```bash
# API 基础路径
VITE_API_BASE_URL=http://localhost:8080

# 应用标题
VITE_APP_TITLE=企业级 CRM 系统
```

### 5.3 启动开发服务器

```bash
# 开发模式启动
pnpm dev

# 或
npm run dev
```

### 5.4 验证前端

访问: http://localhost:5173

---

## 6. Docker 快速启动 (可选)

### 6.1 使用 Docker Compose

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f crm-backend
```

### 6.2 docker-compose.yml 示例

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: crm_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"

  crm-backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_PROFILES_ACTIVE: docker

  crm-frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - crm-backend

volumes:
  mysql_data:
```

---

## 7. 常用命令

### 7.1 后端命令

```bash
# 编译项目
mvn clean compile

# 打包项目
mvn clean package -DskipTests

# 运行单元测试
mvn test

# 运行集成测试
mvn verify -P integration-test

# 生成 API 文档
mvn knife4j:generate
```

### 7.2 前端命令

```bash
# 开发模式
pnpm dev

# 构建生产版本
pnpm build

# 预览生产版本
pnpm preview

# 运行单元测试
pnpm test

# 运行 E2E 测试
pnpm test:e2e

# 代码检查
pnpm lint

# 类型检查
pnpm type-check
```

---

## 8. 项目结构速查

### 8.1 后端模块

```
backend/
├── crm-common/      # 公共模块 (工具类、异常、Result<T>)
├── crm-system/      # 系统模块 (用户、角色、部门、权限)
├── crm-business/    # 业务模块 (L2C 核心业务)
├── crm-ai/          # AI 模块 (P2 阶段，MCP 集成)
└── crm-admin/       # 启动模块 (主启动类、配置)
```

### 8.2 前端目录

```
frontend/src/
├── api/            # API 调用封装
├── components/     # 通用组件
├── views/          # 页面视图
│   ├── system/     # 系统管理 (用户、角色、部门)
│   └── business/   # 业务页面 (线索、客户、商机...)
├── stores/         # Pinia 状态管理
├── router/         # 路由配置
├── layouts/        # 布局组件
├── utils/          # 工具函数
└── types/          # TypeScript 类型定义
```

---

## 9. 常见问题

### Q1: Maven 下载依赖慢?

配置阿里云镜像，编辑 `~/.m2/settings.xml`:

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### Q2: Node.js 安装依赖失败?

使用淘宝镜像:

```bash
pnpm config set registry https://registry.npmmirror.com
```

### Q3: MySQL 连接失败?

检查:
1. MySQL 服务是否启动
2. 用户名/密码是否正确
3. 数据库是否已创建
4. 防火墙是否阻止 3306 端口

### Q4: Redis 连接失败?

检查:
1. Redis 服务是否启动: `redis-cli ping`
2. 端口是否正确 (默认 6379)
3. 密码配置是否匹配

### Q5: 前端无法连接后端?

检查:
1. 后端是否正常启动 (访问 http://localhost:8080/doc.html)
2. `.env.local` 中 `VITE_API_BASE_URL` 配置是否正确
3. 浏览器控制台是否有 CORS 错误

---

## 10. 下一步

1. 阅读 [功能规范](./spec.md) 了解业务需求
2. 阅读 [数据模型](./data-model.md) 了解数据库设计
3. 阅读 [API 合同](./contracts/) 了解接口定义
4. 查看 [技术研究](./research.md) 了解技术决策
5. 执行 [任务列表](./tasks.md) 开始开发 (由 `/speckit.tasks` 生成)
