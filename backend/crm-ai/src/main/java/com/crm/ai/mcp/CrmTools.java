package com.crm.ai.mcp;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CRM MCP 工具定义
 * 定义可供 AI 调用的 CRM 系统工具
 *
 * @author CRM System
 * @since 1.0.0
 */
public class CrmTools {

    /**
     * 获取所有可用工具定义
     *
     * @return 工具定义列表
     */
    public static List<ToolDefinition> getToolDefinitions() {
        List<ToolDefinition> tools = new ArrayList<>();

        // 线索相关工具
        tools.add(buildSearchLeadsTool());
        tools.add(buildCreateLeadTool());
        tools.add(buildGetLeadTool());

        // 客户相关工具
        tools.add(buildSearchCustomersTool());
        tools.add(buildGetCustomerTool());

        // 商机相关工具
        tools.add(buildSearchOpportunitiesTool());
        tools.add(buildGetOpportunityTool());

        // 合同相关工具
        tools.add(buildSearchContractsTool());
        tools.add(buildGetContractTool());

        // 统计分析工具
        tools.add(buildGetDashboardTool());
        tools.add(buildGetSalesFunnelTool());

        // 数据库工具
        tools.add(buildGetDatabaseSchemaTool());

        return tools;
    }

    /**
     * 获取数据库表结构工具
     */
    private static ToolDefinition buildGetDatabaseSchemaTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("get_database_schema");
        tool.setDescription("获取数据库表结构信息。支持查询所有表或通过 tableName 参数查询指定表。返回建表语句(DDL)或列定义。");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("tableName", new ParameterDefinition("string", "表名(可选)，如果为空则返回所有表的摘要信息，如果指定则返回该表的详细 DDL", false));
        tool.setParameters(params);

        return tool;
    }

    /**
     * 搜索线索工具
     */
    private static ToolDefinition buildSearchLeadsTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("search_leads");
        tool.setDescription("搜索线索列表，支持按名称、状态、来源等条件筛选");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("keyword", new ParameterDefinition("string", "搜索关键词，匹配线索名称或联系人", false));
        params.put("status", new ParameterDefinition("integer", "状态：1-新建, 2-跟进中, 3-已转化, 4-无效", false));
        params.put("source", new ParameterDefinition("string", "来源", false));
        params.put("page", new ParameterDefinition("integer", "页码，默认1", false));
        params.put("pageSize", new ParameterDefinition("integer", "每页数量，默认10", false));
        tool.setParameters(params);

        return too

    /**
     * 创建线索工具
     */
    private static ToolDefinition buildCreateLeadTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("create_lead");
        tool.setDescription("创建新线索");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("name", new ParameterDefinition("string", "线索名称/公司名称", true));
        params.put("contactName", new ParameterDefinition("string", "联系人姓名", false));
        params.put("phone", new ParameterDefinition("string", "电话号码", false));
        params.put("email", new ParameterDefinition("string", "邮箱", false));
        params.put("source", new ParameterDefinition("string", "来源", false));
        params.put("remark", new ParameterDefinition("string", "备注", false));
        tool.setParameters(params);

        return tool;
    }

    /**
     * 获取线索详情工具
     */
    private static ToolDefinition buildGetLeadTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("get_lead");
        tool.setDescription("获取线索详细信息");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("id", new ParameterDefinition("integer", "线索ID", true));
        tool.setParameters(params);

        return tool;
    }

    /**
     * 搜索客户工具
     */
    private static ToolDefinition buildSearchCustomersTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("search_customers");
        tool.setDescription("搜索客户列表，支持按名称、行业、等级等条件筛选");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("keyword", new ParameterDefinition("string", "搜索关键词，匹配客户名称", false));
        params.put("industry", new ParameterDefinition("string", "行业", false));
        params.put("level", new ParameterDefinition("string", "客户等级：A/B/C/D", false));
        params.put("status", new ParameterDefinition("integer", "状态：1-潜在, 2-正式, 3-流失", false));
        params.put("page", new ParameterDefinition("integer", "页码，默认1", false));
        params.put("pageSize", new ParameterDefinition("integer", "每页数量，默认10", false));
        tool.setParameters(params);

        return tool;
    }

    /**
     * 获取客户详情工具
     */
    private static ToolDefinition buildGetCustomerTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("get_customer");
        tool.setDescription("获取客户详细信息，包括联系人、商机、合同等关联数据");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("id", new ParameterDefinition("integer", "客户ID", true));
        tool.setParameters(params);

        return tool;
    }

    /**
     * 搜索商机工具
     */
    private static ToolDefinition buildSearchOpportunitiesTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("search_opportunities");
        tool.setDescription("搜索商机列表，支持按名称、阶段、金额等条件筛选");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("keyword", new ParameterDefinition("string", "搜索关键词，匹配商机名称", false));
        params.put("customerId", new ParameterDefinition("integer", "客户ID", false));
        params.put("stage", new ParameterDefinition("integer", "阶段：1-需求确认, 2-方案报价, 3-商务谈判, 4-赢单, 5-输单", false));
        params.put("amountMin", new ParameterDefinition("number", "最小金额", false));
        params.put("amountMax", new ParameterDefinition("number", "最大金额", false));
        params.put("page", new ParameterDefinition("integer", "页码，默认1", false));
        params.put("pageSize", new ParameterDefinition("integer", "每页数量，默认10", false));
        tool.setParameters(params);

        return tool;
    }

    /**
     * 获取商机详情工具
     */
    private static ToolDefinition buildGetOpportunityTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("get_opportunity");
        tool.setDescription("获取商机详细信息，包括产品明细、跟进记录等");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("id", new ParameterDefinition("integer", "商机ID", true));
        tool.setParameters(params);

        return tool;
    }

    /**
     * 搜索合同工具
     */
    private static ToolDefinition buildSearchContractsTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("search_contracts");
        tool.setDescription("搜索合同列表，支持按编号、名称、状态等条件筛选");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("keyword", new ParameterDefinition("string", "搜索关键词，匹配合同编号或名称", false));
        params.put("customerId", new ParameterDefinition("integer", "客户ID", false));
        params.put("status", new ParameterDefinition("integer", "状态：1-草稿, 2-审批中, 3-执行中, 4-已完成, 5-已终止", false));
        params.put("page", new ParameterDefinition("integer", "页码，默认1", false));
        params.put("pageSize", new ParameterDefinition("integer", "每页数量，默认10", false));
        tool.setParameters(params);

        return tool;
    }

    /**
     * 获取合同详情工具
     */
    private static ToolDefinition buildGetContractTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("get_contract");
        tool.setDescription("获取合同详细信息，包括回款计划、回款记录等");

        Map<String, ParameterDefinition> params = new HashMap<>();
        params.put("id", new ParameterDefinition("integer", "合同ID", true));
        tool.setParameters(params);

        return tool;
    }

    /**
     * 获取仪表盘数据工具
     */
    private static ToolDefinition buildGetDashboardTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("get_dashboard");
        tool.setDescription("获取 CRM 仪表盘数据，包括线索、客户、商机、合同、回款的统计信息");

        tool.setParameters(new HashMap<>());

        return tool;
    }

    /**
     * 获取销售漏斗工具
     */
    private static ToolDefinition buildGetSalesFunnelTool() {
        ToolDefinition tool = new ToolDefinition();
        tool.setName("get_sales_funnel");
        tool.setDescription("获取销售漏斗数据，展示各阶段商机数量和金额");

        tool.setParameters(new HashMap<>());

        return tool;
    }

    /**
     * 工具定义
     */
    @Data
    public static class ToolDefinition {
        /**
         * 工具名称
         */
        private String name;

        /**
         * 工具描述
         */
        private String description;

        /**
         * 参数定义
         */
        private Map<String, ParameterDefinition> parameters;
    }

    /**
     * 参数定义
     */
    @Data
    public static class ParameterDefinition {
        /**
         * 参数类型
         */
        private String type;

        /**
         * 参数描述
         */
        private String description;

        /**
         * 是否必填
         */
        private boolean required;

        public ParameterDefinition() {
        }

        public ParameterDefinition(String type, String description, boolean required) {
            this.type = type;
            this.description = description;
            this.required = required;
        }
    }
}
