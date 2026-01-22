package com.crm.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志查询DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class OperationLogQueryDTO {

    /**
     * 模块名称
     */
    private String module;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 状态: 0-失败, 1-成功
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 20;
}
