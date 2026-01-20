package com.crm.ai.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.exception.BusinessException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 智能查重服务
 * 通过 AI 分析数据相似度，识别潜在重复记录
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateCheckService {

    private final AiService aiService;

    /**
     * 检查线索是否与现有数据重复
     *
     * @param newLead 新线索信息
     * @param existingLeads 现有线索列表
     * @return 查重结果
     */
    public DuplicateCheckResult checkLeadDuplicate(JSONObject newLead, List<JSONObject> existingLeads) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        if (existingLeads == null || existingLeads.isEmpty()) {
            return DuplicateCheckResult.noDuplicate();
        }

        String systemPrompt = buildDuplicateCheckPrompt("线索");
        String userMessage = buildDuplicateCheckMessage(newLead, existingLeads);

        String response = aiService.chat(systemPrompt, userMessage);
        return parseDuplicateCheckResult(response);
    }

    /**
     * 检查客户是否与现有数据重复
     *
     * @param newCustomer 新客户信息
     * @param existingCustomers 现有客户列表
     * @return 查重结果
     */
    public DuplicateCheckResult checkCustomerDuplicate(JSONObject newCustomer, List<JSONObject> existingCustomers) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        if (existingCustomers == null || existingCustomers.isEmpty()) {
            return DuplicateCheckResult.noDuplicate();
        }

        String systemPrompt = buildDuplicateCheckPrompt("客户");
        String userMessage = buildDuplicateCheckMessage(newCustomer, existingCustomers);

        String response = aiService.chat(systemPrompt, userMessage);
        return parseDuplicateCheckResult(response);
    }

    /**
     * 检查联系人是否与现有数据重复
     *
     * @param newContact 新联系人信息
     * @param existingContacts 现有联系人列表
     * @return 查重结果
     */
    public DuplicateCheckResult checkContactDuplicate(JSONObject newContact, List<JSONObject> existingContacts) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        if (existingContacts == null || existingContacts.isEmpty()) {
            return DuplicateCheckResult.noDuplicate();
        }

        String systemPrompt = buildDuplicateCheckPrompt("联系人");
        String userMessage = buildDuplicateCheckMessage(newContact, existingContacts);

        String response = aiService.chat(systemPrompt, userMessage);
        return parseDuplicateCheckResult(response);
    }

    /**
     * 构建查重系统提示词
     */
    private String buildDuplicateCheckPrompt(String entityType) {
        return "你是一个 CRM 系统的数据查重助手。请分析新" + entityType + "与现有" + entityType + "列表的相似度。\n" +
                "判断标准：\n" +
                "1. 名称相同或高度相似（考虑简称、别名）\n" +
                "2. 电话号码相同\n" +
                "3. 邮箱地址相同\n" +
                "4. 公司名称+联系人姓名组合相同\n" +
                "\n" +
                "请以 JSON 格式返回分析结果：\n" +
                "{\n" +
                "  \"hasDuplicate\": true/false,\n" +
                "  \"duplicates\": [\n" +
                "    {\n" +
                "      \"id\": \"匹配记录的ID\",\n" +
                "      \"similarity\": 0.0-1.0之间的相似度分数,\n" +
                "      \"reason\": \"判断为重复的原因\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "\n" +
                "只返回 JSON，不要包含其他文字。相似度 >= 0.7 视为可能重复。";
    }

    /**
     * 构建查重消息
     */
    private String buildDuplicateCheckMessage(JSONObject newData, List<JSONObject> existingData) {
        StringBuilder sb = new StringBuilder();
        sb.append("新数据：\n").append(newData.toJSONString()).append("\n\n");
        sb.append("现有数据列表：\n");
        for (int i = 0; i < existingData.size(); i++) {
            sb.append(i + 1).append(". ").append(existingData.get(i).toJSONString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 解析查重结果
     */
    private DuplicateCheckResult parseDuplicateCheckResult(String response) {
        try {
            JSONObject json = parseJsonFromResponse(response);
            DuplicateCheckResult result = new DuplicateCheckResult();
            result.setHasDuplicate(json.getBooleanValue("hasDuplicate"));

            List<DuplicateItem> duplicates = new ArrayList<>();
            JSONArray arr = json.getJSONArray("duplicates");
            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject item = arr.getJSONObject(i);
                    DuplicateItem duplicate = new DuplicateItem();
                    duplicate.setId(item.getString("id"));
                    duplicate.setSimilarity(item.getDoubleValue("similarity"));
                    duplicate.setReason(item.getString("reason"));
                    duplicates.add(duplicate);
                }
            }
            result.setDuplicates(duplicates);

            return result;
        } catch (Exception e) {
            log.error("解析查重结果失败: {}", response, e);
            return DuplicateCheckResult.noDuplicate();
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
     * 查重结果
     */
    @Data
    public static class DuplicateCheckResult {
        /**
         * 是否存在重复
         */
        private boolean hasDuplicate;

        /**
         * 重复项列表
         */
        private List<DuplicateItem> duplicates = new ArrayList<>();

        /**
         * 创建无重复结果
         */
        public static DuplicateCheckResult noDuplicate() {
            DuplicateCheckResult result = new DuplicateCheckResult();
            result.setHasDuplicate(false);
            result.setDuplicates(new ArrayList<>());
            return result;
        }
    }

    /**
     * 重复项
     */
    @Data
    public static class DuplicateItem {
        /**
         * 匹配记录 ID
         */
        private String id;

        /**
         * 相似度 (0-1)
         */
        private double similarity;

        /**
         * 判断原因
         */
        private String reason;
    }
}
