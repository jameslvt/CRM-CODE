package com.crm.ai.mcp;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * MCP 工具执行器
 * 负责执行 MCP 工具调用，将请求转发到对应的业务服务
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class McpToolExecutor {

    private final javax.sql.DataSource dataSource;

    // 注入业务服务（预留，实际使用时需要注入对应的 Service）
    // private final LeadService leadService;
    // ...

    /**
     * 执行工具调用
     *
     * @param toolName  工具名称
     * @param arguments 调用参数
     * @return 执行结果
     */
    public Object execute(String toolName, JSONObject arguments) {
        log.debug("执行 MCP 工具: {} 参数: {}", toolName, arguments);

        switch (toolName) {
            // 数据库工具
            case "get_database_schema":
                return getDatabaseSchema(arguments);

            // 线索相关
            case "search_leads":
                return searchLeads(arguments);
            case "create_lead":
                return createLead(arguments);
            case "get_lead":
                return getLead(arguments);

            // 客户相关
            case "search_customers":
                return searchCustomers(arguments);
            case "get_customer":
                return getCustomer(arguments);

            // 商机相关
            case "search_opportunities":
                return searchOpportunities(arguments);
            case "get_opportunity":
                return getOpportunity(arguments);

            // 合同相关
            case "search_contracts":
                return searchContracts(arguments);
            case "get_contract":
                return getContract(arguments);

            // 统计分析
            case "get_dashboard":
                return getDashboard(arguments);
            case "get_sales_funnel":
                return getSalesFunnel(arguments);

            default:
                throw new BusinessException("未知的工具: " + toolName);
        }
    }

    /**
     * 获取数据库表结构
     */
    private Object getDatabaseSchema(JSONObject arguments) {
        String tableName = arguments.getString("tableName");
        try (java.sql.Connection conn = dataSource.getConnection()) {
            if (tableName == null || tableName.trim().isEmpty()) {
                // 列出所有表
                // 使用 DatabaseMetaData 获取表信息
                java.sql.DatabaseMetaData meta = conn.getMetaData();
                // 这里的 catalog 和 schema 参数可能需要根据具体数据库调整，MySQL 通常 catalog=null,
                // schema=databaseName
                // 但这里我们可以传 null 匹配所有
                String catalog = conn.getCatalog();
                java.sql.ResultSet rs = meta.getTables(catalog, null, "%", new String[] { "TABLE" });

                java.util.List<java.util.Map<String, String>> tables = new java.util.ArrayList<>();
                while (rs.next()) {
                    java.util.Map<String, String> t = new java.util.LinkedHashMap<>();
                    t.put("name", rs.getString("TABLE_NAME"));
                    // REMARKS 某些驱动可能需要额外配置才能获取，但通常能拿到
                    t.put("comment", rs.getString("REMARKS"));
                    tables.add(t);
                }
                return tables;
            } else {
                // 获取指定表的 DDL
                // 简单的防注入校验
                if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
                    throw new BusinessException("非法表名");
                }

                try (java.sql.Statement stmt = conn.createStatement()) {
                    java.sql.ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + tableName);
                    if (rs.next()) {
                        // 第二列通常是 Create Table 语句
                        return rs.getString(2);
                    } else {
                        return "Table '" + tableName + "' not found.";
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取表结构失败", e);
            // 这里不抛出异常给 AI，而是返回错误信息，让 AI 知道发生了什么
            return "Error retrieving schema: " + e.getMessage();
        }
    }

    /**
     * 搜索线索
     * TODO: 实际实现需要注入 LeadService
     */
    private Object searchLeads(JSONObject arguments) {
        // 预留实现
        // String keyword = arguments.getString("keyword");
        // Integer status = arguments.getInteger("status");
        // String source = arguments.getString("source");
        // Integer page = arguments.getInteger("page");
        // Integer pageSize = arguments.getInteger("pageSize");
        // return leadService.search(keyword, status, source, page, pageSize);
        return createPlaceholderResponse("search_leads", "线索搜索功能");
    }

    /**
     * 创建线索
     */
    private Object createLead(JSONObject arguments) {
        // 预留实现
        return createPlaceholderResponse("create_lead", "线索创建功能");
    }

    /**
     * 获取线索详情
     */
    private Object getLead(JSONObject arguments) {
        // 预留实现
        Long id = arguments.getLong("id");
        return createPlaceholderResponse("get_lead", "获取线索 ID: " + id);
    }

    /**
     * 搜索客户
     */
    private Object searchCustomers(JSONObject arguments) {
        // 预留实现
        return createPlaceholderResponse("search_customers", "客户搜索功能");
    }

    /**
     * 获取客户详情
     */
    private Object getCustomer(JSONObject arguments) {
        Long id = arguments.getLong("id");
        return createPlaceholderResponse("get_customer", "获取客户 ID: " + id);
    }

    /**
     * 搜索商机
     */
    private Object searchOpportunities(JSONObject arguments) {
        return createPlaceholderResponse("search_opportunities", "商机搜索功能");
    }

    /**
     * 获取商机详情
     */
    private Object getOpportunity(JSONObject arguments) {
        Long id = arguments.getLong("id");
        return createPlaceholderResponse("get_opportunity", "获取商机 ID: " + id);
    }

    /**
     * 搜索合同
     */
    private Object searchContracts(JSONObject arguments) {
        return createPlaceholderResponse("search_contracts", "合同搜索功能");
    }

    /**
     * 获取合同详情
     */
    private Object getContract(JSONObject arguments) {
        Long id = arguments.getLong("id");
        return createPlaceholderResponse("get_contract", "获取合同 ID: " + id);
    }

    /**
     * 获取仪表盘数据
     */
    private Object getDashboard(JSONObject arguments) {
        return createPlaceholderResponse("get_dashboard", "仪表盘数据");
    }

    /**
     * 获取销售漏斗
     */
    private Object getSalesFunnel(JSONObject arguments) {
        return createPlaceholderResponse("get_sales_funnel", "销售漏斗数据");
    }

    /**
     * 创建占位响应
     * 实际部署时需要替换为真实的业务调用
     */
    private JSONObject createPlaceholderResponse(String tool, String description) {
        JSONObject response = new JSONObject();
        response.put("tool", tool);
        response.put("description", description);
        response.put("message", "MCP 工具已定义，需要集成业务服务后启用");
        response.put("status", "placeholder");
        return response;
    }
}
