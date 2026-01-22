package com.crm.system.controller;

import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.system.dto.OperationLogQueryDTO;
import com.crm.system.entity.OperationLog;
import com.crm.system.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 操作日志控制器
 * 提供操作日志查询接口
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "操作日志")
@RestController
@RequestMapping("/api/system/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @ApiOperation("分页查询操作日志")
    @GetMapping
    @PreAuthorize("hasAuthority('system:log:list')")
    public Result<PageResult<OperationLog>> page(
            @ApiParam("模块名称") @RequestParam(required = false) String module,
            @ApiParam("操作类型") @RequestParam(required = false) String operation,
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @ApiParam("结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "20") Integer pageSize) {

        OperationLogQueryDTO queryDTO = new OperationLogQueryDTO();
        queryDTO.setModule(module);
        queryDTO.setOperation(operation);
        queryDTO.setUsername(username);
        queryDTO.setStatus(status);
        queryDTO.setStartTime(startTime);
        queryDTO.setEndTime(endTime);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);

        PageResult<OperationLog> result = operationLogService.page(queryDTO);
        return Result.success(result);
    }

    /**
     * 查询操作日志详情
     */
    @ApiOperation("查询操作日志详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:log:list')")
    public Result<OperationLog> getById(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        return Result.success(log);
    }

    /**
     * 清理历史日志
     */
    @ApiOperation("清理历史日志")
    @DeleteMapping("/clean")
    @PreAuthorize("hasAuthority('system:log:clean')")
    public Result<Integer> clean(
            @ApiParam("保留天数") @RequestParam(defaultValue = "90") Integer days) {
        int count = operationLogService.cleanBeforeDays(days);
        return Result.success(count);
    }
}
