测试与代码修改规则协议 (Testing & Coding Protocols)
为了确保 L2C 全流程测试的有效性及代码修改的准确性，制定以下规则。本协议遵循 KISS (简洁)、Spec-Style (规格即文) 和 Evidence-Based (事实为本) 核心原则，切记问题只记录，不修改。

1. 测试执行规则 (Testing Protocol)
1.1 严格遵循清单
指令: 测试活动必须严格对应 test-checklist.md 中的 Item ID。
前置: 每次测试大模块（如线索转客户）前，必须检查并清理脏数据，确保环境纯净。
记录: 对关键步骤（Success/Fail）进行事实记录。
Success: 简略确认（"Checked, Pass"）。
Fail: 必须提供 [输入数据, 预期行为, 实际行为, 后端报错/日志关键行]。
1.2 模拟与验证
E2E 优先: 优先模拟真实用户操作路径（如：先登录 -> 再操作）。
数据完整性: 不仅验证 UI显示，必须验证数据库记录（通过 SQL 或后端日志确认关键字段如 status, amount 已落库），数据库操作可以询问您，因为你没有权限操作，不要自己执行命令。
2. 代码修改规则 (Code Modification Protocol)
2.1 Spec 优先 (Spec-First)
原则: 代码是 Spec 的实现。
流程:
发现 Bug。
停顿 (Halt): 检查 
spec.md
 或 
data-model.md
如果是 Spec 定义错误/遗漏,指出来，输出到test_issues.md 中。
2.2 全局上下文与设计一致性 (Global Context & Consistency) [CRITICAL]
全局通读: 修改代码前，务必通读相关的前端代码、后端代码及数据库结构。
严禁臆造: 严格遵循现有设计模式和架构风格，严禁偏离当前设计或自我创造新的模式。
3. 交互模式 (Interaction Mode)
3.1 询问模式 (Ask-First)
场景:
遇到 Spec 未定义的边界情况（如：合同金额是否允许为负？Spec未说 -> 问）。
遇到现有代码逻辑十分复杂，看不懂意图时 -> 问。
修复方案存在多种权衡（如：前端拦截还是后端拦截？） -> 问。
4. 问题记录与修复流程 (Defect Recording & Fix Process) [UPDATED]
4.1 记录优先 (Record First)
统一记录: 所有测试遇到的问题（Bug/体验不佳/Spec不符），必须记录到 test_issues.md 文件中。
不中断: 发现问题后，严禁立即着手修改代码。应记录后继续执行后续测试（除非阻塞无法进行）。
4.2 分析与方案 (Analyze & Propose)
深入分析: 针对记录的每个问题，必须查阅：
前端代码 (Frontend Code)
后端代码 (Backend Code)
数据库结构/数据 (DB Schema & Data)，数据结构和初始化数据在后端代码的sql目录下面
给出方案: 在 test_issues.md 中为每个问题提供根本原因 (Root Cause) 和 拟定修复方案 (Proposed Solution)。
4.3 批量确认与修复 (Batch Confirm & Fix)
禁止擅自修改: 在整个测试阶段，完全禁止修改任何业务代码（只读模式）。
统一审批: 测试结束后，提交包含分析和方案的 test_issues.md 供您审查。
授权执行: 只有在您对所有方案及影响范围确认无误后，才开始批量执行修复。
5. 验证闭环 (Verification Loop)
Regression: 等后续您修复完成后，再重新验证 test_issues.md 中的所有条目。
