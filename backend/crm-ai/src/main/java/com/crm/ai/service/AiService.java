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
        return openAIClient.chat(null, message);
    }

    /**
     * 发送带系统提示的对话请求
     *
     * @param systemPrompt 系统提示词
     * @param message 用户消息
     * @return AI 响应
     */
    public String chat(String systemPrompt, String message) {
        return openAIClient.chat(systemPrompt, message);
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
