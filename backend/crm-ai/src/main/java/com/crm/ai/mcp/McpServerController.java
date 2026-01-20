package com.crm.ai.mcp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crm.ai.service.AiService;
import com.crm.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MCP Server 端点
 * 提供 Model Context Protocol 标准接口
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
@Api(tags = "MCP Server")
public class McpServerController {

    private final AiService aiService;
    private final McpToolExecutor toolExecutor;

    /**
     * 获取服务器信息
     */
    @GetMapping("/info")
    @ApiOperation("获取 MCP 服务器信息")
    public Result<ServerInfo> getServerInfo() {
        ServerInfo info = new ServerInfo();
        info.setName("CRM MCP Server");
        info.setVersion("1.0.0");
        info.setProtocolVersion("2024-11-05");
        info.setCapabilities(new String[]{"tools"});
        return Result.success(info);
    }

    /**
     * 列出可用工具
     */
    @GetMapping("/tools")
    @ApiOperation("列出可用工具")
    public Result<List<CrmTools.ToolDefinition>> listTools() {
        return Result.success(CrmTools.getToolDefinitions());
    }

    /**
     * 执行工具调用
     */
    @PostMapping("/tools/call")
    @ApiOperation("执行工具调用")
    public Result<ToolCallResult> callTool(@RequestBody ToolCallRequest request) {
        log.info("MCP 工具调用: {} - {}", request.getName(), request.getArguments());

        try {
            Object result = toolExecutor.execute(request.getName(), request.getArguments());
            ToolCallResult callResult = new ToolCallResult();
            callResult.setSuccess(true);
            callResult.setResult(result);
            return Result.success(callResult);
        } catch (Exception e) {
            log.error("MCP 工具调用失败: {}", request.getName(), e);
            ToolCallResult callResult = new ToolCallResult();
            callResult.setSuccess(false);
            callResult.setError(e.getMessage());
            return Result.success(callResult);
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @ApiOperation("健康检查")
    public Result<HealthStatus> healthCheck() {
        HealthStatus status = new HealthStatus();
        status.setStatus("ok");
        status.setAiEnabled(aiService.isEnabled());
        status.setAiAvailable(aiService.isAvailable());
        return Result.success(status);
    }

    /**
     * 服务器信息
     */
    @Data
    public static class ServerInfo {
        private String name;
        private String version;
        private String protocolVersion;
        private String[] capabilities;
    }

    /**
     * 工具调用请求
     */
    @Data
    public static class ToolCallRequest {
        /**
         * 工具名称
         */
        private String name;

        /**
         * 调用参数
         */
        private JSONObject arguments;
    }

    /**
     * 工具调用结果
     */
    @Data
    public static class ToolCallResult {
        /**
         * 是否成功
         */
        private boolean success;

        /**
         * 执行结果
         */
        private Object result;

        /**
         * 错误信息
         */
        private String error;
    }

    /**
     * 健康状态
     */
    @Data
    public static class HealthStatus {
        private String status;
        private boolean aiEnabled;
        private boolean aiAvailable;
    }
}
