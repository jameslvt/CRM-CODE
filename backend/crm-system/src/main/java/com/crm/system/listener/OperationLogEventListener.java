package com.crm.system.listener;

import com.crm.common.aspect.OperationLogEvent;
import com.crm.system.entity.OperationLog;
import com.crm.system.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 操作日志事件监听器
 * 监听操作日志事件并异步保存到数据库
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogEventListener {

    private final OperationLogService operationLogService;

    /**
     * 异步处理操作日志事件
     *
     * @param event 操作日志事件
     */
    @Async
    @EventListener
    public void handleOperationLogEvent(OperationLogEvent event) {
        try {
            // 将事件转换为实体
            OperationLog operationLog = new OperationLog();
            operationLog.setModule(event.getModule());
            operationLog.setOperation(event.getOperation());
            operationLog.setMethod(event.getMethod());
            operationLog.setRequestUrl(event.getRequestUrl());
            operationLog.setRequestParams(event.getRequestParams());
            operationLog.setResponseResult(event.getResponseResult());
            operationLog.setUserId(event.getUserId());
            operationLog.setUsername(event.getUsername());
            operationLog.setIp(event.getIp());
            operationLog.setDuration(event.getDuration());
            operationLog.setStatus(event.getStatus());
            operationLog.setErrorMsg(event.getErrorMsg());
            operationLog.setCreateTime(event.getCreateTime());

            // 保存日志
            operationLogService.save(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }
}
