package com.crm.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 配置属性
 * 配置 OpenAI API 相关参数
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "crm.ai")
public class AiProperties {

    /**
     * 是否启用 AI 功能
     */
    private boolean enabled = false;

    /**
     * OpenAI API 配置
     */
    private OpenAI openai = new OpenAI();

    /**
     * OpenAI 配置
     */
    @Data
    public static class OpenAI {
        /**
         * API 基础 URL
         * 默认为 OpenAI 官方地址，可配置为兼容的第三方服务
         */
        private String baseUrl = "https://api.openai.com/v1";

        /**
         * API Key
         */
        private String apiKey;

        /**
         * 默认模型
         */
        private String model = "gpt-3.5-turbo";

        /**
         * 请求超时时间（秒）
         */
        private int timeout = 60;

        /**
         * 最大 Token 数
         */
        private int maxTokens = 2000;

        /**
         * 温度参数（0-2，越高越随机）
         */
        private double temperature = 0.7;
    }
}
