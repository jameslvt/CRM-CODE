package com.crm.ai.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 智能创建服务
 * 通过 AI 解析自然语言，自动提取实体信息
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmartCreateService {

    private final AiService aiService;

    /**
     * 从自然语言描述中提取线索信息
     *
     * @param description 自然语言描述
     * @return 提取的线索信息 JSON
     */
    public JSONObject extractLeadInfo(String description) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        String systemPrompt = buildLeadExtractionPrompt();
        String response = aiService.chat(systemPrompt, description);

        return parseJsonResponse(response);
    }

    /**
     * 从自然语言描述中提取客户信息
     *
     * @param description 自然语言描述
     * @return 提取的客户信息 JSON
     */
    public JSONObject extractCustomerInfo(String description) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        String systemPrompt = buildCustomerExtractionPrompt();
        String response = aiService.chat(systemPrompt, description);

        return parseJsonResponse(response);
    }

    /**
     * 从自然语言描述中提取商机信息
     *
     * @param description 自然语言描述
     * @return 提取的商机信息 JSON
     */
    public JSONObject extractOpportunityInfo(String description) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        String systemPrompt = buildOpportunityExtractionPrompt();
        String response = aiService.chat(systemPrompt, description);

        return parseJsonResponse(response);
    }

    /**
     * 从自然语言描述中提取联系人信息
     *
     * @param description 自然语言描述
     * @return 提取的联系人信息 JSON
     */
    public JSONObject extractContactInfo(String description) {
        if (!aiService.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        String systemPrompt = buildContactExtractionPrompt();
        String response = aiService.chat(systemPrompt, description);

        return parseJsonResponse(response);
    }

    /**
     * 构建线索提取的系统提示词
     */
    private String buildLeadExtractionPrompt() {
        return "你是一个 CRM 系统的数据提取助手。请从用户的描述中提取线索信息，并以 JSON 格式返回。\n" +
                "需要提取的字段包括：\n" +
                "- name: 线索名称/公司名称\n" +
                "- contactName: 联系人姓名\n" +
                "- phone: 电话号码\n" +
                "- email: 邮箱地址\n" +
                "- source: 来源（如：网站、电话、展会、转介绍等）\n" +
                "- industry: 行业\n" +
                "- address: 地址\n" +
                "- remark: 备注信息\n" +
                "\n" +
                "请只返回 JSON 格式的数据，不要包含其他文字说明。如果某个字段无法从描述中提取，则不包含该字段。\n" +
                "示例输出：{\"name\":\"某某公司\",\"contactName\":\"张三\",\"phone\":\"13800138000\"}";
    }

    /**
     * 构建客户提取的系统提示词
     */
    private String buildCustomerExtractionPrompt() {
        return "你是一个 CRM 系统的数据提取助手。请从用户的描述中提取客户信息，并以 JSON 格式返回。\n" +
                "需要提取的字段包括：\n" +
                "- name: 客户名称/公司名称\n" +
                "- shortName: 简称\n" +
                "- industry: 行业\n" +
                "- scale: 规模（如：小型、中型、大型）\n" +
                "- source: 来源\n" +
                "- phone: 电话\n" +
                "- website: 网站\n" +
                "- address: 地址\n" +
                "- remark: 备注\n" +
                "\n" +
                "请只返回 JSON 格式的数据，不要包含其他文字说明。如果某个字段无法从描述中提取，则不包含该字段。";
    }

    /**
     * 构建商机提取的系统提示词
     */
    private String buildOpportunityExtractionPrompt() {
        return "你是一个 CRM 系统的数据提取助手。请从用户的描述中提取商机信息，并以 JSON 格式返回。\n" +
                "需要提取的字段包括：\n" +
                "- name: 商机名称\n" +
                "- customerName: 客户名称\n" +
                "- contactName: 联系人姓名\n" +
                "- amount: 预计金额（数字）\n" +
                "- expectedDate: 预计成交日期（格式：yyyy-MM-dd）\n" +
                "- source: 来源\n" +
                "- remark: 备注\n" +
                "\n" +
                "请只返回 JSON 格式的数据，不要包含其他文字说明。如果某个字段无法从描述中提取，则不包含该字段。";
    }

    /**
     * 构建联系人提取的系统提示词
     */
    private String buildContactExtractionPrompt() {
        return "你是一个 CRM 系统的数据提取助手。请从用户的描述中提取联系人信息，并以 JSON 格式返回。\n" +
                "需要提取的字段包括：\n" +
                "- name: 姓名\n" +
                "- customerName: 所属客户/公司名称\n" +
                "- position: 职位\n" +
                "- department: 部门\n" +
                "- phone: 手机号码\n" +
                "- telephone: 座机号码\n" +
                "- email: 邮箱\n" +
                "- wechat: 微信号\n" +
                "- isKeyContact: 是否关键联系人（true/false）\n" +
                "- remark: 备注\n" +
                "\n" +
                "请只返回 JSON 格式的数据，不要包含其他文字说明。如果某个字段无法从描述中提取，则不包含该字段。";
    }

    /**
     * 解析 AI 返回的 JSON 响应
     */
    private JSONObject parseJsonResponse(String response) {
        try {
            // 尝试直接解析
            return JSON.parseObject(response);
        } catch (Exception e) {
            // 尝试提取 JSON 部分
            int start = response.indexOf("{");
            int end = response.lastIndexOf("}");
            if (start >= 0 && end > start) {
                String jsonStr = response.substring(start, end + 1);
                try {
                    return JSON.parseObject(jsonStr);
                } catch (Exception ex) {
                    log.error("解析 AI 响应失败: {}", response, ex);
                    throw new BusinessException("AI 响应解析失败");
                }
            }
            log.error("AI 响应格式错误: {}", response);
            throw new BusinessException("AI 响应格式错误");
        }
    }
}
