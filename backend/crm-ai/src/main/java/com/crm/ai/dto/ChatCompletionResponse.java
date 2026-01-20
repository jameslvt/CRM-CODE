package com.crm.ai.dto;

import lombok.Data;

import java.util.List;

/**
 * OpenAI Chat Completion 响应 DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class ChatCompletionResponse {

    /**
     * 响应 ID
     */
    private String id;

    /**
     * 对象类型
     */
    private String object;

    /**
     * 创建时间戳
     */
    private Long created;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 选择列表
     */
    private List<Choice> choices;

    /**
     * 使用情况
     */
    private Usage usage;

    /**
     * 选择
     */
    @Data
    public static class Choice {
        /**
         * 索引
         */
        private Integer index;

        /**
         * 消息
         */
        private Message message;

        /**
         * 结束原因
         */
        private String finish_reason;
    }

    /**
     * 消息
     */
    @Data
    public static class Message {
        /**
         * 角色
         */
        private String role;

        /**
         * 内容
         */
        private String content;
    }

    /**
     * 使用情况
     */
    @Data
    public static class Usage {
        /**
         * 提示 Token 数
         */
        private Integer prompt_tokens;

        /**
         * 完成 Token 数
         */
        private Integer completion_tokens;

        /**
         * 总 Token 数
         */
        private Integer total_tokens;
    }
}
