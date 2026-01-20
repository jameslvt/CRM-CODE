---
name: spec-guardian
description: |
  防止上下文爆炸导致规范遗忘的守护技能。
  自动注入章程核心约束，自动管理规范上下文。
  适用于 spec-kit 驱动的项目开发。
triggers:
  - 开始实现任务时（自动注入章程 + 当前阶段 + 待办任务）
  - Session 开始时（自动加载上次摘要）
  - 编写 API 时（自动注入契约片段）
  - 修改数据模型时（自动注入 data-model）
  - 完成功能时（自动更新 tasks.md + 提醒检查）
  - 对话轮次超过 15 轮时（提醒保存上下文）
  - 用户输入 /save-context, /check 命令时
---

# Spec Guardian

## 核心使命

在项目实现过程中，确保 Agent 始终遵循规范文件（章程、spec、data-model、contracts）的约束，防止因上下文膨胀导致的规范遗忘。

---

## 工作模式：全自动 + 2 个核心命令

### 自动行为（无需手动触发）

| 时机 | 自动行为 |
|------|----------|
| **任务开始** | 注入章程摘要（禁忌事项 + 技术栈）|
| **任务开始** | 显示当前实施阶段（从 `plan.md`）和待办任务（从 `tasks.md`）|
| **Session 开始** | 加载上次 session 摘要（`.specify/memory/session-summary.md`）|
| **编写 API** | 注入相关 `contracts/*.yaml` 片段 |
| **修改数据模型** | 注入 `data-model.md` 相关片段 |
| **完成功能** | 自动更新 `tasks.md` 状态 + 提醒执行 `/check` |
| **对话过长** | 轮次 > 15 或大量代码输出时，提醒执行 `/save-context` |

### 手动命令（仅 2 个）

| 命令 | 功能 |
|------|------|
| `/save-context` | 💾 总结当前进度并存入 memory，为重开 session 做准备 |
| `/check` | ✅ 执行合规性检查（章程 + 契约 + 需求验收）|

---

## 上下文注入策略

### 优先级分层

```
┌─────────────────────────────────────────┐
│ 🔴 永久区 (~200 tokens)                  │
│    - 章程禁忌事项                         │
│    - 技术栈核心约束                       │
├─────────────────────────────────────────┤
│ 🟡 动态区 (~500 tokens)                  │
│    - 当前任务相关规范片段                  │
├─────────────────────────────────────────┤
│ 🟢 工作区                                │
│    - 代码实现上下文                       │
└─────────────────────────────────────────┘
```

### 注意力优化

- **黄金位置**：关键约束放在上下文开头和结尾
- **按需加载**：只加载当前任务相关的规范片段
- **压缩摘要**：使用提取脚本生成精简摘要

---

## 规范文件结构

本技能适用于以下 spec-kit 目录结构：

```
.specify/
├── memory/
│   └── constitution.md    ← 章程（最高优先级）
├── scripts/
└── templates/

specs/{project-name}/
├── checklists/
│   └── requirements.md    ← 需求检查清单
├── contracts/
│   ├── business-api.yaml  ← 业务 API 契约
│   └── system-api.yaml    ← 系统 API 契约
├── data-model.md          ← 数据模型定义
├── plan.md                ← 实施计划
├── spec.md                ← 核心功能规范
└── tasks.md               ← 任务列表
```

---

## 章程摘要注入

当任务开始时，**动态读取**当前项目的章程并生成摘要：

### 步骤 1：定位章程文件

按以下优先级查找章程：
1. `.specify/memory/constitution.md`
2. `CONSTITUTION.md`
3. `docs/constitution.md`

### 步骤 2：提取核心约束

使用提取脚本解析章程，自动识别以下内容：

```bash
# 从 skill 目录执行
python "$(dirname "$0")/scripts/extract_summary.py" --constitution <章程路径>

# 或使用绝对路径
python /path/to/agent_skills/spec-guardian/scripts/extract_summary.py --constitution <章程路径>
```

脚本会自动提取：
- **技术栈约束**：从 `## 技术栈约束` 或类似章节
- **禁忌事项**：从 `## 禁忌事项` 或类似章节
- **架构约束**：从 `## 模块化` / `## 分层架构` 等章节

### 步骤 3：注入格式

提取后按以下格式注入上下文开头：

```markdown
## ⚠️ 章程核心约束（动态生成）

### 技术栈（必须遵守）
{从章程提取的技术栈列表}

### 禁忌事项 ❌
{从章程提取的禁忌事项列表}

### 架构约束
{从章程提取的架构约束列表}
```

---

## 规范文件定位

本技能使用**自动发现**机制定位规范文件：

### 自动发现规则

1. **章程**: `.specify/memory/constitution.md` 或项目根目录
2. **规范目录**: `specs/*/` 下的第一个子目录，或 `spec/`
3. **契约文件**: `contracts/*.yaml` 或 `contracts/*.yml`
4. **数据模型**: `data-model.md` 或 `datamodel.md`

### 手动指定

如需手动指定路径，在项目根目录创建 `.spec-guardian.yaml`：

```yaml
# .spec-guardian.yaml
constitution: .specify/memory/constitution.md
spec_root: specs/001-enterprise-crm/
contracts: specs/001-enterprise-crm/contracts/
data_model: specs/001-enterprise-crm/data-model.md
```

---

## 合规检查清单

执行 `/check` 命令时，**动态生成**检查项：

### 通用检查（始终执行）
- [ ] 是否遵循章程定义的分层架构？
- [ ] 是否存在章程禁止的模式？
- [ ] API 实现是否与契约文件一致？

### 项目特定检查（从章程提取）
- 从章程的 `禁忌事项` 章节动态生成检查规则
- 从章程的 `编码规范` 章节动态生成命名检查
- 从章程的 `数据模型` 要求动态生成表名/字段检查

### 需求验收检查（从 checklists/ 提取）

执行 `/check` 时自动读取 `checklists/requirements.md`：

1. 解析所有 `- [ ]` 和 `- [x]` 项
2. 对比已实现的功能与需求清单
3. 输出格式：

```markdown
## 📋 需求验收报告

### ❌ 未完成需求
{从 checklists/requirements.md 提取的 [ ] 项}

### ✅ 已完成需求
{从 checklists/requirements.md 提取的 [x] 项}
```

---

## 自动任务管理

### 任务开始时自动显示

当开始新任务时，自动读取并显示：

1. **当前阶段**（从 `plan.md`）：解析阶段标记，识别进行中的阶段
2. **待办任务**（从 `tasks.md`）：显示未完成的 `- [ ]` 项

### 功能完成时自动更新

当检测到功能实现完成时：

1. 自动在 `tasks.md` 中将对应任务标记为 `[x]`
2. 添加完成时间戳 `<!-- done: 2026-01-19 -->`
3. 提示下一个待办任务
4. 提醒执行 `/check` 合规检查

---

## 上下文压缩管理

### 核心机制

```
对话进行中 ──────────────────────────────────────────┐
                                                      │
  轮次 > 15 或大量代码输出?                             │
       │                                              │
       ├─ 是 → ⚠️ 提醒: "建议执行 /save-context"        │
       │                                              │
  用户执行 /save-context                               │
       │                                              │
       ↓                                              │
  ┌─────────────────────────────────────┐             │
  │ 自动总结:                            │             │
  │ - 本次 session 完成的任务             │             │
  │ - 当前进行中的工作                    │             │
  │ - 遇到的问题和决策                    │             │
  │ - 下一步计划                         │             │
  └─────────────────────────────────────┘             │
       │                                              │
       ↓                                              │
  存入 .specify/memory/session-summary.md             │
       │                                              │
       ↓                                              │
  用户开启新 session ─────────────────────────────────┘
       │
       ↓
  自动执行 /load-context
       │
       ↓
  注入上次 session 摘要到上下文开头
```

### `/save-context` 命令

执行时自动生成以下结构并写入 `.specify/memory/session-summary.md`：

```markdown
# Session 摘要

**保存时间**: {当前时间}
**Session 主题**: {从对话中推断}

## ✅ 已完成
{本次 session 完成的任务列表}

## 🔄 进行中
{当前正在进行的工作}

## 🚧 待解决
{遇到的问题或阻塞}

## 📋 下一步
{下一个 session 应该做的事情}

## 💡 关键决策
{本次做出的重要决策摘要}
```

### `/load-context` 命令

新 session 开始时自动执行，或手动触发：

1. 读取 `.specify/memory/session-summary.md`
2. 将摘要注入上下文开头（高优先级位置）
3. 提示用户上次的进度和下一步计划

### 自动提醒规则

当检测到以下情况时，主动提醒执行 `/save-context`：

| 触发条件 | 提醒内容 |
|----------|----------|
| 对话轮次 > 15 | ⚠️ 对话较长，建议执行 `/save-context` 保存进度 |
| 单次输出 > 500 行代码 | ⚠️ 检测到大量代码输出，建议保存上下文 |
| 完成一个功能模块 | 💡 功能已完成，建议执行 `/save-context` 记录进度 |
| 用户提到"先到这里" | ✅ 收到！正在执行 `/save-context`... |

---

## 记忆持久化

当做出重要实现决策时，将摘要写入 `.specify/memory/decisions.md`：

```markdown
## 决策记录

### [日期] [决策标题]
- **上下文**：为什么需要做这个决策
- **决策**：选择了什么方案
- **理由**：为什么选择这个方案
- **影响**：对其他模块的影响
```

---

## 使用脚本

### 提取规范摘要

```bash
# 自动发现章程
python3 scripts/extract_summary.py --constitution .specify/memory/constitution.md

# 同时提取功能规范概览
python3 scripts/extract_summary.py \
  --constitution .specify/memory/constitution.md \
  --spec specs/*/spec.md
```

### 执行合规检查

```bash
# 指定规范目录和源码目录
python3 scripts/compliance_check.py \
  --spec specs/*/ \
  --src src/main/java

# 输出报告到文件
python3 scripts/compliance_check.py \
  --spec specs/*/ \
  --src src/main/java \
  --output compliance_report.md
```

