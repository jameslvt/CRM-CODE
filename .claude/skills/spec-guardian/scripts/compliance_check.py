#!/usr/bin/env python3
"""
合规性检查脚本

检查代码实现是否符合章程约束和 API 契约。
"""

import argparse
import re
import yaml
from pathlib import Path
from typing import Dict, List, Tuple
from dataclasses import dataclass


@dataclass
class CheckResult:
    """检查结果"""
    category: str
    item: str
    passed: bool
    message: str


class ConstitutionChecker:
    """章程合规检查器"""
    
    # 章程禁止的模式
    FORBIDDEN_PATTERNS = {
        "nacos": r"(?i)\b(nacos|eureka|consul)\b",
        "seata": r"(?i)\b(seata|distributed.?transaction)\b",
        "magic_number": r"(?<![\w\d])(\d{2,})(?![\w\d])",  # 检测魔法数字
        "http_servlet_in_service": r"HttpServletRequest|HttpServletResponse",
        "old_date_api": r"(?<!\w)(java\.util\.Date|new Date\(\))(?!\w)",
    }
    
    # 必须使用的模式
    REQUIRED_PATTERNS = {
        "lombok_data": r"@Data",
        "transactional": r"@Transactional",
        "local_datetime": r"LocalDateTime",
    }
    
    def check_file(self, file_path: Path) -> List[CheckResult]:
        """检查单个文件"""
        results = []
        content = file_path.read_text(encoding="utf-8")
        
        # 检查禁止模式
        for name, pattern in self.FORBIDDEN_PATTERNS.items():
            matches = re.findall(pattern, content)
            if matches:
                results.append(CheckResult(
                    category="禁忌事项",
                    item=name,
                    passed=False,
                    message=f"发现禁止的模式: {matches[:3]}..."
                ))
        
        # 检查 Service 层是否使用了 HttpServletRequest
        if "Service" in file_path.name and re.search(self.FORBIDDEN_PATTERNS["http_servlet_in_service"], content):
            results.append(CheckResult(
                category="禁忌事项",
                item="service_http_servlet",
                passed=False,
                message="Service 层禁止直接操作 HttpServletRequest/Response"
            ))
        
        return results
    
    def check_directory(self, dir_path: Path) -> List[CheckResult]:
        """检查目录下的所有 Java 文件"""
        results = []
        for java_file in dir_path.rglob("*.java"):
            file_results = self.check_file(java_file)
            for result in file_results:
                result.message = f"[{java_file.name}] {result.message}"
            results.extend(file_results)
        return results


class ContractChecker:
    """API 契约检查器"""
    
    def load_contract(self, contract_path: Path) -> Dict:
        """加载 YAML 契约文件"""
        with open(contract_path, "r", encoding="utf-8") as f:
            return yaml.safe_load(f)
    
    def extract_api_endpoints(self, contract: Dict) -> List[Dict]:
        """从契约中提取 API 端点定义"""
        endpoints = []
        
        # 处理 OpenAPI 格式
        if "paths" in contract:
            for path, methods in contract["paths"].items():
                for method, details in methods.items():
                    if method in ["get", "post", "put", "delete", "patch"]:
                        endpoints.append({
                            "path": path,
                            "method": method.upper(),
                            "operation_id": details.get("operationId", ""),
                            "summary": details.get("summary", ""),
                        })
        
        # 处理自定义格式
        elif "apis" in contract:
            for api in contract["apis"]:
                endpoints.append({
                    "path": api.get("path", ""),
                    "method": api.get("method", "GET"),
                    "operation_id": api.get("name", ""),
                    "summary": api.get("description", ""),
                })
        
        return endpoints
    
    def check_controller_compliance(
        self, 
        controller_path: Path, 
        endpoints: List[Dict]
    ) -> List[CheckResult]:
        """检查 Controller 是否符合契约"""
        results = []
        
        if not controller_path.exists():
            return [CheckResult(
                category="契约合规",
                item="controller_missing",
                passed=False,
                message=f"Controller 文件不存在: {controller_path}"
            )]
        
        content = controller_path.read_text(encoding="utf-8")
        
        for endpoint in endpoints:
            path = endpoint["path"]
            method = endpoint["method"]
            
            # 检查路径是否存在
            # 简化路径匹配（移除路径参数）
            path_pattern = re.sub(r"\{[^}]+\}", r"[^/]+", path)
            
            mapping_annotation = f"@{method.capitalize()}Mapping"
            if method == "GET":
                mapping_annotation = "@GetMapping"
            elif method == "POST":
                mapping_annotation = "@PostMapping"
            elif method == "PUT":
                mapping_annotation = "@PutMapping"
            elif method == "DELETE":
                mapping_annotation = "@DeleteMapping"
            
            if mapping_annotation not in content:
                results.append(CheckResult(
                    category="契约合规",
                    item=f"endpoint_{endpoint['operation_id']}",
                    passed=False,
                    message=f"缺少 {method} {path} 的实现"
                ))
        
        return results


class DataModelChecker:
    """数据模型检查器"""
    
    def check_table_prefix(self, entity_path: Path) -> List[CheckResult]:
        """检查实体类的表名前缀"""
        results = []
        
        for java_file in entity_path.rglob("*.java"):
            content = java_file.read_text(encoding="utf-8")
            
            # 查找 @TableName 注解
            match = re.search(r'@TableName\s*\(\s*"([^"]+)"', content)
            if match:
                table_name = match.group(1)
                if not table_name.startswith("crm_"):
                    results.append(CheckResult(
                        category="数据模型",
                        item="table_prefix",
                        passed=False,
                        message=f"[{java_file.name}] 表名 '{table_name}' 未使用 'crm_' 前缀"
                    ))
        
        return results


def generate_report(results: List[CheckResult]) -> str:
    """生成检查报告"""
    if not results:
        return "✅ 所有检查通过！"
    
    passed = [r for r in results if r.passed]
    failed = [r for r in results if not r.passed]
    
    report_parts = [
        "# 合规性检查报告\n",
        f"- 通过: {len(passed)}",
        f"- 失败: {len(failed)}\n",
    ]
    
    if failed:
        report_parts.append("## ❌ 失败项\n")
        for result in failed:
            report_parts.append(f"### [{result.category}] {result.item}")
            report_parts.append(f"{result.message}\n")
    
    if passed:
        report_parts.append("## ✅ 通过项\n")
        for result in passed:
            report_parts.append(f"- [{result.category}] {result.item}")
    
    return "\n".join(report_parts)


def main():
    parser = argparse.ArgumentParser(description="合规性检查工具")
    parser.add_argument(
        "--spec",
        type=str,
        required=True,
        help="规范目录路径 (specs/xxx/)"
    )
    parser.add_argument(
        "--src",
        type=str,
        default="src/main/java",
        help="源代码目录路径"
    )
    parser.add_argument(
        "--output",
        type=str,
        default=None,
        help="输出报告路径"
    )
    
    args = parser.parse_args()
    
    spec_path = Path(args.spec)
    src_path = Path(args.src)
    
    all_results = []
    
    # 章程检查
    constitution_checker = ConstitutionChecker()
    if src_path.exists():
        all_results.extend(constitution_checker.check_directory(src_path))
    
    # 契约检查
    contract_checker = ContractChecker()
    contracts_path = spec_path / "contracts"
    if contracts_path.exists():
        for contract_file in contracts_path.glob("*.yaml"):
            contract = contract_checker.load_contract(contract_file)
            endpoints = contract_checker.extract_api_endpoints(contract)
            print(f"从 {contract_file.name} 加载了 {len(endpoints)} 个 API 端点")
    
    # 数据模型检查
    model_checker = DataModelChecker()
    entity_path = src_path / "entity"
    if entity_path.exists():
        all_results.extend(model_checker.check_table_prefix(entity_path))
    
    # 生成报告
    report = generate_report(all_results)
    
    if args.output:
        Path(args.output).write_text(report, encoding="utf-8")
        print(f"报告已写入: {args.output}")
    else:
        print(report)


if __name__ == "__main__":
    main()
