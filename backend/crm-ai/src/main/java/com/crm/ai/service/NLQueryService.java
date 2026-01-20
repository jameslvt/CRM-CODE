package com.crm.ai.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.exception.BusinessException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自然语言查询服务
 * 将自然语言转换为结构化查询条件
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NLQueryService {

    private final AiService aiService;

    /**
     * 解析自然语言查询为线索查询条件
     *
     * @param query 自然语言查询
     * @return 查询条件
     */
    public QueryCondition parseLeadQuery(String query) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        String systemPrompt = buildLeadQueryPrompt();
        String response = aiService.chat(systemPrompt, query);

        return parseQueryCondition(response);
    }

    /**
     * 解析自然语言查询为客户查询条件
     *
     * @param query 自然语言查询
     * @return 查询条件
     */
    public QueryCondition parseCustomerQuery(String query) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        String systemPrompt = buildCustomerQueryPrompt();
        String response = aiService.chat(systemPrompt, query);

        return parseQueryCondition(response);
    }

    /**
     * 解析自然语言查询为商机查询条件
     *
     * @param query 自然语言查询
     * @return 查询条件
     */
    public QueryCondition parseOpportunityQuery(String query) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        String systemPrompt = buildOpportunityQueryPrompt();
        String response = aiService.chat(systemPrompt, query);

        return parseQueryCondition(response);
    }

    /**
     * 解析自然语言查询为合同查询条件
     *
     * @param query 自然语言查询
     * @return 查询条件
     */
    public QueryCondition parseContractQuery(String query) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        String systemPrompt = buildContractQueryPrompt();
        String response = aiService.chat(systemPrompt, query);

        return parseQueryCondition(response);
    }

    /**
     * 通用自然语言查询解析
     *
     * @param entityType 实体类型
     * @param query 自然语言查询
     * @return 查询条件
     */
    public QueryCondition parseQuery(String entityType, String query) {
        switch (entityType.toLowerCase()) {
            case "lead":
                return parseLeadQuery(query);
            case "customer":
                return parseCustomerQuery(query);
            case "opportunity":
                return parseOpportunityQuery(query);
            case "contract":
                return parseContractQuery(query);
            default:
                throw new BusinessException("不支持的实体类型: " + entityType);
        }
    }

    /**
     * 构建线索查询提示词
     */
    private String buildLeadQueryPrompt() {
        return "你是一个 CRM 系统的查询助手。请将用户的自然语言查询转换为结构化的查询条件。\n" +
                "线索可查询的字段包括：\n" +
                "- name: 线索名称（支持模糊匹配）\n" +
                "- contactName: 联系人姓名（支持模糊匹配）\n" +
                "- phone: 电话\n" +
                "- status: 状态（1-新建, 2-跟进中, 3-已转化, 4-无效）\n" +
                "- source: 来源\n" +
                "- ownerId: 负责人ID\n" +
                "- createTimeStart/createTimeEnd: 创建时间范围\n" +
                "\n" +
                "请以 JSON 格式返回：\n" +
                "{\n" +
                "  \"filters\": [{\"field\": \"字段名\", \"operator\": \"eq/like/gt/lt/gte/lte/in/between\", \"value\": \"值\"}],\n" +
                "  \"orderBy\": \"排序字段\",\n" +
                "  \"orderDirection\": \"asc/desc\"\n" +
                "}\n" +
                "只返回 JSON，不要包含其他文字。";
    }

    /**
     * 构建客户查询提示词
     */
    private String buildCustomerQueryPrompt() {
        return "你是一个 CRM 系统的查询助手。请将用户的自然语言查询转换为结构化的查询条件。\n" +
                "客户可查询的字段包括：\n" +
                "- name: 客户名称（支持模糊匹配）\n" +
                "- industry: 行业\n" +
                "- scale: 规模\n" +
                "- level: 客户等级（A/B/C/D）\n" +
                "- status: 状态（1-潜在, 2-正式, 3-流失）\n" +
                "- ownerId: 负责人ID\n" +
                "- createTimeStart/createTimeEnd: 创建时间范围\n" +
                "\n" +
                "请以 JSON 格式返回：\n" +
                "{\n" +
                "  \"filters\": [{\"field\": \"字段名\", \"operator\": \"eq/like/gt/lt/gte/lte/in/between\", \"value\": \"值\"}],\n" +
                "  \"orderBy\": \"排序字段\",\n" +
                "  \"orderDirection\": \"asc/desc\"\n" +
                "}\n" +
                "只返回 JSON，不要包含其他文字。";
    }

    /**
     * 构建商机查询提示词
     */
    private String buildOpportunityQueryPrompt() {
        return "你是一个 CRM 系统的查询助手。请将用户的自然语言查询转换为结构化的查询条件。\n" +
                "商机可查询的字段包括：\n" +
                "- name: 商机名称（支持模糊匹配）\n" +
                "- customerId: 客户ID\n" +
                "- stage: 阶段（1-需求确认, 2-方案报价, 3-商务谈判, 4-赢单, 5-输单）\n" +
                "- amountMin/amountMax: 金额范围\n" +
                "- expectedDateStart/expectedDateEnd: 预计成交日期范围\n" +
                "- ownerId: 负责人ID\n" +
                "- createTimeStart/createTimeEnd: 创建时间范围\n" +
                "\n" +
                "请以 JSON 格式返回：\n" +
                "{\n" +
                "  \"filters\": [{\"field\": \"字段名\", \"operator\": \"eq/like/gt/lt/gte/lte/in/between\", \"value\": \"值\"}],\n" +
                "  \"orderBy\": \"排序字段\",\n" +
                "  \"orderDirection\": \"asc/desc\"\n" +
                "}\n" +
                "只返回 JSON，不要包含其他文字。";
    }

    /**
     * 构建合同查询提示词
     */
    private String buildContractQueryPrompt() {
        return "你是一个 CRM 系统的查询助手。请将用户的自然语言查询转换为结构化的查询条件。\n" +
                "合同可查询的字段包括：\n" +
                "- contractNo: 合同编号\n" +
                "- name: 合同名称（支持模糊匹配）\n" +
                "- customerId: 客户ID\n" +
                "- status: 状态（1-草稿, 2-审批中, 3-执行中, 4-已完成, 5-已终止）\n" +
                "- amountMin/amountMax: 金额范围\n" +
                "- signDateStart/signDateEnd: 签约日期范围\n" +
                "- ownerId: 负责人ID\n" +
                "\n" +
                "请以 JSON 格式返回：\n" +
                "{\n" +
                "  \"filters\": [{\"field\": \"字段名\", \"operator\": \"eq/like/gt/lt/gte/lte/in/between\", \"value\": \"值\"}],\n" +
                "  \"orderBy\": \"排序字段\",\n" +
                "  \"orderDirection\": \"asc/desc\"\n" +
                "}\n" +
                "只返回 JSON，不要包含其他文字。";
    }

    /**
     * 解析查询条件
     */
    private QueryCondition parseQueryCondition(String response) {
        try {
            JSONObject json = parseJsonFromResponse(response);
            QueryCondition condition = new QueryCondition();

            // 解析过滤条件
            List<QueryFilter> filters = new ArrayList<>();
            if (json.containsKey("filters")) {
                for (Object obj : json.getJSONArray("filters")) {
                    JSONObject filterJson = (JSONObject) obj;
                    QueryFilter filter = new QueryFilter();
                    filter.setField(filterJson.getString("field"));
                    filter.setOperator(filterJson.getString("operator"));
                    filter.setValue(filterJson.get("value"));
                    filters.add(filter);
                }
            }
            condition.setFilters(filters);

            // 解析排序
            condition.setOrderBy(json.getString("orderBy"));
            condition.setOrderDirection(json.getString("orderDirection"));

            return condition;
        } catch (Exception e) {
            log.error("解析查询条件失败: {}", response, e);
            throw new BusinessException("查询条件解析失败");
        }
    }

    /**
     * 从响应中提取 JSON
     */
    private JSONObject parseJsonFromResponse(String response) {
        try {
            return JSON.parseObject(response);
        } catch (Exception e) {
            int start = response.indexOf("{");
            int end = response.lastIndexOf("}");
            if (start >= 0 && end > start) {
                return JSON.parseObject(response.substring(start, end + 1));
            }
            throw new BusinessException("无法解析 AI 响应");
        }
    }

    /**
     * 查询条件
     */
    @Data
    public static class QueryCondition {
        /**
         * 过滤条件列表
         */
        private List<QueryFilter> filters = new ArrayList<>();

        /**
         * 排序字段
         */
        private String orderBy;

        /**
         * 排序方向
         */
        private String orderDirection;
    }

    /**
     * 查询过滤条件
     */
    @Data
    public static class QueryFilter {
        /**
         * 字段名
         */
        private String field;

        /**
         * 操作符: eq, like, gt, lt, gte, lte, in, between
         */
        private String operator;

        /**
         * 值
         */
        private Object value;
    }
}
