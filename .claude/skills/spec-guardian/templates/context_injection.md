# 上下文注入模板

本模板定义了 spec-guardian 在不同场景下注入的上下文格式。

---

## 任务开始时注入

```markdown
## ⚠️ 章程核心约束

### 技术栈（必须遵守）
{{TECH_STACK}}

### 禁忌事项 ❌
{{FORBIDDEN_ITEMS}}

### 架构约束
{{ARCHITECTURE_CONSTRAINTS}}
```

---

## 编写 API 时注入

```markdown
## 📋 API 契约参考

### 当前实现的 API
- 路径: {{API_PATH}}
- 方法: {{HTTP_METHOD}}
- 描述: {{API_DESCRIPTION}}

### 请求格式
{{REQUEST_SCHEMA}}

### 响应格式
{{RESPONSE_SCHEMA}}

### 注意事项
- 必须使用 `Result<T>` 封装响应
- 遵循 RESTful 设计规范
```

---

## 修改数据模型时注入

```markdown
## 📊 数据模型参考

### 当前实体: {{ENTITY_NAME}}

#### 字段定义
{{FIELD_DEFINITIONS}}

#### 关联关系
{{RELATIONSHIPS}}

### 命名规范
- 表名前缀: `crm_`
- 字段命名: 驼峰 → 下划线
```

---

## 完成实现后提醒

```markdown
## ✅ 实现完成检查清单

请确认以下事项：

- [ ] 是否遵循分层架构？(Controller → Service → Mapper)
- [ ] Service 层是否添加了 @Transactional？
- [ ] 是否存在硬编码的魔法数字？
- [ ] 表名是否使用 crm_ 前缀？
- [ ] 是否使用 LocalDateTime 而非 java.util.Date？

如需执行自动检查，请输入: `/check`
```

---

## 占位符说明

| 占位符 | 来源 | 说明 |
|--------|------|------|
| `{{TECH_STACK}}` | constitution.md | 技术栈约束列表 |
| `{{FORBIDDEN_ITEMS}}` | constitution.md | 禁忌事项列表 |
| `{{ARCHITECTURE_CONSTRAINTS}}` | constitution.md | 架构约束列表 |
| `{{API_PATH}}` | contracts/*.yaml | 当前 API 路径 |
| `{{HTTP_METHOD}}` | contracts/*.yaml | HTTP 方法 |
| `{{API_DESCRIPTION}}` | contracts/*.yaml | API 描述 |
| `{{REQUEST_SCHEMA}}` | contracts/*.yaml | 请求体结构 |
| `{{RESPONSE_SCHEMA}}` | contracts/*.yaml | 响应体结构 |
| `{{ENTITY_NAME}}` | data-model.md | 实体名称 |
| `{{FIELD_DEFINITIONS}}` | data-model.md | 字段定义 |
| `{{RELATIONSHIPS}}` | data-model.md | 关联关系 |
