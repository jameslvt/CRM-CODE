package com.crm.ai.controller;

import com.alibaba.fastjson.JSONObject;
import com.crm.ai.service.AiService;
import com.crm.ai.service.DuplicateCheckService;
import com.crm.ai.service.NLQueryService;
import com.crm.ai.service.SmartCreateService;
import com.crm.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * AI 功能控制器
 * 提供 AI 辅助功能的 REST API
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Api(tags = "AI 功能")
@Validated
public class AiController {

    private final AiService aiService;
    private final SmartCreateService smartCreateService;
    private final DuplicateCheckService duplicateCheckService;
    private final NLQueryService nlQueryService;

    /**
     * 获取 AI 服务状态
     */
    @GetMapping("/status")
    @ApiOperation("获取 AI 服务状态")
    public Result<AiService.AiStatusInfo> getStatus() {
        return Result.success(aiService.getStatus());
    }

    /**
     * AI 对话
     */
    @PostMapping("/chat")
    @ApiOperation("AI 对话")
    @PreAuthorize("hasAuthority('ai:chat')")
    public Result<String> chat(@RequestBody @Validated ChatRequest request) {
        String response = aiService.chat(request.getSystemPrompt(), request.getMessage());
        return Result.success(response);
    }

    /**
     * 智能提取线索信息
     */
    @PostMapping("/extract/lead")
    @ApiOperation("从自然语言提取线索信息")
    @PreAuthorize("hasAuthority('lead:create')")
    public Result<JSONObject> extractLeadInfo(@RequestBody @Validated ExtractRequest request) {
        JSONObject result = smartCreateService.extractLeadInfo(request.getDescription());
        return Result.success(result);
    }

    /**
     * 智能提取客户信息
     */
    @PostMapping("/extract/customer")
    @ApiOperation("从自然语言提取客户信息")
    @PreAuthorize("hasAuthority('customer:create')")
    public Result<JSONObject> extractCustomerInfo(@RequestBody @Validated ExtractRequest request) {
        JSONObject result = smartCreateService.extractCustomerInfo(request.getDescription());
        return Result.success(result);
    }

    /**
     * 智能提取商机信息
     */
    @PostMapping("/extract/opportunity")
    @ApiOperation("从自然语言提取商机信息")
    @PreAuthorize("hasAuthority('opportunity:create')")
    public Result<JSONObject> extractOpportunityInfo(@RequestBody @Validated ExtractRequest request) {
        JSONObject result = smartCreateService.extractOpportunityInfo(request.getDescription());
        return Result.success(result);
    }

    /**
     * 智能提取联系人信息
     */
    @PostMapping("/extract/contact")
    @ApiOperation("从自然语言提取联系人信息")
    @PreAuthorize("hasAuthority('contact:create')")
    public Result<JSONObject> extractContactInfo(@RequestBody @Validated ExtractRequest request) {
        JSONObject result = smartCreateService.extractContactInfo(request.getDescription());
        return Result.success(result);
    }

    /**
     * 线索查重
     */
    @PostMapping("/duplicate/lead")
    @ApiOperation("线索智能查重")
    @PreAuthorize("hasAuthority('lead:create')")
    public Result<DuplicateCheckService.DuplicateCheckResult> checkLeadDuplicate(
            @RequestBody @Validated DuplicateCheckRequest request) {
        DuplicateCheckService.DuplicateCheckResult result =
                duplicateCheckService.checkLeadDuplicate(request.getNewData(), request.getExistingData());
        return Result.success(result);
    }

    /**
     * 客户查重
     */
    @PostMapping("/duplicate/customer")
    @ApiOperation("客户智能查重")
    @PreAuthorize("hasAuthority('customer:create')")
    public Result<DuplicateCheckService.DuplicateCheckResult> checkCustomerDuplicate(
            @RequestBody @Validated DuplicateCheckRequest request) {
        DuplicateCheckService.DuplicateCheckResult result =
                duplicateCheckService.checkCustomerDuplicate(request.getNewData(), request.getExistingData());
        return Result.success(result);
    }

    /**
     * 联系人查重
     */
    @PostMapping("/duplicate/contact")
    @ApiOperation("联系人智能查重")
    @PreAuthorize("hasAuthority('contact:create')")
    public Result<DuplicateCheckService.DuplicateCheckResult> checkContactDuplicate(
            @RequestBody @Validated DuplicateCheckRequest request) {
        DuplicateCheckService.DuplicateCheckResult result =
                duplicateCheckService.checkContactDuplicate(request.getNewData(), request.getExistingData());
        return Result.success(result);
    }

    /**
     * 自然语言查询解析
     */
    @PostMapping("/query/parse")
    @ApiOperation("解析自然语言查询")
    @PreAuthorize("isAuthenticated()")
    public Result<NLQueryService.QueryCondition> parseQuery(@RequestBody @Validated NLQueryRequest request) {
        NLQueryService.QueryCondition condition =
                nlQueryService.parseQuery(request.getEntityType(), request.getQuery());
        return Result.success(condition);
    }

    /**
     * 对话请求
     */
    @Data
    public static class ChatRequest {
        /**
         * 系统提示词（可选）
         */
        private String systemPrompt;

        /**
         * 用户消息
         */
        @NotBlank(message = "消息不能为空")
        private String message;
    }

    /**
     * 信息提取请求
     */
    @Data
    public static class ExtractRequest {
        /**
         * 自然语言描述
         */
        @NotBlank(message = "描述不能为空")
        private String description;
    }

    /**
     * 查重请求
     */
    @Data
    public static class DuplicateCheckRequest {
        /**
         * 新数据
         */
        private JSONObject newData;

        /**
         * 现有数据列表
         */
        private List<JSONObject> existingData;
    }

    /**
     * 自然语言查询请求
     */
    @Data
    public static class NLQueryRequest {
        /**
         * 实体类型: lead, customer, opportunity, contract
         */
        @NotBlank(message = "实体类型不能为空")
        private String entityType;

        /**
         * 自然语言查询
         */
        @NotBlank(message = "查询内容不能为空")
        private String query;
    }
}
