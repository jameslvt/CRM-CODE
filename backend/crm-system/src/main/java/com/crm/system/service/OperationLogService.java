package com.crm.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.result.PageResult;
import com.crm.system.dto.OperationLogQueryDTO;
import com.crm.system.entity.OperationLog;
import com.crm.system.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 操作日志服务
 * 提供操作日志的记录和查询功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    /**
     * 异步保存操作日志
     * 使用异步方式避免影响主业务性能
     *
     * @param operationLog 操作日志
     */
    @Async
    public void saveAsync(OperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    /**
     * 同步保存操作日志
     *
     * @param operationLog 操作日志
     */
    public void save(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }

    /**
     * 分页查询操作日志
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public PageResult<OperationLog> page(OperationLogQueryDTO queryDTO) {
        Page<OperationLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        // 模块筛选
        if (StringUtils.hasText(queryDTO.getModule())) {
            wrapper.like(OperationLog::getModule, queryDTO.getModule());
        }

        // 操作类型筛选
        if (StringUtils.hasText(queryDTO.getOperation())) {
            wrapper.like(OperationLog::getOperation, queryDTO.getOperation());
        }

        // 用户名筛选
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(OperationLog::getUsername, queryDTO.getUsername());
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(OperationLog::getStatus, queryDTO.getStatus());
        }

        // 时间范围筛选
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(OperationLog::getCreateTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(OperationLog::getCreateTime, queryDTO.getEndTime());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(OperationLog::getCreateTime);

        Page<OperationLog> result = operationLogMapper.selectPage(page, wrapper);

        return PageResult.of(result.getRecords(), result.getTotal(), (long) queryDTO.getPageNum(), (long) queryDTO.getPageSize());
    }

    /**
     * 根据ID查询操作日志详情
     *
     * @param id 日志ID
     * @return 操作日志
     */
    public OperationLog getById(Long id) {
        return operationLogMapper.selectById(id);
    }

    /**
     * 清理指定天数之前的日志
     *
     * @param days 天数
     * @return 删除的记录数
     */
    public int cleanBeforeDays(int days) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(OperationLog::getCreateTime, java.time.LocalDateTime.now().minusDays(days));
        return operationLogMapper.delete(wrapper);
    }
}
