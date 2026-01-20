package com.crm.ai.client;

import com.alibaba.fastjson.JSON;
import com.crm.ai.config.AiProperties;
import com.crm.ai.dto.ChatCompletionRequest;
import com.crm.ai.dto.ChatCompletionResponse;
import com.crm.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * OpenAI 客户端封装
 * 提供与 OpenAI API 兼容接口的通信能力
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAIClient {

    private final AiProperties aiProperties;

    private OkHttpClient httpClient;

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    @PostConstruct
    public void init() {
        AiProperties.OpenAI openai = aiProperties.getOpenai();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(openai.getTimeout(), TimeUnit.SECONDS)
                .readTimeout(openai.getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(openai.getTimeout(), TimeUnit.SECONDS)
                .build();
    }

    /**
     * 发送 Chat Completion 请求
     *
     * @param systemPrompt 系统提示词
     * @param userMessage 用户消息
     * @return AI 响应内容
     */
    public String chat(String systemPrompt, String userMessage) {
        if (!aiProperties.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        List<ChatCompletionRequest.Message> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(new ChatCompletionRequest.Message("system", systemPrompt));
        }
        messages.add(new ChatCompletionRequest.Message("user", userMessage));

        return chatCompletion(messages);
    }

    /**
     * 发送 Chat Completion 请求（多轮对话）
     *
     * @param messages 消息列表
     * @return AI 响应内容
     */
    public String chatCompletion(List<ChatCompletionRequest.Message> messages) {
        if (!aiProperties.isEnabled()) {
            throw new BusinessException("AI 功能未启用");
        }

        AiProperties.OpenAI openai = aiProperties.getOpenai();

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(openai.getModel());
        request.setMessages(messages);
        request.setTemperature(openai.getTemperature());
        request.setMax_tokens(openai.getMaxTokens());

        String requestBody = JSON.toJSONString(request);
        String url = openai.getBaseUrl() + "/chat/completions";

        Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + openai.getApiKey())
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, JSON_MEDIA_TYPE))
                .build();

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                log.error("OpenAI API 请求失败: {} - {}", response.code(), errorBody);
                throw new BusinessException("AI 服务请求失败: " + response.code());
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            ChatCompletionResponse completionResponse = JSON.parseObject(responseBody, ChatCompletionResponse.class);

            if (completionResponse.getChoices() == null || completionResponse.getChoices().isEmpty()) {
                throw new BusinessException("AI 服务返回空响应");
            }

            String content = completionResponse.getChoices().get(0).getMessage().getContent();
            log.debug("OpenAI 响应: {}", content);

            return content;
        } catch (IOException e) {
            log.error("OpenAI API 请求异常", e);
            throw new BusinessException("AI 服务请求异常: " + e.getMessage());
        }
    }

    /**
     * 检查 AI 服务是否可用
     *
     * @return 是否可用
     */
    public boolean isAvailable() {
        if (!aiProperties.isEnabled()) {
            return false;
        }

        AiProperties.OpenAI openai = aiProperties.getOpenai();
        return openai.getApiKey() != null && !openai.getApiKey().isEmpty();
    }
}
