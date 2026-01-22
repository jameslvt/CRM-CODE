# 测试问题记录 (Test Issues Log)

遵循 `Test Rules.md` 协议：
- **Record First**: 遇到问题先记录，不修改代码。
- **Analyze**: 深入分析前端、后端和数据库。
- **Solution**: 提出修复方案待审批。

| ID | 关联 Checklist ID | 类型 | 描述 (Input / Expected / Actual / Error) | 根因分析 (Root Cause) | 拟定方案 (Proposed Solution) | 状态 |
|----|-------------------|------|------------------------------------------|-----------------------|------------------------------|------|
|    |                   |      |                                          |                       |                              |      |

| I-001 | 1.2 Role Creation | Bug | **操作**: 创建新角色<br>**预期**: 成功创建<br>**实际**: 报错 10000 系统繁忙 / 数据未正确保存 | **后端实体不匹配**<br>`RoleDTO`字段(`roleCode`,`sortOrder`)与实体`Role`字段(`roleKey`,`sort`)不一致，且缺少`dataScope`。`BeanUtils`拷贝失败。 | 1. 修改 `RoleDTO` 对齐实体字段。<br>2. 补充 `dataScope` 字段。 | Open |
| I-002 | 1.2 Role Assign | Bug | **操作**: 给用户分配角色<br>**预期**: 分配成功<br>**实际**: 报错 405 Method Not Allowed | **接口方法不匹配**<br>前端请求 PUT，后端定义 `@PostMapping("/{id}/roles")`。 | 修正前端 API 调用方法为 POST，或后端适配。 | Open |
| I-003 | 1.2 Dict Load | Bug | **操作**: 打开角色弹窗加载数据权限字典<br>**实际**: 404 Not Found (`/api/system/dicts/...`) | **API 路径不匹配**<br>后端 Controller 为 `@RequestMapping("/system/dict")` (单数)，前端请求复数路径。 | 统一前后端路径约定（建议前端修正为单数）。 | Open |
