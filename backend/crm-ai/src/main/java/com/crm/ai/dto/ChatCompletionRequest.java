package com.crm.ai.dto;

import lombok.Data;

import java.util.List;

/**
 * OpenAI Chat Completion 请求 DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class ChatCompletionRequest {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 消息列表
     */
    private List<Message> messages;

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * 最大 Token 数
     */
    private Integer max_tokens;

    /**
     * 消息
     */
    @Data
    public static class Message {
        /**
         * 角色: system、user、assistant
         */
        private String role;

        /**
         * 内容
         */
        private String content;

        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
