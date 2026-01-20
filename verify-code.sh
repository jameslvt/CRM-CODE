#!/bin/bash

# 企业级CRM系统 - 快速验证脚本
# 用于检查代码完整性和基本配置

echo "========================================="
echo "企业级CRM系统 - 代码验证"
echo "========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查函数
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $2"
        return 0
    else
        echo -e "${RED}✗${NC} $2 (文件不存在: $1)"
        return 1
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $2"
        return 0
    else
        echo -e "${RED}✗${NC} $2 (目录不存在: $1)"
        return 1
    fi
}

# 统计变量
total_checks=0
passed_checks=0

# 1. 检查后端文件
echo "1. 检查后端代码..."
echo "-------------------"

# 字典管理
((total_checks++))
check_file "backend/crm-system/src/main/java/com/crm/system/entity/DictType.java" "字典类型实体" && ((passed_checks++))
((total_checks++))
check_file "backend/crm-system/src/main/java/com/crm/system/entity/DictData.java" "字典数据实体" && ((passed_checks++))
((total_checks++))
check_file "backend/crm-system/src/main/java/com/crm/system/service/DictService.java" "字典服务" && ((passed_checks++))
((total_checks++))
check_file "backend/crm-system/src/main/java/com/crm/system/controller/DictController.java" "字典控制器" && ((passed_checks++))

# 商机阶段
((total_checks++))
check_file "backend/crm-system/src/main/java/com/crm/system/entity/OpportunityStage.java" "商机阶段实体" && ((passed_checks++))
((total_checks++))
check_file "backend/crm-system/src/main/java/com/crm/system/service/OpportunityStageService.java" "商机阶段服务" && ((passed_checks++))
((total_checks++))
check_file "backend/crm-system/src/main/java/com/crm/system/controller/OpportunityStageController.java" "商机阶段控制器" && ((passed_checks++))

# Excel工具
((total_checks++))
check_file "backend/crm-common/src/main/java/com/crm/common/utils/ExcelUtils.java" "Excel工具类" && ((passed_checks++))

echo ""

# 2. 检查前端文件
echo "2. 检查前端代码..."
echo "-------------------"

# 字典管理
((total_checks++))
check_file "frontend/src/stores/dict.ts" "字典状态管理" && ((passed_checks++))
((total_checks++))
check_file "frontend/src/api/system/dict.ts" "字典API" && ((passed_checks++))

# 商机阶段
((total_checks++))
check_file "frontend/src/views/system/config/opportunity-stage.vue" "商机阶段配置页面" && ((passed_checks++))
((total_checks++))
check_file "frontend/src/api/system/opportunity-stage.ts" "商机阶段API" && ((passed_checks++))

# 线索管理
((total_checks++))
check_file "frontend/src/views/business/lead/index.vue" "线索列表页面" && ((passed_checks++))
((total_checks++))
check_file "frontend/src/views/business/lead/detail.vue" "线索详情页面" && ((passed_checks++))
((total_checks++))
check_file "frontend/src/components/business/LeadForm.vue" "线索表单组件" && ((passed_checks++))
((total_checks++))
check_file "frontend/src/components/business/LeadConvertDialog.vue" "线索转化弹窗" && ((passed_checks++))
((total_checks++))
check_file "frontend/src/components/business/LeadImportExport.vue" "导入导出组件" && ((passed_checks++))
((total_checks++))
check_file "frontend/src/api/business/lead.ts" "线索API" && ((passed_checks++))
((total_checks++))
check_file "frontend/src/types/business.ts" "业务类型定义" && ((passed_checks++))

echo ""

# 3. 检查配置文件
echo "3. 检查配置文件..."
echo "-------------------"

((total_checks++))
check_file "backend/pom.xml" "后端父POM" && ((passed_checks++))
((total_checks++))
check_file "frontend/package.json" "前端package.json" && ((passed_checks++))

echo ""

# 4. 检查POI依赖
echo "4. 检查关键依赖..."
echo "-------------------"

if grep -q "poi-ooxml" backend/pom.xml; then
    echo -e "${GREEN}✓${NC} Apache POI依赖已配置"
    ((passed_checks++))
else
    echo -e "${RED}✗${NC} Apache POI依赖未配置"
fi
((total_checks++))

echo ""

# 5. 统计结果
echo "========================================="
echo "验证结果"
echo "========================================="
echo "总检查项: $total_checks"
echo "通过: $passed_checks"
echo "失败: $((total_checks - passed_checks))"
echo ""

if [ $passed_checks -eq $total_checks ]; then
    echo -e "${GREEN}✓ 所有检查通过！代码完整性验证成功。${NC}"
    echo ""
    echo "下一步："
    echo "1. 后端: cd backend && mvn clean install && mvn spring-boot:run -pl crm-admin"
    echo "2. 前端: cd frontend && npm install && npm install xlsx && npm run dev"
    echo "3. 访问: http://localhost:5173"
    exit 0
else
    echo -e "${RED}✗ 有 $((total_checks - passed_checks)) 项检查失败，请检查缺失的文件。${NC}"
    exit 1
fi
