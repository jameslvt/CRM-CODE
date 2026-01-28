package com.crm.ai.service;

import com.crm.ai.client.OpenAIClient;
import com.crm.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI 服务
 * 提供 AI 相关的基础能力
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final OpenAIClient openAIClient;
    private final AiProperties aiProperties;
    private final com.crm.ai.mcp.McpToolExecutor toolExecutor;

    /**
     * 检查 AI 服务是否可用
     *
     * @return 是否可用
     */
    public boolean isAvailable() {
        return openAIClient.isAvailable();
    }

    /**
     * 检查 AI 功能是否启用
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return aiProperties.isEnabled();
    }

    /**
     * 发送简单对话请求
     *
     * @param message 用户消息
     * @return AI 响应
     */
    public String chat(String message) {
        return chat(null, message);
    }

    private static final String DEFAULT_SYSTEM_PROMPT = "你是一个企业级 CRM 系统的智能助手，内嵌在系统的右下角。\n" +
            "你的服务对象是业务员、销售经理。\n" +
            "请严格遵守以下回复规范：\n" +
            "1. **禁止**展示 SQL、表结构等技术细节。\n" +
            "2. **拒绝废话**：不要建议用户“登录系统”或“进入菜单”，因为你就在系统中。\n" +
            "3. **智能响应**：\n" +
            "   - 如果用户想查询数据（如“查看合同”、“找个客户”），请回复简短的引导语，并在回复末尾附带特殊指令：\n" +
            "     `:::action{\"type\":\"query\",\"entityType\":\"<target_entity>\",\"content\":\"<refined_query_condition>\"}:::`\n"
            +
            "     target_entity values: lead(线索), customer(客户), opportunity(商机), contract(合同), contact(联系人), product(产品), payment(回款)\n"
            +
            "   - 例如用户问“看下当前合同”，你回复：“好的，正在为您查找当前有效的合同... :::action{\"type\":\"query\",\"entityType\":\"contract\",\"content\":\"查询所有状态为生效且未过期的合同\"}:::”\n"
            +
            "4. 语气亲切、自信、高效。";

    /**
     * 发送带系统提示的对话请求
     *
     * @param systemPrompt 系统提示词
     * @param message      用户消息
     * @return AI 响应
     */
    public String chat(String systemPrompt, String message) {
        // 自动注入数据库表结构信息到 System Prompt
        String schemaInfo = getDatabaseSchemaContext();

        // 组合 Prompt：优先使用传入的 Prompt，否则使用默认 Prompt
        String basePrompt = (systemPrompt != null && !systemPrompt.isEmpty()) ? systemPrompt : DEFAULT_SYSTEM_PROMPT;
        String finalSystemPrompt = basePrompt + "\n\n" + schemaInfo;

        return openAIClient.chat(finalSystemPrompt, message);
    }

    /**
     * 获取数据库表结构上下文
     */
    private String getDatabaseSchemaContext() {
        try {
            // 调用工具获取表结构摘要
            Object result = toolExecutor.execute("get_database_schema", new com.alibaba.fastjson.JSONObject());
            if (result instanceof java.util.List) {
                StringBuilder sb = new StringBuilder();
                sb.append("\n\n【数据库表结构参考】\n");
                java.util.List<java.util.Map<String, String>> tables = (java.util.List<java.util.Map<String, String>>) result;
                for (java.util.Map<String, String> table : tables) {
                    sb.append("- ").append(table.get("name"));
                    String comment = table.get("comment");
                    if (comment != null && !comment.isEmpty()) {
                        sb.append(" (").append(comment).append(")");
                    }
                    sb.append("\n");
                }
                sb.append("如果是查询特定表的详细字段，请在回复中说明。\n");
                return sb.toString();
            }
        } catch (Exception e) {
            log.warn("获取数据库表结构失败，将忽略上下文注入", e);
        }
        return "";
    }

    /**
     * 获取 AI 服务状态信息
     *
     * @return 状态信息
     */
    public AiStatusInfo getStatus() {
        AiStatusInfo status = new AiStatusInfo();
        status.setEnabled(aiProperties.isEnabled());
        status.setAvailable(openAIClient.isAvailable());
        status.setModel(aiProperties.getOpenai().getModel());
        status.setBaseUrl(aiProperties.getOpenai().getBaseUrl());
        return status;
    }

    /**
     * AI 服务状态信息
     */
    @lombok.Data
    public static class AiStatusInfo {
        /**
         * 是否启用
         */
        private boolean enabled;

        /**
         * 是否可用
         */
        private boolean available;

        /**
         * 使用的模型
         */
        private String model;

        /**
         * API 基础 URL
         */
        private String baseUrl;
    }
}
