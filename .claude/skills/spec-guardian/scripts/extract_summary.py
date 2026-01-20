#!/usr/bin/env python3
"""
规范摘要提取脚本

从章程和规范文件中提取核心约束，生成压缩后的上下文注入片段。
"""

import argparse
import re
from pathlib import Path
from typing import Dict, List, Optional


def extract_section(content: str, section_name: str) -> Optional[str]:
    """提取指定章节的内容"""
    pattern = rf"##\s*{re.escape(section_name)}.*?\n(.*?)(?=\n##|\Z)"
    match = re.search(pattern, content, re.DOTALL | re.IGNORECASE)
    return match.group(1).strip() if match else None


def extract_forbidden_items(content: str) -> List[str]:
    """提取禁忌事项列表"""
    section = extract_section(content, "禁忌事项")
    if not section:
        return []
    
    items = []
    for line in section.split("\n"):
        line = line.strip()
        if line.startswith("-") or line.startswith("*"):
            # 清理格式标记
            item = re.sub(r"^[-*]\s*", "", line)
            item = re.sub(r"\*\*([^*]+)\*\*", r"\1", item)
            if item:
                items.append(item)
    return items


def extract_tech_stack(content: str) -> Dict[str, str]:
    """提取技术栈约束"""
    section = extract_section(content, "技术栈约束")
    if not section:
        return {}
    
    stack = {}
    current_key = None
    current_items = []
    
    for line in section.split("\n"):
        line = line.strip()
        if line.startswith("**") and line.endswith("**:"):
            if current_key and current_items:
                stack[current_key] = ", ".join(current_items)
            current_key = line.strip("*:").strip()
            current_items = []
        elif line.startswith("-") and current_key:
            item = re.sub(r"^[-*]\s*", "", line)
            item = re.sub(r"\*\*([^*]+)\*\*", r"\1", item)
            # 只保留关键信息
            if ":" in item:
                key_val = item.split(":", 1)
                current_items.append(f"{key_val[0].strip()}: {key_val[1].strip()}")
            else:
                current_items.append(item)
    
    if current_key and current_items:
        stack[current_key] = ", ".join(current_items)
    
    return stack


def extract_architecture_constraints(content: str) -> List[str]:
    """提取架构约束"""
    constraints = []
    
    # 模块化单体架构
    if "模块化单体" in content or "Modular Monolith" in content:
        constraints.append("模块化单体架构")
    
    # 分层架构
    if "三层架构" in content or "分层架构" in content:
        constraints.append("严格三层：Controller → Service → Mapper")
    
    # 模块解耦
    if "模块解耦" in content or "Spring Event" in content:
        constraints.append("模块间通过 Spring Event 或接口抽象通信")
    
    return constraints


def generate_summary(constitution_path: str, max_tokens: int = 200) -> str:
    """生成章程摘要"""
    path = Path(constitution_path)
    if not path.exists():
        return f"错误: 文件不存在 - {constitution_path}"
    
    content = path.read_text(encoding="utf-8")
    
    # 提取各部分
    forbidden = extract_forbidden_items(content)
    tech_stack = extract_tech_stack(content)
    architecture = extract_architecture_constraints(content)
    
    # 构建摘要
    summary_parts = ["## ⚠️ 章程核心约束\n"]
    
    # 技术栈
    if tech_stack:
        summary_parts.append("### 技术栈（必须遵守）")
        for category, items in tech_stack.items():
            summary_parts.append(f"- {category}：{items}")
        summary_parts.append("")
    
    # 禁忌事项
    if forbidden:
        summary_parts.append("### 禁忌事项 ❌")
        for item in forbidden[:5]:  # 最多保留5项
            summary_parts.append(f"- {item}")
        summary_parts.append("")
    
    # 架构约束
    if architecture:
        summary_parts.append("### 架构约束")
        for constraint in architecture:
            summary_parts.append(f"- {constraint}")
    
    return "\n".join(summary_parts)


def extract_spec_overview(spec_path: str) -> str:
    """提取 spec.md 的功能概览"""
    path = Path(spec_path)
    if not path.exists():
        return f"错误: 文件不存在 - {spec_path}"
    
    content = path.read_text(encoding="utf-8")
    
    # 提取第一级和第二级标题作为概览
    headers = []
    for line in content.split("\n"):
        if line.startswith("# "):
            headers.append(line)
        elif line.startswith("## "):
            headers.append("  " + line.lstrip("# "))
    
    return "\n".join(headers[:20])  # 最多20个标题


def main():
    parser = argparse.ArgumentParser(description="规范摘要提取工具")
    parser.add_argument(
        "--constitution",
        type=str,
        help="章程文件路径 (.specify/memory/constitution.md)"
    )
    parser.add_argument(
        "--spec",
        type=str,
        help="规范文件路径 (specs/xxx/spec.md)"
    )
    parser.add_argument(
        "--output",
        type=str,
        default=None,
        help="输出文件路径（默认输出到标准输出）"
    )
    
    args = parser.parse_args()
    
    result = []
    
    if args.constitution:
        result.append(generate_summary(args.constitution))
    
    if args.spec:
        result.append("\n## 功能规范概览\n")
        result.append(extract_spec_overview(args.spec))
    
    output = "\n".join(result)
    
    if args.output:
        Path(args.output).write_text(output, encoding="utf-8")
        print(f"摘要已写入: {args.output}")
    else:
        print(output)


if __name__ == "__main__":
    main()
